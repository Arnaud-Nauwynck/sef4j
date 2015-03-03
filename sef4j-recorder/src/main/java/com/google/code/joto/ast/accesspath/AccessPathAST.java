package com.google.code.joto.ast.accesspath;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.code.joto.reflect.ReflectUtils;


/**
 * root class hierarchy of AST for Object Access Path traversal
 * 
 * 
 * built-in Access Path:
 *   Object --Field--> Object/Primitive
 *   Class  --StaticField--> Object/Primitive
 *   ObjectArray --[index]--> ArrayElt Object/Primitive
 *   ObjectArray --.length--> primitive ArrayLength
 *   
 * non-built-in AccessPath: (cf implementation using Array / LinkedList...)
 *   Collection --.get(index)/.add()/.remove()/.iterator()--> Object CollectionElt
 *   Map[key] --.get(key)/.put()/.remove()/.iterator()--> Object Map.Entry value
 *   
 *   CompountPath  --elt1 o elt2 o ... o eltN--> Object
 *   
 *   AggregateField:  Object --Field1 o Field2--> Object/Primitive
 *   AggregateObjectArray: Object --Field o [index]--> Object/Primitive
 *   AggregateCollection: Object --Field o .get(index)/.add()/.remove()/.iterator()--> Object     
 *   AggregateMap: Object --Field o .get(key)/.put()/.remove()/.iterator()--> Object
 *   
 *   Inverse Relation Object:  child-->implicit parent  :  Object--add/remove(child)--> .... side effect: child.setParent(object)  
 *   
 * Note:
 * non-built-in PathElt can be partially accessible, with .add() only and not .get() ...
 * exemple in JavaBeans  addListener()/removeListener() ... but no getListener() !!
 *   
 * 
 */
public abstract class AccessPathAST {

	protected Class<?> lhsType;
	protected Class<?> resultType;
	
	// -------------------------------------------------------------------------
	
	protected AccessPathAST(Class<?> lhsType, Class<?> resultType) {
		this.lhsType = lhsType;
		this.resultType = resultType;
	}
	
	public abstract void visit(AccessPathVisitor v);
	
	// -------------------------------------------------------------------------
	
	protected static class PropertyMethods {
		private Method readMethod;
		private Method writeMethod;

		public PropertyMethods(Class<?> clss, Class<?> fieldType, String propName) {
			this.readMethod = ReflectUtils.findMethod(clss, "get" + propName);
			if (readMethod == null && fieldType.equals(boolean.class)){
				readMethod = ReflectUtils.findMethod(clss, "is" + propName);
			}
			this.writeMethod = ReflectUtils.findMethod(clss, "set" + propName, fieldType);
		}

		public Method getReadMethod() {
			return readMethod;
		}
		public Method getWriteMethod() {
			return writeMethod;
		}
		public Method getPublicReadMethod() {
			return ReflectUtils.methodIfPublic(readMethod);
		}
		public Method getPublicWriteMethod() {
			return ReflectUtils.methodIfPublic(writeMethod);
		}

		
	}
	
	/**
	 * 
	 */
	public static abstract class AbstractFieldPathElt extends AccessPathAST {
		
		private final Field field;
		private PropertyMethods methods;
		
		public AbstractFieldPathElt(Field field) {
			super(field.getDeclaringClass(), field.getType());
			this.field = field;

			String propName = ReflectUtils.fieldToCapitalizedName(field);
			this.methods = new PropertyMethods(field.getDeclaringClass(), field.getType(), propName);
		}

		public Field getField() {
			return field;
		}
		
		public String getFieldName() {
			return field.getName();
		}
		
		public PropertyMethods getMethods() {
			return methods;
		}

		public Method getReadMethod() {
			return methods.getReadMethod();
		}
		public Method getWriteMethod() {
			return methods.getWriteMethod();
		}
		public Method getPublicReadMethod() {
			return methods.getPublicReadMethod();
		}
		public Method getPublicWriteMethod() {
			return methods.getPublicWriteMethod();
		}
		
	}

	/**
	 * 
	 */
	public static class ObjectFieldAccess extends AbstractFieldPathElt {

		public ObjectFieldAccess(Field field) {
			super(field);
		}

		public void visit(AccessPathVisitor v) {
			v.caseObjectField(this);
		}

	}

	/**
	 * 
	 */
	public static class ClassFieldAccess extends AbstractFieldPathElt {

		public ClassFieldAccess(Field field) {
			super(field);
		}

