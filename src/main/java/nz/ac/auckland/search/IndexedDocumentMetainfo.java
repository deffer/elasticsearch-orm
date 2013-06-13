package nz.ac.auckland.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class IndexedDocumentMetainfo {
    public static Logger log = LoggerFactory.getLogger(IndexedDocumentMetainfo.class);

    /**
     * List of methods, annotated with @IndexedField
     */
    Map<Method, IndexedField> annotatedMethods = new HashMap<>();

    /**
     * List of fields, annotated with @IndexedField
     */
    Map<Field, IndexedField> annotatedFields = new HashMap<>();

    /**
     * List of fields, which 'getters' are annotated. For example if getNote() is annotated,
     *   this map will have entry 'note' --> annotation.
     * Used to detect and warn about possible conflicts between annotations of field and its getter.
     */
    Map<String, IndexedField> sanityCheckCache = new HashMap<>();

    /**
     * Preferable way to access field is through its getter (to allow 'enhanced' beans to load their value on demand).
     * This is a cache of getters which fields are annotated
     */
    Map<Field, Method> field2getterMap = new HashMap<>();

    /**
     * Injected. Used to convert values that has markupPresent=true
     */
    MarkupConvertor markupConvertor = null;

    public IndexedDocumentMetainfo(Class clazz){
        this(clazz, null);
    }

    public IndexedDocumentMetainfo(Class clazz, MarkupConvertor markupConvertor){
        this.markupConvertor = markupConvertor;
        cacheAnnotatedMethods(clazz);
        cacheAnnotatedFields(clazz);
    }

    /**
     * Cache all annotated method information.
     *
     * If method looks like getter, put field name into sanityCheckCache
     *
     * @param clazz
     */
    private void cacheAnnotatedMethods(Class clazz){

        for (Method method : clazz.getDeclaredMethods()){
            IndexedField annotation = method.getAnnotation(IndexedField.class);
            if (annotation != null){
                if (Modifier.isPrivate(method.getModifiers())){
                    method.setAccessible(true);
                    log.warn("Accessing private methods "+clazz.getSimpleName()+"."+method.getName());
                }
                if (method.getGenericParameterTypes().length>0) {
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
    }


    /**
     * Cache all annotated field's information.
     *
     * The default behaviour is to use getter instead of directly accessing field (to allow lazy loading techniques to work),
     * Therefore for each annotated field, lookup corresponding getter and cache it too.
     *
     * When both field and its getter annotated, if annotations are exactly same - ignore field's annotation, otherwise
     *   issue a WARNING.
     *
     * @param clazz
     */
    private void cacheAnnotatedFields(Class clazz){

        // if sanityCheckCache already contains this name, make a warning
        //   that getter for this field is also annotated
        for (Field field : clazz.getDeclaredFields()){
            IndexedField annotation = field.getAnnotation(IndexedField.class);
            if (annotation!=null){
                String name = field.getName();
                String logName =clazz.getSimpleName()+"."+name;
                if (Modifier.isPrivate(field.getModifiers())){
                    field.setAccessible(true);
                    log.warn("Accessing private field of "+logName);
                }
                annotatedFields.put(field, annotation);

                IndexedField existing = sanityCheckCache.get(name);
                // if there is a getter with annotation
                if (existing != null){
                    //if (existing.indexFieldName()!=annotation.indexFieldName() // string literals in annotations don't seem to be 'interned'
                    if (!(existing.indexFieldName().equals(annotation.indexFieldName()))
                            || existing.markupPresent()!=annotation.markupPresent()){
                        log.warn("Possible mistake. Both field "+name+" and its getter are annotated.");
                    }else {
                        // absolutely same annotations
                        log.warn("Annotation on field '"+logName+"' will be ignored. You only need one annotation (either field or getter).");
                        annotatedFields.remove(field);
                    }
                }else{
                    // if there is non-annotated getter, pick it it to use instead of field
                    String getterName = "get"+name.substring(0,1).toUpperCase()+name.substring(1);
                    try{
                        Method getter = clazz.getDeclaredMethod(getterName);
                        if (Modifier.isPrivate(getter.getModifiers())){
                            getter.setAccessible(true);
                            log.warn("Accessing private getter of "+logName);
                        }
                        field2getterMap.put(field, getter);
                    }catch (NoSuchMethodException e){
                        // nothing
                    }
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
                Method getter = field2getterMap.get(field);
                Object value;
                if (getter!=null)
                    value = getter.invoke(object, null);
                else
                    value = field.get(object);
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
                list.addAll((Collection) value);
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
