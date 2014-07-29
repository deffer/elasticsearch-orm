package nz.ac.auckland.search

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * A shortcut for IndexedField with MappingInfo(store=yes, indexInstruction=NO)
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD, ElementType.FIELD])
public @interface StoredField {


	/**
	 * @return name of the field in index. If object declares several fields with the same indexFieldName, they will
	 *   be stored as arrays of values.
	 */
	String indexFieldName();

}