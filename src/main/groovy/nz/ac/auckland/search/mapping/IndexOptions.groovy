package nz.ac.auckland.search.mapping


//Since 0.20.
public enum IndexOptions {
    DEFAULT,  //positions for analyzed fields, and to docs for not_analyzed fields
    DOCS, // only doc numbers
    FREQS, // doc numbers and term frequencies
    POSITIONS // all above plus positions
}