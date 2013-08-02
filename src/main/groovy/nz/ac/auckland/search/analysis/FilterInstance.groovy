package nz.ac.auckland.search.analysis

import nz.ac.auckland.search.VariableParameter


public @interface FilterInstance {
    String name()
    String type() default ''
    String version() default ''
    VariableParameter[] params() default[]
}
