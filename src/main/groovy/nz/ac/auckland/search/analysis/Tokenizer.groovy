package nz.ac.auckland.search.analysis

import nz.ac.auckland.search.VariableParameter


public @interface Tokenizer {
    String name()
    String type() default ''
    String version() default ''
	String[] properties() default[] // an array of "key=value" strings. alternative to params
    VariableParameter[] params() default[]
}
