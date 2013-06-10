package nz.ac.auckland.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class IndexedDocumentMetainfo {
    public static Logger log = LoggerFactory.getLogger(IndexedDocumentMetainfo.class);

    Map<String, IndexedField> sanityCheckCache = new HashMap<>();
    Map<Method, IndexedField> annotatedMethods = new HashMap<>();
    Map<Field, IndexedField> annotatedFields = new HashMap<>();

    MarkupConvertor markupConvertor = null;

    public IndexedDocumentMetainfo(Class clazz){
        this(clazz, null);
    }

    public IndexedDocumentMetainfo(Class clazz, MarkupConvertor markupConvertor){
        this.markupConvertor = markupConvertor;
        // cache all annotated methods of interest
        // if method looks like getter, put field name into sanityCheckCache
        //clazz.getDeclaredMethods().each {Method method->
        for (Method method : clazz.getDeclaredMethods()){
            IndexedField annotation = method.getAnnotation(IndexedField.class);
            if (annotation != null){
                if (Modifier.isPrivate(method.getModifiers())){
                    log.warn("Private methods aren't supported: "+clazz.getSimpleName()+"."+method.getName());
                }else if (method.getGenericParameterTypes().length>0) {
                    log.warn("Methods with arguments aren't supported: "+clazz.getSimpleName()+"."+method.getName());
                }else{
                    annotatedMethods.put(method, annotation);

                    String name = method.getName();
                    if (name.startsWith("get") && name.length()>3){
                        String fieldName = name.substring(3);
                        // lower case first char
                        fieldName = fieldName.substring(0,1).toLowerCase()+fieldName.substring(1);
                        if (sanityCheckCache.put(fieldName, annotation)!=null){
                            log.warn("Two getters for same field ?? "+method.getName());
                        }
                    }
                }
            }
        }

        // cache all annotated field of interest
        // if sanityCheckCache already contains this name, make a warning
        //   that getter for this field is also annotated
        //clazz.getDeclaredFields().each {Field field->
        for (Field field : clazz.getDeclaredFields()){
            IndexedField annotation = field.getAnnotation(IndexedField.class);
            if (annotation!=null){
                String logName =clazz.getSimpleName()+"."+field.getName();
                if (Modifier.isPrivate(field.getModifiers())){
                    field.setAccessible(true);
                    log.warn("Accessing private field of "+logName);
                }
                annotatedFields.put(field, annotation);

                String name = field.getName();
                IndexedField existing = sanityCheckCache.get(name);
                if (existing!=null){
                    if (existing.indexFieldName()!=annotation.indexFieldName()){
                        log.warn("Possible mistake. Both field "+name+" and its getter are annotated.");
                    }else if (existing.markupPresent()==annotation.markupPresent()){
                        // absolutely same annotations
                        log.warn("Duplicate annotation on the field "+logName+" and its getter. May result in duplicate data in the index. Index field name "+annotation.indexFieldName());
                    } // same indexFieldName but different markup flag is very likely intended behaviour
                }
            }
        }
    }

    public Map<String, Object> convertObject(Object object){

        Map<String, Object> result = new HashMap<>();
        for (Method method : annotatedMethods.keySet()){
            IndexedField annotation = annotatedMethods.get(method);
            try {
                Object value = method.invoke(object, null);
                processValue(value, annotation, result);
            }catch(Throwable e){
                log.error(e.getMessage(), e);
            }
        }

        for (Field field : annotatedFields.keySet()){
            IndexedField annotation = annotatedFields.get(field);
            try {
                Object value = field.get(object);
                processValue(value, annotation, result);
            }catch(Throwable e){
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }

    protected void processValue(Object value, IndexedField annotation, Map<String, Object> into){
        if (value!=null && annotation.markupPresent())
            value = getNakedText(value);

        if (value!=null){
            addField(annotation.indexFieldName(), value, into);
        }
    }

    protected void addField(String indexedFieldName, Object fieldValue, Map<String, Object> into){
        if (fieldValue == null) return;
        Object value = fieldValue;

        if (fieldValue instanceof Collection){
            if (fieldValue instanceof Map){
                log.error("Maps aren't supported. "+indexedFieldName+" will be ignored.");
                return;
            }
            value = new ArrayList( (Collection)fieldValue );
        }

        if (into.containsKey(indexedFieldName)){
            // if not array, convert to array and add to it
            Object existing = into.get(indexedFieldName);
            List list = null;
            if (!(existing instanceof List)){
                list = new ArrayList();
                list.add(existing);
                into.put(indexedFieldName, list);
            }else{
                list = (List)existing ;
            }

            if (value instanceof Collection)
                list.addAll((Collection)value);
            else
                list.add(value);
        }else{
            into.put(indexedFieldName, value);
        }
    }

    protected String getNakedText(Object value){
        if (value==null) return null;
        if (markupConvertor == null){
            log.error("Attempt to convert tex without markupConvertor specified");
            return value.toString();
        }
        String str = markupConvertor.convertToText(value.toString());
        if (!str.trim().equals(".") && !str.trim().isEmpty()){
            return str;
        }else{
            return null;
        }
    }
}
