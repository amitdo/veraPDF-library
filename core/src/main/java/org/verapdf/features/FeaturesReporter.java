package org.verapdf.features;

import org.verapdf.core.FeatureParsingException;
import org.verapdf.features.config.FeaturesConfig;
import org.verapdf.features.tools.FeatureTreeNode;
import org.verapdf.features.tools.FeaturesCollection;

import java.util.*;

/**
 * Features reporter
 *
 * @author Maksim Bezrukov
 */
public class FeaturesReporter {

	public static final String CUSTOM_FEATURES_ROOT_NODE_NAME = "customFeatures";

	private static Map<FeaturesObjectTypesEnum, List<FeaturesExtractor>> featuresExtractors = new HashMap<>();

	private final FeaturesCollection collection;
	private final FeaturesConfig config;

	/**
	 * Creates new FeaturesReporter
	 */
	public FeaturesReporter(FeaturesConfig config, List<FeaturesExtractor> extractors) {
		if (extractors == null) {
			throw new IllegalArgumentException("Extractors argument shall not be null");
		}
		if (config == null) {
			throw new IllegalArgumentException("Config argument shall not be null");
		}
		this.config = config;
		this.collection = new FeaturesCollection();
		for (FeaturesExtractor extractor : extractors) {
			registerFeaturesExtractor(extractor);
		}
	}

	public FeaturesReporter(FeaturesConfig config) {
		this(config, Collections.<FeaturesExtractor>emptyList());
	}

	/**
	 * Registers feature extractor for features object type
	 *
	 * @param extractor object for extract custom features
	 */
	static void registerFeaturesExtractor(FeaturesExtractor extractor) {
		if (featuresExtractors.get(extractor.getType()) == null) {
			featuresExtractors.put(extractor.getType(), new ArrayList<FeaturesExtractor>());
		}

		featuresExtractors.get(extractor.getType()).add(extractor);
	}

	/**
	 * Reports feature object for feature report
	 *
	 * @param obj object for reporting
	 */
	public void report(IFeaturesObject obj) {
		if (config.isFeaturesEnabledForType(obj.getType())) {
			try {
				FeatureTreeNode root = obj.reportFeatures(this.collection);
				if (featuresExtractors.get(obj.getType()) != null) {
					FeaturesData objData = obj.getData();
					if (objData != null) {
						FeatureTreeNode custom = FeatureTreeNode.createChildNode(CUSTOM_FEATURES_ROOT_NODE_NAME, root);
						for (FeaturesExtractor ext : featuresExtractors.get(obj.getType())) {
							List<FeatureTreeNode> cust = ext.getFeatures(objData);
							if (cust != null && !cust.isEmpty()) {
								FeatureTreeNode custRoot = FeatureTreeNode.createChildNode("pluginFeatures", custom);
								FeaturesExtractor.ExtractorDetails details = ext.getDetails();
								if (!details.getName().isEmpty()) {
									custRoot.setAttribute("name", details.getName());
								}
								if (!details.getVersion().isEmpty()) {
									custRoot.setAttribute("version", details.getVersion());
								}
								if (!details.getDescription().isEmpty()) {
									custRoot.setAttribute("description", details.getDescription());
								}
								for (FeatureTreeNode ftn : cust) {
									if (ftn != null) {
										custRoot.addChild(ftn);
									}
								}
							}
						}
					}
				}

			} catch (FeatureParsingException ignore) {
				// The method logic should ensure this never happens, so if it does
				// it's catastrophic. We'll throw an IllegalStateException with this
				// as a cause. The only time it's ignored is when the unthinkable
				// happens
				throw new IllegalStateException(
						"FeaturesReporter.report() illegal state.", ignore);
				// This exception occurs when wrong node creates for feature tree.
				// The logic of the method guarantees this doesn't occur.
			}
		}
	}

	/**
	 * @return collection of featurereport
	 */
	public FeaturesCollection getCollection() {
		return this.collection;
	}
}
