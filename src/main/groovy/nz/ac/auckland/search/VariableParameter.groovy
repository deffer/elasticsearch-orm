package nz.ac.auckland.search

/**
 * A key-value pair. Alternative to using properties=["key1=value1", "key2=value2"] syntax
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public @interface VariableParameter {
    String name()
    String value()
}
