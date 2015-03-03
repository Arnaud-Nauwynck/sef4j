package com.google.code.joto.ast.valueholder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.code.joto.util.attr.DefaultAttributeSupport;
import com.google.code.joto.util.attr.IAttributeSupport;
import com.google.code.joto.util.attr.IAttributeSupportDelegate;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;

/**
 * base class of AST hierarchy for object tree values (or fragment of values)
 * 
 * This is an in-memory replacement of real objects by generic ones:
 *   transform real java Object => Map < Map < Field,Value> > 
 * 
 * This is done for several purpose:
 * <ul>
 * <li> Objects graph can be traversed in both direction : every object records the list of pointer to itself</li>
 * <li> Object elements (instance, fields, array elements) are all first-class-citizen objects, and can be annotated with key=values markers for internal algorithms</li>
 * <li> The reflection api is called only once, to build the generic equivalent form. </li>
 * <li> The reflection API is rather complex to access private field (using native call to jdk specific methods, using XStream implementation helpers) </li>
 * </ul>
 * 
 * It allows treating similar objects like List,ArrayList,UnmodifiableList...  
 */
public abstract class ValueHolderAST implements IAttributeSupportDelegate {
	
	private IAttributeSupport attributeSupport;
	
	protected ValueHolderAST() {
	}
	
	public abstract void visit(ValueHolderVisitor v);
	public abstract <R,A> R visit(ValueHolderVisitor2<R,A> v, A arg);

	
	
	public IAttributeSupport getAttributeSupport() {
		 if (attributeSupport == null) {
			 attributeSupport = new DefaultAttributeSupport();
		}
		 return attributeSupport;
	}

    public static Class<?> wrapperTypeFor(Class<?> primitiveType) {
        if (primitiveType == Boolean.TYPE) return Boolean.class;
        if (primitiveType == Byte.TYPE) return Byte.class;
        if (primitiveType == Character.TYPE) return Character.class;
        if (primitiveType == Short.TYPE) return Short.class;
        if (primitiveType == Integer.TYPE) return Integer.class;
        if (primitiveType == Long.TYPE) return Long.class;
        if (primitiveType == Float.TYPE) return Float.class;
        if (primitiveType == Double.TYPE) return Double.class;
        if (primitiveType == Void.TYPE) return Void.class;
        return null;
    }

	private static void checkValueWrapperForPrimitiveType(Object value, Class<?> type) {
		if (value != null) {
			Class<?> valueClass = value.getClass();
			if (valueClass != wrapperTypeFor(type)) {
				throw new IllegalArgumentException();
			}
		} else {
			// no check or throw ??
		}
	}

	// -------------------------------------------------------------------------

	public static abstract class AbstractObjectValueHolder extends ValueHolderAST {
		
		protected final Class<?> objClass;
		
		private List<RefObjectValueHolder> linksFrom = new ArrayList<RefObjectValueHolder>(); 
		
		protected AbstractObjectValueHolder(Class<?> objClass) {
			this.objClass = objClass;
		}

		public Class<?> getObjClass() {
			return objClass;
		}

		public List<RefObjectValueHolder> getLinksFrom() {
			return linksFrom;
		}
		
		/*pp*/ void _inv_removeLinkFrom(RefObjectValueHolder p) {
			linksFrom.remove(p);
		}

		/*pp*/ void _inv_addLinkFrom(RefObjectValueHolder p) {
			linksFrom.add(p);
		}

	}
	
	/**
	 * This class is the parent-classes for all kind of "pointer" in the ValueHolder hierarchy
	 */
	public static abstract class RefObjectValueHolder extends ValueHolderAST {
		
		private final AbstractObjectValueHolder from;
		private AbstractObjectValueHolder to;
		
		public RefObjectValueHolder(AbstractObjectValueHolder from) {
			this.from = from;
		}

		public RefObjectValueHolder(AbstractObjectValueHolder from, AbstractObjectValueHolder to) {
			this(from);
			setTo(to);
		}

		public final AbstractObjectValueHolder getTo() {
			return to;
		}

		public final AbstractObjectValueHolder getFrom() {
			return from;
		}

