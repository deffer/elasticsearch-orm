package nz.ac.auckland.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * A helper class that converts object annotated with IndexedFields to a map that represents single elastic document.
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public class IndexedDocumentMetainfo {
	public static Logger log = LoggerFactory.getLogger(IndexedDocumentMetainfo.class);

	/**
	 * List of methods, annotated with @IndexedField
	 */
	Map<Method, IndexedField> annotatedMethods = [:]

	/**
	 * List of fields, annotated with @IndexedField
	 */
	Map<Field, IndexedField> annotatedFields = [:]

	/**
	 * List of fields, which 'getters' are annotated. For example if getNote() is annotated,
	 *   this map will have entry 'note' --> annotation.
	 * Used to detect and warn about possible conflicts between annotations of field and its getter.
	 */
	Map<String, IndexedField> sanityCheckCache = [:]

	/**
	 * Preferable way to access field is through its getter (to allow 'enhanced' beans to load their value on demand).
	 * This is a cache of getters which fields are annotated
	 */
	Map<Field, Method> field2getterMap = [:]

	/**
	 * Injected. Used to convert values that has markupPresent=true
	 */
	MarkupConvertor markupConvertor = null;

	public IndexedDocumentMetainfo(Class<?> clazz){
		this(clazz, null);
	}

	public IndexedDocumentMetainfo(Class<?> clazz, MarkupConvertor markupConvertor){
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
	private void cacheAnnotatedMethods(Class<?> clazz){

		for (Method method : clazz.getDeclaredMethods()){
			IndexedField annotation = method.getAnnotation(IndexedField.class);
			if (annotation != null){
				if (Modifier.isPrivate(method.getModifiers())){
					log.warn("Accessing private method ${clazz.getSimpleName()}.${method.getName()}");
				}
				if (method.getGenericParameterTypes().length>0) {
					log.warn("Methods with arguments aren't supported: ${clazz.getSimpleName()}.${method.getName()}");
				}else{
					annotatedMethods.put(method, annotation);

					String name = method.getName();
					if (name.startsWith('get') && name.length()>3){
						String fieldName = name.substring(3);
						// lower case first char
						fieldName = fieldName.substring(0,1).toLowerCase()+fieldName.substring(1);
						if (sanityCheckCache.put(fieldName, annotation)!=null){
							log.warn("Two getters for same field ?? ${method.getName()}");
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
	private void cacheAnnotatedFields(Class<?> clazz){

		// if sanityCheckCache already contains this name, make a warning
		//   that getter for this field is also annotated
		for (Field field : clazz.getDeclaredFields()){
			IndexedField annotation = field.getAnnotation(IndexedField.class);
			if (annotation != null){
				String name = field.getName();
				String logName = clazz.getSimpleName()+"."+name;
				if (Modifier.isPrivate(field.getModifiers())){
					log.warn("Accessing private field of $logName");
				}
				annotatedFields.put(field, annotation);

				IndexedField existing = sanityCheckCache.get(name);
				// if there is a getter with annotation
				if (existing != null){
					//if (existing.indexFieldName()!=annotation.indexFieldName() // string literals in annotations don't seem to be 'interned'
					if (!(existing.indexFieldName().equals(annotation.indexFieldName()))
							|| existing.markupPresent()!=annotation.markupPresent()){
						log.warn("Possible mistake. Both field $name and its getter are annotated.");
					}else {
						// absolutely same annotations
						log.warn("Annotation on field $logName will be ignored. You only need one annotation (either field or getter).");
						annotatedFields.remove(field);
					}
				}else{
					// if there is non-annotated getter, pick it it to use instead of field
					String getterName = 'get'+name.substring(0,1).toUpperCase()+name.substring(1);
					try{
						Method getter = clazz.getDeclaredMethod(getterName);
						if (Modifier.isPrivate(getter.getModifiers())){
							log.warn("Accessing private getter of $logName");
						}
						field2getterMap.put(field, getter);
					}catch (NoSuchMethodException e){
						// nothing
					}
				}
			}
		}
	}

	/**
	 * Main functionality. Converts annotated object into map of fields and its values.
	 *
	 * @param object object which methods and fields are annotated with IndexedField
	 *
	 * @return
	 */
	public Map<String, Object> convertObject(Object object){
		Map<String, ?> result = [:]
		annotatedMethods.each{Method method, IndexedField annotation ->
			try {
				Object value = getMethodValue(method, object);
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
					value = getMethodValue(getter, object);
				else
					value = getFieldValue(field, object);
				processValue(value, annotation, result);
			}catch(Throwable e){
				log.error(e.getMessage(), e);
			}
		}
		return result;
	}

	private Object getMethodValue(Method method, Object obj) throws Throwable{
		boolean before = method.isAccessible();
		Object result;
		if (Modifier.isPrivate(method.getModifiers())){
			method.setAccessible(true);
		}
		try{
			result = method.invoke(obj, null);
		}finally {
			if (method.isAccessible() != before){
				method.setAccessible(before);
			}
		}
		return result;
	}

	private Object getFieldValue(Field field, Object obj) throws Throwable{
		boolean before = field.isAccessible();
		Object result;
		if (Modifier.isPrivate(field.getModifiers())){
			field.setAccessible(true);
		}
		result = field.get(obj);
		if (field.isAccessible() != before){
			field.setAccessible(before);
		}
		return result;
	}

	protected void processValue(Object value, IndexedField annotation, Map<String, ?> into){
		if (value!=null && annotation.markupPresent())
			value = getNakedText(value);

		if (!emptyValue(value)){
			addField(annotation.indexFieldName(), value, into);
		}
	}

	private boolean emptyValue(Object value){
		if (value == null)
			return true;

		if (value instanceof String){
			return ((String)value).trim().isEmpty();
		}else{
			return false;
		}
	}

	protected void addField(String indexedFieldName, Object fieldValue, Map<String, ?> into){
		if (fieldValue == null) return;
		def value = fieldValue;

		if (fieldValue instanceof Collection){
			if (fieldValue instanceof Map){
				log.error("Maps are not supported. $indexedFieldName will be ignored.");
				return;
			}
            // make a copy
			value = new ArrayList(fieldValue)
		}

		if (into.containsKey(indexedFieldName)){
			// if not array, convert to array and add to it
			def existing = into.get(indexedFieldName);
			if (!(existing instanceof List)){
				existing = [existing] as List
				into.put(indexedFieldName, existing);
			}

			if (value instanceof Collection)
				existing.addAll((Collection) value);
			else
				existing << value
		}else{
			into.put(indexedFieldName, value);
		}
	}

	protected String getNakedText(Object value){
		if (value == null) return null;
		if (markupConvertor == null){
			log.error('Attempt to convert text without markupConvertor specified');
			return value.toString();
		}
		String str = markupConvertor.convertToText(value.toString());
		if (!str.trim().isEmpty()){
			return str.trim();
		}else{
			return null;
		}
	}
}
