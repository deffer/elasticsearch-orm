package nz.ac.auckland.search.mapping


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
}