		public final void setTo(AbstractObjectValueHolder p) {
			if (p == to) return;
			if (to != null) {
				to._inv_removeLinkFrom(this);
			}
			this.to = p;
			if (to != null) {
				to._inv_addLinkFrom(this);
			}
		}
		
		public String toString() {
			return "RefVH[ to:" + to + "]";
		}
		
	}
	
//	/**
//	 * 
//	 */
//	public static class NullValueHolder extends AbstractObjectValueHolder {
//
//		public static final NullValueHolder INSTANCE = new NullValueHolder();
//		
//		public static final NullValueHolder getInstance() { return INSTANCE; }
//		
//		private NullValueHolder() {
//			super(Void.class);
//		}
//		
//		@Override
//		public void visit(ValueHolderVisitor v) {
//			v.caseNull();
//		}
//
//		@Override
//		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
//			return v.caseNull(a);
//		}
//		
//	}
	

	// -------------------------------------------------------------------------

	/**
	 * 
	 */
	public static class ObjectValueHolder extends AbstractObjectValueHolder {
		
		private Map<Field,FieldValueHolder> fieldsValuesMap = new LinkedHashMap<Field,FieldValueHolder>();
		
		public ObjectValueHolder(Class<?> objClass) {
			super(objClass);
		}

		public void visit(ValueHolderVisitor v) {
			v.caseObject(this);
		}

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseObject(this, a);
		}

		public Map<Field,FieldValueHolder> getFieldsValuesMap() {
			return fieldsValuesMap;
		}

		public FieldValueHolder getFieldValue(FieldDictionary fieldDictionary, String fieldName, Class<?> fieldType) {
			Field field;
			try {
				// does not work: only public fields are found
				//... field = objClass.getField(fieldName);
				field = fieldDictionary.field(objClass, fieldName, null);
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
			return getFieldValue(field);
		}
		
		public FieldValueHolder getFieldValue(Field field) {
			FieldValueHolder res = fieldsValuesMap.get(field);
			if (res == null) {
				// instanciate proer sub-class for Field
				res = newFieldValue(field);
				fieldsValuesMap.put(field, res);
			}
			return res;
		}
		
		protected FieldValueHolder newFieldValue(Field field) {
			FieldValueHolder res;
			Class<?> fieldType = field.getType();
			if (fieldType.isPrimitive()) {
				res = new PrimitiveFieldValueHolder(this, field);
			} else {
				res = new RefFieldValueHolder(this, field);
			}
			return res;
		}
	}

	/**
	 * this interface is a technical atifact to do emulate "multiple inheritance":
	 * PrimitiveField .. extends ValueHolderAST
	 * RefField .. extends RefValueHolder    
	 */
	public static interface FieldValueHolder {
		ValueHolderAST getThisValueHolder();
		// helper for getThisValueHolder().visit(v) 
		public void visit(ValueHolderVisitor v);
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A arg);
		
		public ObjectValueHolder getParent();
		public Field getField();
		public Class<?> getFieldType(); // helper for getField().getType()
		
	}
	
