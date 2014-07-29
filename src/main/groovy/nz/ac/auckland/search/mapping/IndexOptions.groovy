package nz.ac.auckland.search.mapping

/**
 * A property of MappingInfo.
 * Since 0.20.
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public enum IndexOptions {
    DEFAULT,  //positions for analyzed fields, and to docs for not_analyzed fields
    DOCS, // only doc numbers
    FREQS, // doc numbers and term frequencies
    POSITIONS // all above plus positions
}