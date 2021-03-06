package me.retrodaredevil.solarthing.program;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.solarthing.config.message.MessageEventNode;
import me.retrodaredevil.solarthing.config.options.MessageSenderProgramOptions;
import me.retrodaredevil.solarthing.couchdb.CouchDbQueryHandler;
import me.retrodaredevil.solarthing.couchdb.SolarThingCouchDb;
import me.retrodaredevil.solarthing.message.MessageSender;
import me.retrodaredevil.solarthing.message.MessageSenderMultiplexer;
import me.retrodaredevil.solarthing.misc.device.DevicePacket;
import me.retrodaredevil.solarthing.misc.error.ErrorPacket;
import me.retrodaredevil.solarthing.packets.Packet;
import me.retrodaredevil.solarthing.packets.collection.FragmentedPacketGroup;
import me.retrodaredevil.solarthing.packets.collection.parsing.ObjectMapperPacketConverter;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketGroupParser;
import me.retrodaredevil.solarthing.packets.collection.parsing.PacketParserMultiplexer;
import me.retrodaredevil.solarthing.packets.collection.parsing.SimplePacketGroupParser;
import me.retrodaredevil.solarthing.packets.handling.PacketHandleException;
import me.retrodaredevil.solarthing.packets.instance.InstancePacket;
import me.retrodaredevil.solarthing.solar.SolarStatusPacket;
import me.retrodaredevil.solarthing.solar.common.BatteryVoltage;
import me.retrodaredevil.solarthing.solar.extra.SolarExtraPacket;
import me.retrodaredevil.solarthing.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MessageSenderMain {
	private MessageSenderMain() { throw new UnsupportedOperationException(); }

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderMain.class);
	private static final ObjectMapper CONFIG_MAPPER = JacksonUtil.defaultMapper();
	private static final ObjectMapper PARSE_MAPPER = JacksonUtil.lenientMapper(JacksonUtil.defaultMapper());

	private static Map<String, MessageSender> getMessageSenderMap(MessageSenderProgramOptions options) throws IOException {
		Map<String, MessageSender> senderMap = new HashMap<>();
		for (Map.Entry<String, File> entry : options.getMessageSenderFileMap().entrySet()) {
			String key = entry.getKey();
			File file = entry.getValue();
			MessageSender sender = CONFIG_MAPPER.readValue(file, MessageSender.class);
			senderMap.put(key, sender);
		}
		return senderMap;
	}

	public static int startMessageSender(MessageSenderProgramOptions options) {
		CouchDbQueryHandler queryHandler = SolarMain.createCouchDbQueryHandler(options);
		PacketGroupParser statusParser = new SimplePacketGroupParser(new PacketParserMultiplexer(Arrays.asList(
				new ObjectMapperPacketConverter(PARSE_MAPPER, SolarStatusPacket.class),
				new ObjectMapperPacketConverter(PARSE_MAPPER, SolarExtraPacket.class),
				new ObjectMapperPacketConverter(PARSE_MAPPER, DevicePacket.class),
				new ObjectMapperPacketConverter(PARSE_MAPPER, ErrorPacket.class),
				new ObjectMapperPacketConverter(PARSE_MAPPER, InstancePacket.class)
		), PacketParserMultiplexer.LenientType.FAIL_WHEN_UNHANDLED_WITH_EXCEPTION));
		final Map<String, MessageSender> messageSenderMap;
		try {
			messageSenderMap = getMessageSenderMap(options);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
//		senders.add(message -> LOGGER.debug("Sending message: {}", message));
//		MessageSender sender = new MessageSenderMultiplexer(senders);
		List<MessageEventNode> messageEventNodes = options.getMessageEventNodes();

		FragmentedPacketGroup last = null;
		while (!Thread.currentThread().isInterrupted()) {
			List<ObjectNode> statusPacketNodes = null;
			try {
				long now = System.currentTimeMillis();
				statusPacketNodes = queryHandler.query(SolarThingCouchDb.createMillisView()
						.startKey(now - 5 * 60 * 1000)
						.endKey(now));
				LOGGER.debug("Got packets");
			} catch (PacketHandleException e) {
				LOGGER.error("Couldn't get status packets", e);
			}
			if(statusPacketNodes != null) {
				List<FragmentedPacketGroup> packetGroups = PacketUtil.getPacketGroups(options.getSourceId(), options.getDefaultInstanceOptions(), statusPacketNodes, statusParser);
				if (packetGroups != null) {
					FragmentedPacketGroup packetGroup = packetGroups.get(packetGroups.size() - 1);
					if (last != null) {
						for (MessageEventNode messageEventNode : messageEventNodes) {
							List<MessageSender> messageSenders = new ArrayList<>();
							for (String senderName : messageEventNode.getSendTo()) {
								MessageSender sender = messageSenderMap.get(senderName);
								if (sender == null) {
									throw new IllegalArgumentException("senderName: " + senderName + " is not defined!");
								}
								messageSenders.add(sender);
							}
							MessageSender sender = new MessageSenderMultiplexer(messageSenders);
							messageEventNode.getMessageEvent().run(sender, last, packetGroup);
						}
					}
					last = packetGroup;
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(ex);
			}
		}
		return 0;
	}
}
