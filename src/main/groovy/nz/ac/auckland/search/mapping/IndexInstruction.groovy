package nz.ac.auckland.search.mapping

/**
 * Created with IntelliJ IDEA.
 * User: deffer
 * Date: 2/08/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public enum IndexInstruction {
    NO,             // not searchable
    ANALYZED,       // searchable, indexed using specified (or default) analyzer
    NOT_ANALYZED    // searchable, but not analyzed
}