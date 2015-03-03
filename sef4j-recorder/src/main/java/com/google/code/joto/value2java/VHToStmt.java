package com.google.code.joto.value2java;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.code.joto.JotoConfig;
import com.google.code.joto.ast.beanstmt.BeanAST;
import com.google.code.joto.ast.beanstmt.BeanAST.AssignExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.BeanExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.BeanStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.ExprStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.IndexedArrayExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.LiteralExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.MethodApplyExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.NewArrayExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.NewObjectExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.SimpleNameExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.VarDeclStmt;
import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ArrayEltRefValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.CollectionEltRefValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.CollectionValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.FieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ImmutableObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.MapEntryKeyRefValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.MapEntryValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.MapEntryValueRefValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.MapValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveArrayEltValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveArrayValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveFieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefArrayValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefFieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderVisitor2;
import com.google.code.joto.reflect.ClassDictionaryJotoInfo;
import com.google.code.joto.reflect.ClassJotoInfo;
import com.google.code.joto.reflect.ConstructorJotoInfo;
import com.google.code.joto.reflect.ParamToFieldInfo;
import com.google.code.joto.util.NameGenerator;
import com.google.code.joto.value2java.impl.ObjectStmtInfo;

/**
 *
 */
public class VHToStmt implements ValueHolderVisitor2<BeanAST,ObjectStmtInfo> {

	private Map<AbstractObjectValueHolder,ObjectStmtInfo> objStmtInfoMap = 
		new IdentityHashMap<AbstractObjectValueHolder,ObjectStmtInfo>();
	
	private NameGenerator nameGenerator = new NameGenerator(); 
	
	private VarDeclStmt logVarDeclStmt;

	private JotoConfig config;
	
	// may move in JotoContext??? (but not JotoConfig since nothing is supposed to be persistent / serializable)
	// contains mainly static cached data, computed from Class
	private ClassDictionaryJotoInfo classDicInfo = ClassDictionaryJotoInfo.getDefaultInstance();
	
	// -------------------------------------------------------------------------
	
	public VHToStmt(JotoConfig config) {
		this.config = config;
	}
	
	// ------------------------------------------------------------------------

	public Map<AbstractObjectValueHolder, ObjectStmtInfo> getResultObjInitInfoMap() {
		return objStmtInfoMap;
	}
	
	public JotoConfig getConfig() {
		return config;
	}

	public void setConfig(JotoConfig config) {
		this.config = config;
	}

//	public VHToStmtConverterLookup getVhToStmtConverterLookup() {
//		return vhToStmtConverterLookup;
//	}
//
//	public void setVhToStmtConverterLookup(VHToStmtConverterLookup p) {
//		this.vhToStmtConverterLookup = p;
//	}

	public void visitRootObject(AbstractObjectValueHolder objVH, String name) {
		objToInitInfo(objVH, name);
	}
	
	public ClassJotoInfo getClassInfo(Class<?> objClass) {
		return classDicInfo.getClassInfo(objClass);
	}

	// implements ValueHolderVisitor2
	// -------------------------------------------------------------------------

