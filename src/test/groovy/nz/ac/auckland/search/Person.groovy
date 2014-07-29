package nz.ac.auckland.search

import nz.ac.auckland.search.analysis.Analyzer
import nz.ac.auckland.search.analysis.Filter;

import java.util.*;

@IndexedDocument(indexDocumentName='person',
    analyzers = [
        @Analyzer(name = 'snowball_english', type = 'snowball',
            params = [@VariableParameter(name = 'language', value = 'English')]),
        @Analyzer(name = 'snowball_spanish', type = 'snowball',
            params = [@VariableParameter(name = 'language', value = 'Spanish')]),
        @Analyzer(name = 'phonetic',
            tokenizerName = 'whitespace',
            filterNames = ['trim', 'lowercase', 'asciifolding', 'stop', 'unique', 'custom_metaphone'])],
    filters = [
        @Filter(name = 'custom_metaphone', type = 'phonetic',
            params = [
                @VariableParameter(name = 'encoder', value = 'metaphone'),
                @VariableParameter(name = 'replace', value = 'false')] )
])
class Person {
    Long id = 8
    String field1 = "abc";
    Set<Long> favoriteNumbers = [4,7,13,37,66,99] as Set

    @IndexedField(indexFieldName = 'age')
    private Long annotatedField = 29  // private field without getter

    @IndexedField(indexFieldName = 'descrHTML')
    private String descrHTML = "<br>Some text</br>";

    @IndexedField(indexFieldName = 'descrTXT', markupPresent =  true)
    String getDescr(){
        return descrHTML
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

    @IndexedField(indexFieldName = 'favoriteNumbers')
    Set<Long> getFavoriteNumbers() {return favoriteNumbers}

    @IndexedField(indexFieldName = 'favoriteNumbers')
    List<Long> addSomethingToFavNumbers(){
        return [111,222,333]
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
