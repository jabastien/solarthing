package me.retrodaredevil.solarthing.pvoutput.data;

import me.retrodaredevil.solarthing.pvoutput.SimpleDate;
import me.retrodaredevil.solarthing.pvoutput.SimpleTime;
import me.retrodaredevil.solarthing.pvoutput.WeatherCondition;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unused")
public class AddOutputParametersBuilder implements AddOutputParameters {
	private final SimpleDate date;
	private Number generated;
	private Number exported;
	private Number peakPower;
	private SimpleTime peakTime;
	private String condition;
	private Float minimumTemperatureCelsius;
	private Float maximumTemperatureCelsius;
	private String comments;
	private Number importPeak;
	private Number importOffPeak;
	private Number importShoulder;
	private Number importHighShoulder;
	private Number consumption;

	public AddOutputParametersBuilder(SimpleDate date) {
		this.date = requireNonNull(date);
	}

	public AddOutputParameters build(){
		return new ImmutableAddOutputParameters(date, generated, exported, peakPower, peakTime, condition, minimumTemperatureCelsius, maximumTemperatureCelsius, comments, importPeak, importOffPeak, importShoulder, importHighShoulder, consumption);
	}

	@Override
	public SimpleDate getOutputDate() {
		return date;
	}

	public AddOutputParametersBuilder setGenerated(Number generated) {
		this.generated = generated;
		return this;
	}

	@Override
	public Number getGenerated() {
		return generated;
	}

	public AddOutputParametersBuilder setExported(Number exported) {
		this.exported = exported;
		return this;
	}

	@Override
	public Number getExported() {
		return exported;
	}

	public AddOutputParametersBuilder setPeakPower(Number peakPower) {
		this.peakPower = peakPower;
		return this;
	}

	@Override
	public Number getPeakPower() {
		return peakPower;
	}

	public AddOutputParametersBuilder setPeakTime(SimpleTime peakTime) {
		this.peakTime = peakTime;
		return this;
	}

	@Override
	public SimpleTime getPeakTime() {
		return peakTime;
	}

	public AddOutputParametersBuilder setConditionValue(String condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public String getConditionValue() {
		return condition;
	}

	public AddOutputParametersBuilder setCondition(WeatherCondition condition){
		this.condition = condition.toPVOutputString();
		return this;
	}

	public AddOutputParametersBuilder setMinimumTemperatureCelsius(Float minimumTemperatureCelsius) {
		this.minimumTemperatureCelsius = minimumTemperatureCelsius;
		return this;
	}

	@Override
	public Float getMinimumTemperatureCelsius() {
		return minimumTemperatureCelsius;
	}

	public AddOutputParametersBuilder setMaximumTemperatureCelsius(Float maximumTemperatureCelsius) {
		this.maximumTemperatureCelsius = maximumTemperatureCelsius;
		return this;
	}

	@Override
	public Float getMaximumTemperatureCelsius() {
		return maximumTemperatureCelsius;
	}

	/**
	 * @param comments The comments string. It is recommended to be less than 30 characters
	 */
	public AddOutputParametersBuilder setComments(String comments) {
		this.comments = comments;
		return this;
	}

	@Override
	public String getComments() {
		return comments;
	}

	public AddOutputParametersBuilder setImportPeak(Number importPeak) {
		this.importPeak = importPeak;
		return this;
	}

	@Override
	public Number getImportPeak() {
		return importPeak;
	}

	public AddOutputParametersBuilder setImportOffPeak(Number importOffPeak) {
		this.importOffPeak = importOffPeak;
		return this;
	}

	@Override
	public Number getImportOffPeak() {
		return importOffPeak;
	}

	public AddOutputParametersBuilder setImportShoulder(Number importShoulder) {
		this.importShoulder = importShoulder;
		return this;
	}

	@Override
	public Number getImportShoulder() {
		return importShoulder;
	}

	public AddOutputParametersBuilder setImportHighShoulder(Number importHighShoulder) {
		this.importHighShoulder = importHighShoulder;
		return this;
	}

	@Override
	public Number getImportHighShoulder() {
		return importHighShoulder;
	}

	public AddOutputParametersBuilder setConsumption(Number consumption) {
		this.consumption = consumption;
		return this;
	}

	@Override
	public Number getConsumption() {
		return consumption;
	}

	private static final class ImmutableAddOutputParameters implements AddOutputParameters {
		private final SimpleDate date;
		private final Number generated;
		private final Number exported;
		private final Number peakPower;
		private final SimpleTime peakTime;
		private final String condition;
		private final Float minimumTemperatureCelsius;
		private final Float maximumTemperatureCelsius;
		private final String comments;
		private final Number importPeak;
		private final Number importOffPeak;
		private final Number importShoulder;
		private final Number importHighShoulder;
		private final Number consumption;

		private ImmutableAddOutputParameters(SimpleDate date, Number generated, Number exported, Number peakPower, SimpleTime peakTime, String condition, Float minimumTemperatureCelsius, Float maximumTemperatureCelsius, String comments, Number importPeak, Number importOffPeak, Number importShoulder, Number importHighShoulder, Number consumption) {
			this.date = requireNonNull(date);
			this.generated = generated;
			this.exported = exported;
			this.peakPower = peakPower;
			this.peakTime = peakTime;
			this.condition = condition;
			this.minimumTemperatureCelsius = minimumTemperatureCelsius;
			this.maximumTemperatureCelsius = maximumTemperatureCelsius;
			this.comments = comments;
			this.importPeak = importPeak;
			this.importOffPeak = importOffPeak;
			this.importShoulder = importShoulder;
			this.importHighShoulder = importHighShoulder;
			this.consumption = consumption;
		}

		@Override public SimpleDate getOutputDate() { return date; }
		@Override public Number getGenerated() { return generated; }
		@Override public Number getExported() { return exported; }
		@Override public Number getPeakPower() { return peakPower; }
		@Override public SimpleTime getPeakTime() { return peakTime; }
		@Override public String getConditionValue() { return condition; }
		@Override public Float getMinimumTemperatureCelsius() { return minimumTemperatureCelsius; }
		@Override public Float getMaximumTemperatureCelsius() { return maximumTemperatureCelsius; }

		@Override public String getComments() { return comments; }
		@Override public Number getImportPeak() { return importPeak; }
		@Override public Number getImportOffPeak() { return importOffPeak; }
		@Override public Number getImportShoulder() { return importShoulder; }
		@Override public Number getImportHighShoulder() { return importHighShoulder; }
		@Override public Number getConsumption() { return consumption; }
	}
}