	@Override
	public BeanAST caseObject(ObjectValueHolder p, ObjectStmtInfo objInfo) {
		BeanExpr initExpr;
		Class<?> objClass = p.getObjClass();
		
		
		ObjectVHToStmtConverter converter = config.lookupConverter(objClass);
		if (converter != null && converter.canConvert(objClass)) {
			converter.convert(this, objInfo.getObjectVH(), objInfo); // ??? TOCHECK
			
			initExpr = objInfo.getVarDeclStmt().getInitExpr(); // ??
		} else {
			
			Map<Field,FieldValueHolder> fieldsToSet = new HashMap<Field,FieldValueHolder>(p.getFieldsValuesMap());
			
			// choose one ctor, public, with @ConstructorProperties..
			ClassJotoInfo classInfo = getClassInfo(objClass);
			// List<ConstructorJotoInfo> ctorInfos = classInfo.getConstructorInfos();
			ConstructorJotoInfo ctorInfo = classInfo.choosePublicCtorWithInfo();
	
			List<BeanExpr> ctorExprs = new ArrayList<BeanExpr>();
			if (ctorInfo != null) {
				List<ParamToFieldInfo> ctorParamToFieldInfos = ctorInfo.getParamToFieldInfos();
				for(ParamToFieldInfo elt : ctorParamToFieldInfos) {
					Field field = elt.getTargetAssignedField();
					FieldValueHolder vh = fieldsToSet.remove(field);
					// convert field -> value -> BeanExpr   (not BeanStmt using visitor)
					BeanExpr valueExpr;
					if (vh instanceof PrimitiveFieldValueHolder) {
						PrimitiveFieldValueHolder vh2 = (PrimitiveFieldValueHolder) vh;
						Object value = vh2.getValue();
						valueExpr = new LiteralExpr(value);
					} else { // (vh instanceof RefObjectFieldValueNode)
						RefFieldValueHolder vh2 = (RefFieldValueHolder) vh;
						AbstractObjectValueHolder fieldVH = vh2.getTo();
						String prefixName = field.getName();
						valueExpr = objToLhsExpr(fieldVH, prefixName);
					}
					ctorExprs.add(valueExpr);
				}
			}				
			// use field values expr as ctor parameters
			initExpr = doNewDefaultObjInstanceExpr(objInfo, p, ctorExprs);
			doSetObjInitExpr(objInfo, initExpr);
	
// TODO TEMPORARY HACK
String className = objClass.getName();
final boolean recurseInObj = !(className.startsWith("com.lyxor.model.businessobject."));
if (!recurseInObj) {
	return initExpr;
}

			// convert remaining field values to setter stmt
// TODO TEMPORARY HACK: sort by field name

			for(Map.Entry<Field,FieldValueHolder> e : fieldsToSet.entrySet()) {
				FieldValueHolder fieldVH = e.getValue();
				BeanStmt fieldStmt = (BeanStmt) fieldVH.visit(this, objInfo);
				objInfo.addInitStmt(fieldStmt);
			}
		}
		return initExpr;
	}
	
	@Override
	public BeanAST casePrimitiveField(PrimitiveFieldValueHolder p, ObjectStmtInfo objInfo) {
		BeanExpr lhsExpr = objToLhsExpr(objInfo);
		BeanExpr argExpr = new LiteralExpr(p.getValue());

		return doCaseFieldSetterExprStmt(p.getField(), lhsExpr, argExpr);
	}

	@Override
	public BeanAST caseRefField(RefFieldValueHolder p, ObjectStmtInfo objInfo) {
		BeanExpr lhsExpr = objToLhsExpr(objInfo);
		String namePrefix = p.getField().getName();
		AbstractObjectValueHolder refVH = p.getTo();

		// ** recurse **
		ObjectStmtInfo refObjInfo = objToInitInfo(refVH, namePrefix);
		
		BeanExpr argExpr = objToLhsExpr(refObjInfo);

		return doCaseFieldSetterExprStmt(p.getField(), lhsExpr, argExpr);
	}

	private BeanStmt doCaseFieldSetterExprStmt(Field field,
			BeanExpr lhsExpr, BeanExpr argExpr) {
		// find corresponding setter for initializing "obj.setField(value)"
		PropertyDescriptor prop = findPropertyDesc(field);
		String setterName;
		if (prop == null) {
			return newLogWarnStmt("prop not found for field " + field);	
		} else if (prop.getWriteMethod() == null) {
			return newLogWarnStmt("writeMethod not found for prop " + field);	
		} else if (prop.getWriteMethod().isAccessible()) {
			return newLogWarnStmt("writeMethod not accessible for prop " + field);	
		} else {
			setterName = prop.getWriteMethod().getName();
		}			 
		BeanExpr expr = new MethodApplyExpr(lhsExpr, setterName, argExpr);
		return new ExprStmt(expr);
	}


