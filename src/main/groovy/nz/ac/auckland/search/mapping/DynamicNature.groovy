package nz.ac.auckland.search.mapping

/**
 * Instruction on what to do when one tries to save document with fields
 *   that were not defined in the document mapping during index creation.
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public enum DynamicNature {
	FALSE, // will not try to index 'dynamic' fields (those that weren't defined on index creation)
	TRUE,  // accepts object with any fields and will add them to object mapping on the fly. Default one.
	STRICT // will throw an exception if object has fields that weren't defined on index creation
}