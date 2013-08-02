package nz.ac.auckland.search.analysis

import nz.ac.auckland.search.VariableParameter


public @interface AnalyzerInstance {

    // http://www.elasticsearch.org/guide/reference/index-modules/analysis/
    // https://github.com/elasticsearch/elasticsearch-analysis-phonetic

    String name()
    String type() default ''
    String version() default ''
    VariableParameter[] params() default[]

    String tokenizerName() default ''
    String[] filterNames() default []
}