//	/**
//	 * 
//	 */
//	public static abstract class AbstractFieldValueHolder extends ValueHolderAST {
//
//		protected AbstractObjectValueHolder parent;
//		protected final Field field;
//
//		public AbstractFieldValueHolder(AbstractObjectValueHolder parent, Field field) {
//			super();
//			this.parent = parent;
//			this.field = field;
//		}
//
//		
//		public AbstractObjectValueHolder getParent() {
//			return parent;
//		}
//
//		public Field getField() {
//			return field;
//		}
//
//		public Class<?> getFieldType() {
//			return field.getType();
//		}
//
//	}

	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class PrimitiveFieldValueHolder extends ValueHolderAST implements FieldValueHolder { // extends AbstractFieldValueHolder 
	    
		protected final ObjectValueHolder parent;
		protected final Field field;

		private Object value;
		
		public PrimitiveFieldValueHolder(ObjectValueHolder parent, Field field) {
			this.parent = parent;
			this.field = field;
		}

		public void visit(ValueHolderVisitor v) {
			v.casePrimitiveField(this);
		}		

		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.casePrimitiveField(this, a);
		}

		public ValueHolderAST getThisValueHolder() {
			return this;
		}
		
		public ObjectValueHolder getParent() {
			return parent;
		}

		public Field getField() {
			return field;
		}

		public Class<?> getFieldType() {
			return field.getType();
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			checkValueWrapperForPrimitiveType(value, field.getType());
			this.value = value;
		}

	}


	/**
	 * 
	 */
	public static class RefFieldValueHolder extends RefObjectValueHolder implements FieldValueHolder {

		protected final Field field;
		
		public RefFieldValueHolder(ObjectValueHolder parent, Field field) {
			super(parent);
			this.field = field;
		}

		public void visit(ValueHolderVisitor v) {
			v.caseRefField(this);
		}
		
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseRefField(this, a);
		}

		public ValueHolderAST getThisValueHolder() {
			return this;
		}
		
		public ObjectValueHolder getParent() {
			return (ObjectValueHolder) getFrom();
		}

		public Field getField() {
			return field;
		}

		public Class<?> getFieldType() {
			return field.getType();
		}

	}

	// -------------------------------------------------------------------------

	/**
	 * 
	 */
	public static class ImmutableObjectValueHolder extends AbstractObjectValueHolder {
		
		private final Object value;

		public ImmutableObjectValueHolder(Object value) {
			super(value.getClass());
			this.value = value;
		}
		
		@Override
		public void visit(ValueHolderVisitor v) {
			v.caseImmutableObjectValue(this);
		}

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseImmutableObjectValue(this, a);
		}

		public Object getValue() {
			return value;
		}
		
	}

	// -------------------------------------------------------------------------

	/**
	 * 
	 */
	public static class PrimitiveArrayEltValueHolder<T> extends ValueHolderAST {
	    
		private PrimitiveArrayValueHolder<T> parent;
		private int index;
		
		private T value;
		
		public PrimitiveArrayEltValueHolder(PrimitiveArrayValueHolder<T> parent, int index) {
			super();
			this.parent = parent;
			this.index = index;
		}

		public void visit(ValueHolderVisitor v) {
			v.casePrimitiveArrayElt(this);
		}		

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.casePrimitiveArrayElt(this, a);
		}

		public PrimitiveArrayValueHolder<T> getParent() {
			return parent;
		}

		public int getIndex() {
			return index;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			if (value != null && value.getClass() != parent.componentWrapperType) {
				throw new IllegalArgumentException();
			}
			this.value = value;
		}

	}

	/**
	 * 
	 */
	public static class PrimitiveArrayValueHolder<T> extends AbstractObjectValueHolder {

 		private Class<?> componentWrapperType;
		private PrimitiveArrayEltValueHolder<T>[] holderArray;
		
		@SuppressWarnings("unchecked")
		public PrimitiveArrayValueHolder(Class<?> arrayObjType, int len) {
			super(arrayObjType);
			componentWrapperType = wrapperTypeFor(arrayObjType.getComponentType());
			holderArray = new PrimitiveArrayEltValueHolder[len];
			for (int i = 0; i < len; i++) {
				holderArray[i] = new PrimitiveArrayEltValueHolder<T>(this, i);
			}
		}

		public void visit(ValueHolderVisitor v) {
			v.casePrimitiveArray(this);
		}		

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.casePrimitiveArray(this, a);
		}

		public PrimitiveArrayEltValueHolder<T>[] getHolderArray() {
			return holderArray;
		}

		public PrimitiveArrayEltValueHolder<T> getHolderArrayAt(int index) {
			return holderArray[index];
		}
		
		public void setValueAt(int index, T value) {
			PrimitiveArrayEltValueHolder<T> h = getHolderArrayAt(index);
			h.setValue(value);
		}

		public T getValueAt(int index) {
			PrimitiveArrayEltValueHolder<T> h = getHolderArrayAt(index);
			return h.getValue();
		}

	}


	// -------------------------------------------------------------------------

	/**
	 * 
	 */
	public static class ArrayEltRefValueHolder extends RefObjectValueHolder {

		// cf super: ObjectArrayValueHolder from
		private int index;
		
		public ArrayEltRefValueHolder(RefArrayValueHolder from, int index) {
			super(from);
			this.index = index;
		}

		public void visit(ValueHolderVisitor v) {
			v.caseRefArrayElt(this);
		}		

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseRefArrayElt(this, a);
		}

		public RefArrayValueHolder getFromArray() {
			return (RefArrayValueHolder) super.getFrom();
		}

		public int getIndex() {
			return index;
		}
		
	}

	/**
	 * 
	 */
	public static class RefArrayValueHolder extends AbstractObjectValueHolder {
	    
		private ArrayEltRefValueHolder[] elts;
		
		public RefArrayValueHolder(Class<?> arrayType, int len) {
			super(arrayType);
			elts = new ArrayEltRefValueHolder[len];
			for (int i = 0; i < len; i++) {
				elts[i] = new ArrayEltRefValueHolder(this, i);
			}
		}

		@Override
		public void visit(ValueHolderVisitor v) {
			v.caseRefArray(this);
		}		

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseRefArray(this, a);
		}

		public ArrayEltRefValueHolder[] getEltRefs() {
			return elts;
		}

		public AbstractObjectValueHolder[] getElts() {
			int len = elts.length;
			AbstractObjectValueHolder[] res = new AbstractObjectValueHolder[len];
			for (int i = 0; i < len; i++) {
				res[i] = elts[i].getTo();
			}
			return res;
		}

		public ArrayEltRefValueHolder getHolderArrayEltAt(int index) {
			return elts[index];
		}
		
		public void setValueAt(int index, AbstractObjectValueHolder value) {
			ArrayEltRefValueHolder h = getHolderArrayEltAt(index);
			h.setTo(value);
		}

		public AbstractObjectValueHolder getValueAt(int index) {
			ArrayEltRefValueHolder h = getHolderArrayEltAt(index);
			return h.getTo();
		}

	}


	// -------------------------------------------------------------------------

	/**
	 * 
	 */
	public static class CollectionValueHolder extends ObjectValueHolder {
		
		private Collection<CollectionEltRefValueHolder> elts;
		
		public CollectionValueHolder() {
			this(ArrayList.class, new ArrayList<CollectionEltRefValueHolder>());
		}

		public CollectionValueHolder(Class<?> type, Collection<CollectionEltRefValueHolder> value) {
			super(type);
			this.elts = value;
		}

		@Override
		public void visit(ValueHolderVisitor v) {
			v.caseCollection(this);
		}		

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseCollection(this, a);
		}
		
		public Collection<CollectionEltRefValueHolder> getEltRefs() {
			return elts;
		}

		public Collection<AbstractObjectValueHolder> getElts() {
			Collection<AbstractObjectValueHolder> res = new ArrayList<AbstractObjectValueHolder>(elts.size());
			for(CollectionEltRefValueHolder elt : elts) {
				res.add(elt.getTo());
			}
			return res;
		}

