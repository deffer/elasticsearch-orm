package nz.ac.auckland.search.mapping

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * This is configuration to be used during index auto-creation
 *   for putting document's fields mapping.
 *
 * Defines mapping of one field (boost, analyze options, etc)ю
 *
 * This annotation require presence of IndexedField (or IndexedObject) on the same field (method)
 *   to get the field name from it.
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD, ElementType.FIELD])
public @interface MappingInfo {
    boolean enabled() default true // used for fields like _all and _source

    boolean store() default true
    IndexInstruction index() default IndexInstruction.ANALYZED
    boolean includeInAll() default true // not valid when used to describe _all/_source fields
    double boost() default 1.0

    // In elastic it defaults to false for analyzed fields, and to true for not_analyzed fields.
    // For consistency i am making it default to false
    boolean omitNorms() default false

    // Defaults to positions for analyzed fields, and to docs for not_analyzed fields. Since 0.20.
    IndexOptions indexOptions() default IndexOptions.DEFAULT

    /**
     * Used once during index creation and document mapping definitions. Should return a name
     *   of the index analyzer for this field. Analyzer with given name should be defined
     *   in IndexedDocument annotation (doesn't have to be in the same document as long as the other
     *   document is in the same index)
     * @return name of index analyzer
     */
    String analyzerName() default ''
    String searchAnalyzerName() default ''

	/**
	 * Only valid for object-type fields.
	 *
	 * Non-dynamic object will not index 'dynamic' fields (fields that were not defined on the moment of index creation)
	 *
	 * Set to false if this object contains varying fields and is not indexed (for instance Map);
	 *   or use StoredObject instead.
	 *
	 * Set to strict if no dynamic fields are allowed. The exception will be thrown when attempt to save object
	 *   with 'new' fields.
	 */
	DynamicNature dynamic() default DynamicNature.TRUE
}
