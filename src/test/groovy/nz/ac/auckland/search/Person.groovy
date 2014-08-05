package nz.ac.auckland.search

import nz.ac.auckland.search.analysis.Analyzer
import nz.ac.auckland.search.analysis.Filter;

import java.util.*;

@IndexedDocument(indexDocumentName = 'person',
		analyzers = [
		@Analyzer(name = 'snowball_english', type = 'snowball', properties = 'language=English'),
		@Analyzer(name = 'snowball_spanish', type = 'snowball', properties = 'language=Spanish'),
		@Analyzer(name = 'phonetic',
				tokenizerName = 'whitespace',
				filterNames = ['trim', 'lowercase', 'asciifolding', 'stop', 'unique', 'custom_metaphone'])],
		filters = [
		@Filter(name = 'custom_metaphone', type = 'phonetic',properties = 'encoder=metaphone, replace=false')
		])
class Person {
	Long id = 8

	Set<Long> favoriteNumbers = [4, 7, 13, 37, 66, 99] as Set

	String title = "Mrs";

	@IndexedField(indexFieldName = "personid")
	public Long getId() { return id; } // getter for existing field

	@IndexedField(indexFieldName = 'age')
	private Long currentAge = 29  // private field without getter

	@IndexedField(indexFieldName = "defaultValue")
	String defaultValueField = "."; // field with getter returning different value

	private String getDefaultValueField() {
		return "Lazy loaded value";
	}

	@IndexedField(indexFieldName = "names")
	String firstName = "Anna";

	@IndexedField(indexFieldName = "names")
	String lastName = "Torrez"

	@IndexedField(indexFieldName = "names")
	public String getMiddleName() {
		return "Maria";
	}


	@IndexedField(indexFieldName = 'favorites')
	Set<Long> getFavoriteNumbers() { return favoriteNumbers }

	@IndexedField(indexFieldName = 'favorites')
	List<Long> additionalFavNumbers() {
		return [111, 222, 333]
	}

	@IndexedField(indexFieldName = "privateMethod")
	private String currentDepartment() {
		return "Science";
	}

	@IndexedField(indexFieldName = "excluded")
	Long methodWithArgs(int something) {
		return new Long(888);
	}

	@IndexedField(indexFieldName = 'descrHTML')
	private String descrHTML = "<br>Some text</br>";

	@IndexedField(indexFieldName = 'descrTXT', markupPresent = true)
	String getDescr() {
		return descrHTML
	}
}
