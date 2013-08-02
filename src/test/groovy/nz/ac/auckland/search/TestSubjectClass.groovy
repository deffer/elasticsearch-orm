package nz.ac.auckland.search;

import java.util.*;

@IndexedDocument(indexDocumentName="testSubject")
class TestSubjectClass {
    Long id = new Long(8);
    String field1 = "abc";
    Set<Long> facs = new HashSet(Arrays.asList(new Long(43), new Long(11), new Long(890)));

    @IndexedField(indexFieldName = "fld")
    private Long annotatedField = new Long(990);  // private field without getter


    @IndexedField(indexFieldName = "rawText")
    private String markedUpString = "<br>Some text</br>";

    @IndexedField(indexFieldName = "convertedText", markupPresent =  true)
    String getMarkedUpString(){
        return markedUpString;
    }

    @IndexedField(indexFieldName="defaultValue")
    String defaultValueField = "."; // getter returns different value

    private String getDefaultValueField(){
        return "Lazy loaded value";
    }

    @IndexedField(indexFieldName="duplication")
    String duplication = "duplication field";

    @IndexedField(indexFieldName="duplication")
    String getDuplication(){
        return "duplication method";
    }

    @IndexedField(indexFieldName = "id")
    public Long getId(){return id;}

    @IndexedField(indexFieldName = "faculties")
    Set<Long> getFacs() {return facs;}

    @IndexedField(indexFieldName = "faculties")
    List<Long> addSomethingToFaculties(){
        return Arrays.asList(new Long(111), new Long(222), new Long(333));
    }

    @IndexedField(indexFieldName = "h")
    String getABC(){
        return field1;
    }

    @IndexedField(indexFieldName = "h")
    String getBCD(){
        return "bcd";
    }

    @IndexedField(indexFieldName = "h")
    String notGetter(){
        return "cdf";
    }

    @IndexedField(indexFieldName = "privateMethod")
    private String privateMethod(){
        return field1;
    }

    @IndexedField(indexFieldName = "excluded")
    Long methodWithArgs(int something){
        return new Long(888);
    }

}