		public void visit(AccessPathVisitor v) {
			v.caseClassStaticField(this);
		}

	}

	/**
	 * AccessPathAST sub-class for   Object[] ---index---> ArrayElt
	 */
	public static class ArrayIndexAccess extends AccessPathAST {
		
		public ArrayIndexAccess(Class<?> lhsArrayType) {
			super(lhsArrayType, lhsArrayType.getComponentType());
		}
		
		public void visit(AccessPathVisitor v) {
			v.caseArrayIndex(this);
		}
		
	}
	
	
	// Non built-in AccessPath, for standard java.util. Collection / Map
	// -------------------------------------------------------------------------
	
	public static class CollectionMethods {
		private Method iteratorMethod;
		private Method getIndexedMethod;
		private Method addMethod;
//		private Method addIndexedMethod;
//		private Method setIndexedMethod;
//		private Method removeMethod;

		public CollectionMethods(Class<?> clss) {
			this.iteratorMethod = ReflectUtils.findMethod(clss, "iterator");
			this.getIndexedMethod = ReflectUtils.findMethod(clss, "get", int.class);
			this.addMethod = ReflectUtils.findMethod(clss, "add");
		}

		public Method getIteratorMethod() {
			return iteratorMethod;
		}

		public Method getGetIndexedMethod() {
			return getIndexedMethod;
		}

		public Method getAddMethod() {
			return addMethod;
		}

	}
	
	/**
	 * 
	 */
	public static class CollectionAccess extends AccessPathAST {
		
		private CollectionMethods methods;
		
		public CollectionAccess(Class<?> collectionClass, Class<?> eltType) {
			super(collectionClass, eltType);
			this.methods = new CollectionMethods(collectionClass);
		}

		public void visit(AccessPathVisitor v) {
			v.caseCollectionAccess(this);
		}

		public CollectionMethods getMethods() {
			return methods;
		}
		
	}

	// -------------------------------------------------------------------------
	
	public static class MapMethods {
		private Method entrySetMethod;
		private Method valuesMethod;
		private Method getMethod;
		private Method putMethod;

		public MapMethods(Class<?> clss) {
			this.entrySetMethod = ReflectUtils.findMethod(clss, "entrySet");
			this.valuesMethod = ReflectUtils.findMethod(clss, "values");
			this.getMethod = ReflectUtils.findMethod(clss, "get", Object.class);
			this.putMethod = ReflectUtils.findMethod(clss, "put", Object.class, Object.class);
		}

		public Method getEntrySetMethod() {
			return entrySetMethod;
		}

		public Method getValuesMethod() {
			return valuesMethod;
		}

		public Method getGetMethod() {
			return getMethod;
		}

		public Method getPutMethod() {
			return putMethod;
		}
		
	}

	/**
	 * 
	 */
	public static class MapAccess extends AccessPathAST {
		
		private MapMethods methods;
		
		public MapAccess(Class<?> collectionClass, Class<?> eltType) {
			super(collectionClass, eltType);
			this.methods = new MapMethods(collectionClass);
		}

		public void visit(AccessPathVisitor v) {
			v.caseMapAccess(this);
		}

		public MapMethods getMethods() {
			return methods;
		}
		
	}	
	
	// Compound design pattern: path = pathElt1/pathElt2/...pathEltN
	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class CompoundPathAccess extends AccessPathAST {
		
		private List<AccessPathAST> pathElts = new ArrayList<AccessPathAST>();
		
		public CompoundPathAccess(Class<?> lhsType, Class<?> resultType, AccessPathAST... pathElts) {
			this(lhsType, resultType, Arrays.asList(pathElts));
		}

		public CompoundPathAccess(Class<?> lhsType, Class<?> resultType, List<AccessPathAST> pathElts) {
			super(lhsType, resultType);
			this.pathElts.addAll(pathElts);
		}
		
		@Override
		public void visit(AccessPathVisitor v) {
			v.caseCompoundPath(this);
		}

		public List<AccessPathAST> getPathElts() {
			return pathElts;
		}
		
	}
	
	// Aggregate Partial Field Path
	// used for example as SCC objects : Secundary Citizen Class
	// -------------------------------------------------------------------------

	/**
	 * 
	 */
	public static class AggregateFieldFieldAccess extends AccessPathAST {

		private ObjectFieldAccess parentFieldAccess;
		private ObjectFieldAccess childFieldAccess;
		private String aggrFieldName;
		private PropertyMethods methods;
		