	@Override
	public BeanAST caseImmutableObjectValue(ImmutableObjectValueHolder p, ObjectStmtInfo objInfo) {
		BeanExpr initExpr;
		if (p.getObjClass().equals(String.class)) {
			initExpr = new LiteralExpr(p.getValue());
		} else {
			BeanExpr ctorArg = new LiteralExpr(p.getValue());
			initExpr = doNewDefaultObjInstanceExpr(objInfo, p, ctorArg);
		}
		doSetObjInitExpr(objInfo, initExpr);
		return initExpr;
	}
	
	@Override
	public BeanAST caseCollection(CollectionValueHolder p, ObjectStmtInfo objInfo) {
		BeanExpr initExpr = doNewDefaultObjInstanceExpr(objInfo, p);
		doSetObjInitExpr(objInfo, initExpr);
		
		BeanExpr lsExpr = objToLhsExpr(objInfo);
		String addMethodName = "add";
		
		Collection<CollectionEltRefValueHolder> eltVHs = p.getEltRefs();
		String eltNamePrefix = objInfo.getVarNameWithSuffix("Elt");
		
		int eltIndex = 0;
		for(CollectionEltRefValueHolder eltVH : eltVHs) {
			String indexedEltNamePrefix = eltNamePrefix + eltIndex; // ??
			// *** recurse ***
			BeanExpr eltExpr = (BeanExpr) 
				// eltVH.visit(this, objInfo); ??? use name??
				objToLhsExpr(eltVH.getTo(), indexedEltNamePrefix);
			
			MethodApplyExpr eltAddExpr = new MethodApplyExpr(lsExpr, addMethodName, eltExpr); 
			objInfo.addInitStmt(new ExprStmt(eltAddExpr));
			eltIndex++;
		}
		return initExpr;
	}

	@Override
	public BeanAST caseCollectionElt(CollectionEltRefValueHolder p, ObjectStmtInfo objInfo) {
		// NOT USED, cf caseCollection()!
		BeanExpr eltExpr = objToLhsExpr(p.getTo(), null);
		return eltExpr;
	}
	
	@Override
	public BeanAST caseMap(MapValueHolder p, ObjectStmtInfo objInfo) {
		BeanExpr initExpr = doNewDefaultObjInstanceExpr(objInfo, p);
		doSetObjInitExpr(objInfo, initExpr);

		BeanExpr lsExpr = objToLhsExpr(objInfo);
		String keyNamePrefix = objInfo.getVarNameWithSuffix("Key");
		String valueNamePrefix = objInfo.getVarNameWithSuffix("Value");
		String putMethodName = "put";
		
		Collection<MapEntryValueHolder> entryVHs = p.getEntries();
		for(MapEntryValueHolder entryVH : entryVHs) {
			// *** recurse ***
			BeanExpr keyExpr = objToLhsExpr(entryVH.getKey(), keyNamePrefix);
			BeanExpr valueExpr = objToLhsExpr(entryVH.getValue(), valueNamePrefix);
			
			MethodApplyExpr eltAddExpr = new MethodApplyExpr(lsExpr, putMethodName, keyExpr, valueExpr); 
			objInfo.addInitStmt(new ExprStmt(eltAddExpr));
		}
		return initExpr;
	}

	

	@Override
	public BeanAST caseMapEntry(MapEntryValueHolder p, ObjectStmtInfo arg) {
		// NOT USED, cf caseMap()!
		return null;
	}

	@Override
	public BeanAST caseMapEntryKey(MapEntryKeyRefValueHolder p, ObjectStmtInfo arg) {
		// NOT USED, cf caseMap()!
		return null;
	}

	@Override
	public BeanAST caseMapEntryValue(MapEntryValueRefValueHolder p, ObjectStmtInfo arg) {
		// NOT USED, cf caseMap()!
		return null;
	}

