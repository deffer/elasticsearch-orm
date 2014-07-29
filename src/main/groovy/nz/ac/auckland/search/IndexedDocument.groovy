package nz.ac.auckland.search

import nz.ac.auckland.search.analysis.Analyzer
import nz.ac.auckland.search.analysis.Filter
import nz.ac.auckland.search.mapping.DynamicNature
import nz.ac.auckland.search.mapping.MappingInfo

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IndexedDocument {
	String indexDocumentName()

	// http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis.html

	// Should only be defined once per index
	Analyzer[] analyzers() default []
	Filter[] filters() default []
	int numberOfShards() default 5

	/**
	 * Non-dynamic object will not index 'dynamic' fields (fields that were not defined on the moment of index creation)
	 *
	 * Set to false if this object contains varying fields and is not indexed (for instance Map);
	 *   or use StoredObject instead.
	 *
	 * Set to strict if no dynamic fields are allowed. The exception will be thrown when attempt to save object
	 *   with 'new' fields.
	 */
	DynamicNature dynamic() default DynamicNature.TRUE

	MappingInfo fieldAll() default @MappingInfo(enabled = true)
	MappingInfo fieldSource() default @MappingInfo(enabled = true)
	MappingInfo fieldTimestamp() default @MappingInfo(enabled = true)
}


