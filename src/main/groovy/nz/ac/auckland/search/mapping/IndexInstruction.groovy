package nz.ac.auckland.search.mapping

/**
 * A property of MappingInfo.
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public enum IndexInstruction {
    NO,             // not searchable
    ANALYZED,       // searchable, indexed using specified (or default) analyzer
    NOT_ANALYZED    // searchable, but not analyzed
}