	@Override
	public BeanAST casePrimitiveArray(PrimitiveArrayValueHolder<?> p, ObjectStmtInfo objInfo) {
		PrimitiveArrayEltValueHolder<?>[] arrayVH = p.getHolderArray();
		int len = arrayVH.length;
		Class<?> compClass = p.getObjClass().getComponentType();
		BeanExpr initExpr = new NewArrayExpr(compClass, len);
		doSetObjInitExpr(objInfo, initExpr);
		BeanExpr lhsArrayExpr = objToLhsExpr(objInfo);
		
		for(int i = 0; i < len; i++) {
			PrimitiveArrayEltValueHolder<?> eltVH = arrayVH[i];
			// *** recurse ***
			BeanExpr eltExpr = new LiteralExpr(eltVH.getValue());
			// TODO test if it is not necessary to set default values.... array[i] = 0; 0l; 0.0f; 0.0; false; '0' ...
			boolean setToDefault = false;
			if (!setToDefault) {
				// stmt for "array[i] = expr"
				ExprStmt assignIndexStmt = newAssignArrayIndexStmt(lhsArrayExpr, i, eltExpr);
				objInfo.addInitStmt(assignIndexStmt);
			}
		}
		return initExpr;
	}

	@Override
	public BeanAST caseRefArray(RefArrayValueHolder p, ObjectStmtInfo objInfo) {
		AbstractObjectValueHolder[] eltsVH = p.getElts();
		int len = eltsVH.length;
		Class<?> compClass = p.getObjClass().getComponentType();
		BeanExpr initExpr = new NewArrayExpr(compClass, len);
		doSetObjInitExpr(objInfo, initExpr);
		BeanExpr lhsArrayExpr = objToLhsExpr(objInfo);
		
		String arrayEltNamePrefix = objInfo.getVarNameWithSuffix("Elt");
		for(int i = 0; i < len; i++) {
			AbstractObjectValueHolder eltVH = eltsVH[i];
			// *** recurse ***
			BeanExpr eltExpr = objToLhsExpr(eltVH, arrayEltNamePrefix);
			// test if it is not necessary to set default values: null
			boolean setToDefault = (eltExpr == null || 
					((eltExpr instanceof LiteralExpr) && ((LiteralExpr)eltExpr).getValue() == null));
			if (!setToDefault) {
				// stmt for "array[i] = expr"
				ExprStmt assignIndexStmt = newAssignArrayIndexStmt(lhsArrayExpr, i, eltExpr);
				objInfo.addInitStmt(assignIndexStmt);
			}
		}
		return initExpr;
	}

	private ExprStmt newAssignArrayIndexStmt(BeanExpr lhsArrayExpr, int index, BeanExpr eltExpr) {
		IndexedArrayExpr lhs = new IndexedArrayExpr(lhsArrayExpr, new LiteralExpr(index));
		ExprStmt assignIndexStmt = new ExprStmt(new AssignExpr(lhs, eltExpr));
		return assignIndexStmt;
	}
	
	
	@Override
	public BeanAST casePrimitiveArrayElt(PrimitiveArrayEltValueHolder<?> p, ObjectStmtInfo objInfo) {
		// NOT USED ... see casePrimitiveArray() 
		return null;
	}
	
	@Override
	public BeanAST caseRefArrayElt(ArrayEltRefValueHolder p, ObjectStmtInfo objInfo) {
		// NOT USED ... see caseRefArray() 
		return null;
	}
	
	// -------------------------------------------------------------------------
	
	protected ObjectStmtInfo objToInitInfo(AbstractObjectValueHolder objVH, String optGeneratePrefixName) {
		if (objVH == null) {
			return null;
		}
		ObjectStmtInfo res = objStmtInfoMap.get(objVH);
		if (res == null) {
			res = new ObjectStmtInfo(objVH);
			objStmtInfoMap.put(objVH, res);

			if (optGeneratePrefixName != null) {
				checkGeneratedVarName(res, optGeneratePrefixName);
			}
			
			// *** recurse (non lazy) ****
			BeanExpr initExpr = (BeanExpr) objVH.visit(this, res);
			doSetObjInitExpr(res, initExpr); // ... TOCHECK already set?
		}
		return res;
	}

