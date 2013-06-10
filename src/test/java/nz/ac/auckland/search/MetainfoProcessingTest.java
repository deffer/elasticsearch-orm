package nz.ac.auckland.search;

import org.fest.assertions.Fail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class MetainfoProcessingTest {
    @Before
    public void setup() {

    }

    @Test
    public void testConversion(){
        IndexedDocumentMetainfo doc = new IndexedDocumentMetainfo(TestSubjectClass.class, new MarkupConvertor() {
            @Override
            public String convertToText(String formattedText) {
                return "Some text";
            }
        });

        TestSubjectClass object = new TestSubjectClass();
        Map<String, Object> result = doc.convertObject(object);

        assert result.get("id").equals(new Long(8));

        assert result.get("fld").equals(new Long(990));

        assert result.get("h") instanceof List;
        List h = (List)result.get("h");
        assert h.size()==3;
        assert h.containsAll(Arrays.asList("abc", "bcd", "cdf"));

        assert result.get("faculties") instanceof List;
        List faculties = (List)result.get("faculties");
        assert faculties.size()==6;
        assert faculties.containsAll(Arrays.asList(new Long(111), new Long(222), new Long(333), new Long(43), new Long(11), new Long(890)));

        assert result.get("convertedText").equals("Some text");
        assert result.get("rawText").equals("<br>Some text</br>");
        assert !result.containsKey("excluded");
    }
}