//		public void setElts(Collection<RefObjectValueHolder> p) {
//			this.elts = p;
//		}

		public void addRefElt(AbstractObjectValueHolder p) {
			CollectionEltRefValueHolder refElt = new CollectionEltRefValueHolder(this, p);
			elts.add(refElt);
		}

	}

	/**
	 * ValuHolder for elements of CollectionValueHolder
	 */
	public static class CollectionEltRefValueHolder extends RefObjectValueHolder {

		// cf super: CollectionValueHolder from
		// 
		public CollectionEltRefValueHolder(CollectionValueHolder from) {
			super(from);
		}
		
		public CollectionEltRefValueHolder(CollectionValueHolder from, AbstractObjectValueHolder to) {
			this(from);
			setTo(to);
		}

		@Override
		public void visit(ValueHolderVisitor v) {
			v.caseCollectionElt(this);
		}

		@Override
		public <R, A> R visit(ValueHolderVisitor2<R, A> v, A arg) {
			return v.caseCollectionElt(this, arg);
		}
		
		public String toString() {
			return "EltRefVH[" + super.toString() + "]";
		}
	}
	

	// -------------------------------------------------------------------------
	
	
	/**
	 * 
	 */
	public static class MapValueHolder extends ObjectValueHolder {
		
		private Collection<MapEntryValueHolder> entries;
		
		public MapValueHolder() {
			this(HashMap.class);
		}

		public MapValueHolder(Class<?> type) {
			super(type);
			this.entries = new ArrayList<MapEntryValueHolder>();
		}

		public void visit(ValueHolderVisitor v) {
			v.caseMap(this);
		}		

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseMap(this, a);
		}
		
		public Collection<MapEntryValueHolder> getEntries() {
			return entries;
		}

