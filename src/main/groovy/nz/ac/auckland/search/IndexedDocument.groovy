package nz.ac.auckland.search

import nz.ac.auckland.search.analysis.AnalyzerInstance
import nz.ac.auckland.search.analysis.FilterInstance
import nz.ac.auckland.search.mapping.MappingInfo

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IndexedDocument {
    String indexDocumentName()

    // Should only be defined once per index
    AnalyzerInstance[] analyzers() default []
    FilterInstance[] filters()  default[]
    int numberOfShards() default 5

    MappingInfo fieldAll() default @MappingInfo(enabled = true)
    MappingInfo fieldSource() default @MappingInfo(enabled = true)
}