		public AggregateFieldFieldAccess(
				ObjectFieldAccess parentFieldAccess,
				ObjectFieldAccess childFieldAccess
				) {
			super(parentFieldAccess.lhsType, childFieldAccess.resultType);
			// extends ObjectFieldAccess + override init for finding/hidding partial ??
			// ... or extends AccessPathAST + delegate... 
			this.parentFieldAccess = parentFieldAccess;
			this.childFieldAccess = childFieldAccess;
			
			this.aggrFieldName = 
				ReflectUtils.fieldToCapitalizedName(parentFieldAccess.getFieldName())
				+ ReflectUtils.fieldToCapitalizedName(childFieldAccess.getFieldName()); 
			this.methods = new PropertyMethods(childFieldAccess.lhsType, childFieldAccess.resultType, aggrFieldName); 
		}

		@Override
		public void visit(AccessPathVisitor v) {
			v.caseAggrFieldField(this);
		}

		public ObjectFieldAccess getParentFieldAccess() {
			return parentFieldAccess;
		}

		public ObjectFieldAccess getChildFieldAccess() {
			return childFieldAccess;
		}

		public String getAggrFieldName() {
			return aggrFieldName;
		}

		public PropertyMethods getMethods() {
			return methods;
		}
		
	}

	/**
	 * 
	 */
	public static class AggregateFieldCollectionAccess extends AccessPathAST {

		private ObjectFieldAccess parentFieldAccess;
		private ObjectFieldAccess childFieldAccess;
		private String aggrFieldName;
		private CollectionMethods methods;
		
		public AggregateFieldCollectionAccess(
				ObjectFieldAccess parentFieldAccess,
				ObjectFieldAccess childFieldAccess
				) {
			super(parentFieldAccess.lhsType, childFieldAccess.resultType);
			// extends ObjectFieldAccess + override init for finding/hidding partial ??
			// ... or extends AccessPathAST + delegate... 
			this.parentFieldAccess = parentFieldAccess;
			this.childFieldAccess = childFieldAccess;
			
			this.aggrFieldName = 
				ReflectUtils.fieldToCapitalizedName(parentFieldAccess.getFieldName())
				+ ReflectUtils.fieldToCapitalizedName(childFieldAccess.getFieldName()); 
//TODO			this.methods = new CollectionMethods(childFieldAccess.lhsType, childFieldAccess.resultType, aggrFieldName); 
		}

		@Override
		public void visit(AccessPathVisitor v) {
			v.caseAggrFieldCollection(this);
		}

		public ObjectFieldAccess getParentFieldAccess() {
			return parentFieldAccess;
		}

		public ObjectFieldAccess getChildFieldAccess() {
			return childFieldAccess;
		}

		public String getAggrFieldName() {
			return aggrFieldName;
		}

		public CollectionMethods getMethods() {
			return methods;
		}
		
	}
	
	/**
	 * 
	 */
	public static class AggregateFieldMapAccess extends AccessPathAST {

		private ObjectFieldAccess parentFieldAccess;
		private ObjectFieldAccess childFieldAccess;
		private String aggrFieldName;
		private MapMethods methods;
		
		public AggregateFieldMapAccess(
				ObjectFieldAccess parentFieldAccess,
				ObjectFieldAccess childFieldAccess
				) {
			super(parentFieldAccess.lhsType, childFieldAccess.resultType);
			// extends ObjectFieldAccess + override init for finding/hidding partial ??
			// ... or extends AccessPathAST + delegate... 
			this.parentFieldAccess = parentFieldAccess;
			this.childFieldAccess = childFieldAccess;
			
			this.aggrFieldName = 
				ReflectUtils.fieldToCapitalizedName(parentFieldAccess.getFieldName())
				+ ReflectUtils.fieldToCapitalizedName(childFieldAccess.getFieldName()); 
//TODO			this.methods = new MapMethods(childFieldAccess.lhsType, childFieldAccess.resultType, aggrFieldName); 
		}

		@Override
		public void visit(AccessPathVisitor v) {
			v.caseAggrFieldMap(this);
		}

		public ObjectFieldAccess getParentFieldAccess() {
			return parentFieldAccess;
		}

		public ObjectFieldAccess getChildFieldAccess() {
			return childFieldAccess;
		}

		public String getAggrFieldName() {
			return aggrFieldName;
		}

		public MapMethods getMethods() {
			return methods;
		}
		
	}

}