	protected void doSetObjInitExpr(ObjectStmtInfo res, BeanExpr initExpr) {
		Class<?> objType = res.getObjectVH().getObjClass();
		res.setTypeAndInitExpr(objType, initExpr);	
	}
	
	protected ObjectStmtInfo objToInitInfo(AbstractObjectValueHolder objVH) {
		ObjectStmtInfo res = objToInitInfo(objVH, null);
		return res;
	}

	protected void checkGeneratedVarName(ObjectStmtInfo objInfo, String optGeneratePrefixName) {
		if (objInfo == null) {
			return;
		}
		if (objInfo.getVarName() == null) {
			String varName = nameGenerator.newName(optGeneratePrefixName);
			objInfo.setVarName(varName);
		}
	}

	protected BeanExpr objToLhsExpr(AbstractObjectValueHolder objVH, String optGeneratePrefixName) {
		if (objVH == null) return new LiteralExpr(null);
		ObjectStmtInfo objInfo = objToInitInfo(objVH);
		if (objInfo.getVarName() == null) {
			if (optGeneratePrefixName == null) {
				optGeneratePrefixName = "tmp";
			}
			checkGeneratedVarName(objInfo, optGeneratePrefixName);
		}
		return objToLhsExpr(objInfo);
	}

	protected BeanExpr objToLhsExpr(ObjectStmtInfo objInfo) {
		if (objInfo == null) {
			return new LiteralExpr(null);
		}
		return new SimpleNameExpr(objInfo.getVarDeclStmt());
	}
	
	private NewObjectExpr doNewDefaultObjInstanceExpr(
			ObjectStmtInfo objInfo, 
			AbstractObjectValueHolder p, 
			BeanExpr... optArgs
			) {
		if (objInfo.getVarName() == null) {
			String classAlias = nameGenerator.classToAlias(p.getObjClass());
			checkGeneratedVarName(objInfo, classAlias);
		}
		NewObjectExpr initExpr = new NewObjectExpr(p.getObjClass(), optArgs);
		return initExpr;
	}

	private NewObjectExpr doNewDefaultObjInstanceExpr(
			ObjectStmtInfo objInfo, 
			AbstractObjectValueHolder p, 
			List<BeanExpr> optArgs
			) {
		if (objInfo.getVarName() == null) {
			String classAlias = nameGenerator.classToAlias(p.getObjClass());
			checkGeneratedVarName(objInfo, classAlias);
		}
		NewObjectExpr initExpr = new NewObjectExpr(p.getObjClass(), optArgs);
		return initExpr;
	}

	public static PropertyDescriptor findPropertyDesc(Field field) {
		PropertyDescriptor res = null;
		String fieldName = field.getName();
		if (fieldName.startsWith("_")) {
			fieldName = fieldName.substring(1); // standard convention on field "_foo" => getFoo()...
		}
		Class<?> beanClass = field.getDeclaringClass();
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(beanClass);
		} catch(Exception ex) {
			return null; // ??? SHOULD NOT OCCUR
		}
		for(PropertyDescriptor prop : beanInfo.getPropertyDescriptors()) {
			if (prop.getName().equals(fieldName)) {
				res = prop;
				break;
			}
		}
		return res;
	}

	private BeanStmt newLogWarnStmt(String msg) {
		if (logVarDeclStmt == null) {
			logVarDeclStmt = new VarDeclStmt(Logger.class, "log", null);
		}
		BeanExpr logFieldExpr = new SimpleNameExpr(logVarDeclStmt);
		String logMethName = "warn";
		BeanExpr logArgMsg = new LiteralExpr(msg);
		BeanExpr methExpr = new MethodApplyExpr(logFieldExpr, logMethName, logArgMsg);
		return new ExprStmt(methExpr );
	}

	public VarDeclStmt getLogVarDeclStmt() {
		return logVarDeclStmt;
	}

}
