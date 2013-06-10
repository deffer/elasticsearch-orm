class TestSubjectClass {
    Long id = 8
    String field1 = 'abc'
    Set<Long> facs = [43, 11, 890] as Set<Long>

    @IndexedField(indexFieldName = 'rawText')
    String markedUpString = '<br>Some text</br>'

    @IndexedField(indexFieldName = "convertedText", markupPresent =  true)
    String getMarkedUpString(){
        return markedUpString
    }

    @IndexedField(indexFieldName = 'fld')
    Long annotatedField = 990

    @IndexedField(indexFieldName = 'id')
    public Long getId(){return id}

    @IndexedField(indexFieldName = 'faculties')
    Set<Long> getFacs() {return facs}

    @IndexedField(indexFieldName = 'faculties')
    List<Long> addSomethingToFaculties(){
        return [111, 222, 333] as List<Long>
    }

    @IndexedField(indexFieldName = 'h')
    String getABC(){
        return field1
    }

    @IndexedField(indexFieldName = 'h')
    String getBCD(){
        return 'bcd'
    }

    @IndexedField(indexFieldName = 'h')
    String notGetter(){
        return 'cdf'
    }

    @IndexedField(indexFieldName = 'excluded')
    private String privateMethod(){
        return field1
    }

    @IndexedField(indexFieldName = 'excluded')
    def methodWithArgs(int something){
        return 888
    }

}
