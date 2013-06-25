package nz.ac.auckland.search;

/**
 * Instance of this should be injected into IndexedDocumentMetainfo during creation.
 * Is used to convert fields annotated with markupPresent=true to plain text
 */
public interface MarkupConvertor {
    String convertToText(String formattedText);
}
