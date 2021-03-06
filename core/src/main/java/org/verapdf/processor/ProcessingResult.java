package org.verapdf.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.verapdf.processor.config.Config;

/**
 * Instance of this class contains result of
 * {@link org.verapdf.processor.ProcessorImpl#validate(InputStream, ItemDetails, Config, OutputStream)}
 * work.
 *
 * @author Sergey Shemyakov
 */
public class ProcessingResult {

	private ProcessingResult.ValidationSummary validationSummary;
	private ProcessingResult.MetadataFixingSummary metadataFixerSummary;
	private ProcessingResult.FeaturesSummary featuresSummary;
	private ProcessingResult.ReportSummary reportSummary;

	private List<String> errorMessages = new ArrayList<>();

	ProcessingResult(Config config) {
		this.validationSummary = config.getProcessingType().isValidating() ?
				ValidationSummary.FILE_VALID : ValidationSummary.VALIDATION_DISABLED;
		this.metadataFixerSummary = config.isFixMetadata() ?
				MetadataFixingSummary.FIXING_SUCCEED : MetadataFixingSummary.FIXING_DISABLED;
		this.featuresSummary = config.getProcessingType().isFeatures() ?
				FeaturesSummary.FEATURES_SUCCEED : FeaturesSummary.FEATURES_DISABLED;
		this.reportSummary = ReportSummary.REPORT_SUCCEED;
	}

	/**
	 * @return summary of validation
	 */
	public ValidationSummary getValidationSummary() {
		return validationSummary;
	}

	/**
	 * @return summary of metadata fixing
	 */
	public MetadataFixingSummary getMetadataFixerSummary() {
		return metadataFixerSummary;
	}

	/**
	 * @return summary of feature extracting
	 */
	public FeaturesSummary getFeaturesSummary() {
		return featuresSummary;
	}

	/**
	 * @return summary of report generating
	 */
	public ReportSummary getReportSummary() {
		return reportSummary;
	}

	/**
	 * @return List of messages of exceptions that occurred during
	 * {@link org.verapdf.processor.ProcessorImpl#validate(InputStream, ItemDetails, Config, OutputStream)}
	 */
	public List<String> getErrorMessages() {
		return Collections.unmodifiableList(errorMessages);
	}

	void setValidationSummary(ValidationSummary validationSummary) {
		this.validationSummary = validationSummary;
	}

	void setReportSummary(ReportSummary reportSummary) {
		this.reportSummary = reportSummary;
	}

	void setMetadataFixerSummary(MetadataFixingSummary metadataFixerSummary) {
		this.metadataFixerSummary = metadataFixerSummary;
	}

	void setFeaturesSummary(FeaturesSummary featuresSummary) {
		this.featuresSummary = featuresSummary;
	}

	void addErrorMessage(String message) {
		errorMessages.add(message);
	}

	public enum ValidationSummary {
		FILE_VALID,
		FILE_NOT_VALID,
		ERROR_IN_VALIDATION,
		VALIDATION_DISABLED
	}

	public enum MetadataFixingSummary {
		FIXING_SUCCEED,
		ERROR_IN_FIXING,
		FIXING_DISABLED
	}

	public enum FeaturesSummary {
		FEATURES_SUCCEED,
		ERROR_IN_FEATURES,
		FEATURES_DISABLED
	}

	public enum ReportSummary {
		REPORT_SUCCEED,
		ERROR_IN_REPORT
	}
}
