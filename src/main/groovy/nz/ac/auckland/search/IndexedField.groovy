package nz.ac.auckland.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indicates that method or field should be stored in index.
 * Private methods or methods with arguments are not supported.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD, ElementType.FIELD])
public @interface IndexedField {

    /**
     * @return name of the field in index. If object declares several fields with the same indexFieldName, they will
     *   be stored as arrays of values.
     */
    String indexFieldName();

    /**
     * @return true if field value is text and contains a markup. The default configured markup converter
     *   will be used to convert field value to plain text.
     */
    boolean markupPresent() default false;

}