//		public void setEntries(Collection<MapEntryValueHolder<K,T>> p) {
//			this.entries = p;
//		}

		public void putEntry(AbstractObjectValueHolder/*<K>*/ key, AbstractObjectValueHolder/*<T>*/ value) {
			MapEntryValueHolder e = new MapEntryValueHolder(this, key, value);
			this.entries.add(e);
		}

	}

	
	/**
	 *
	 */
	public static class MapEntryValueHolder extends AbstractObjectValueHolder {
		
		final Map2MapEntryRefValueHolder map2MapEntryRef;
		final MapEntryKeyRefValueHolder key = new MapEntryKeyRefValueHolder(this);
		final MapEntryValueRefValueHolder value = new MapEntryValueRefValueHolder(this);
		
		public MapEntryValueHolder(MapValueHolder from,
				AbstractObjectValueHolder keyTo,
				AbstractObjectValueHolder valueTo) {
			super(Map.Entry.class);
			this.map2MapEntryRef = new Map2MapEntryRefValueHolder(from, this);
			this.key.setTo(keyTo); 
			this.value.setTo(valueTo);
		}

		@Override
		public void visit(ValueHolderVisitor v) {
			v.caseMapEntry(this);
		}

		@Override
		public <R, A> R visit(ValueHolderVisitor2<R, A> v, A arg) {
			return v.caseMapEntry(this, arg);
		}		

		public MapValueHolder getFromMap() {
			return map2MapEntryRef.getFromMap();
		}
		
		public MapEntryKeyRefValueHolder getKeyRef() {
			return key;
		}

		public MapEntryValueRefValueHolder getValueRef() {
			return value;
		}

		public AbstractObjectValueHolder getValue() {
			return value.getTo();
		}

		public AbstractObjectValueHolder getKey() {
			return key.getTo();
		}

		public void setValueTo(AbstractObjectValueHolder valueTo) {
			this.value.setTo(valueTo);
		}

	}

	/**
	 * 
	 */
	public static class Map2MapEntryRefValueHolder extends RefObjectValueHolder {

		public Map2MapEntryRefValueHolder(MapValueHolder from, MapEntryValueHolder to) {
			super(from);
		}

		@Override
		public void visit(ValueHolderVisitor v) {
			// internal... do nothing! v.caseMap2MapEntry(this);
		}

		@Override
		public <R, A> R visit(ValueHolderVisitor2<R, A> v, A arg) {
			// internal... do nothing! return v.caseMap2MapEntry(this, arg);
			return null;
		}
		
		public MapValueHolder getFromMap() {
			return (MapValueHolder) getFrom();
		}
		public MapEntryValueHolder getToMapEntry() {
			return (MapEntryValueHolder) getTo();
		}
	}

	/**
	 * 
	 */
	public static class MapEntryKeyRefValueHolder extends RefObjectValueHolder {

		public MapEntryKeyRefValueHolder(MapEntryValueHolder from) {
			super(from);
		}

		@Override
		public void visit(ValueHolderVisitor v) {
			v.caseMapEntryKey(this);
		}

		@Override
		public <R, A> R visit(ValueHolderVisitor2<R, A> v, A arg) {
			return v.caseMapEntryKey(this, arg);
		}

		public MapValueHolder getFromMap() {
			return getFromMapEntry().getFromMap();
		}
		public MapEntryValueHolder getFromMapEntry() {
			return (MapEntryValueHolder) getFrom();
		}
		
	}

	/**
	 * 
	 */
	public static class MapEntryValueRefValueHolder extends RefObjectValueHolder {

		public MapEntryValueRefValueHolder(AbstractObjectValueHolder from) {
			super(from);
		}

		@Override
		public void visit(ValueHolderVisitor v) {
			v.caseMapEntryValue(this);
		}

		@Override
		public <R, A> R visit(ValueHolderVisitor2<R, A> v, A arg) {
			return v.caseMapEntryValue(this, arg);
		}
		
		public MapValueHolder getFromMap() {
			return getFromMapEntry().getFromMap();
		}
		public MapEntryValueHolder getFromMapEntry() {
			return (MapEntryValueHolder) getFrom();
		}
	}

}

