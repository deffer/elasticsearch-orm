package nz.ac.auckland.search.analysis

import nz.ac.auckland.search.VariableParameter


/**
 * Describes and analyzer to be used in the index.
 *
 * Analyzers are uniquely identified by their name. If you have more than one
 *   document type in your index, you can put you Analyzer into any of them.
 */
public @interface Analyzer {

    // http://www.elasticsearch.org/guide/reference/index-modules/analysis/
    // https://github.com/elasticsearch/elasticsearch-analysis-phonetic

    String name()
    String type() default ''
    String version() default ''
	String[] properties() default[] // an array of "key=value" strings. alternative to params
    VariableParameter[] params() default[]

    String tokenizerName() default ''
    String[] filterNames() default []
}
