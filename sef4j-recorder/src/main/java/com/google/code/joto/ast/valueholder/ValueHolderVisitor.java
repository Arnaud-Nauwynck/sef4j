package com.google.code.joto.ast.valueholder;

import com.google.code.joto.ast.valueholder.ValueHolderAST.ArrayEltRefValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.CollectionEltRefValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.CollectionValueHolder;
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

/**
 * Visitor design pattern for ValueHolderAST class hierarchy 
 */
public interface ValueHolderVisitor {

	void caseObject(ObjectValueHolder p);
	void casePrimitiveField(PrimitiveFieldValueHolder node);
	void caseRefField(RefFieldValueHolder node);

	void casePrimitiveArray(PrimitiveArrayValueHolder<?> p);
	void casePrimitiveArrayElt(PrimitiveArrayEltValueHolder<?> p);
	
	void caseRefArray(RefArrayValueHolder p);
	void caseRefArrayElt(ArrayEltRefValueHolder p);


	// non primitive helper sub-classes of ObjectValueHolder
	void caseImmutableObjectValue(ImmutableObjectValueHolder p);

	void caseCollection(CollectionValueHolder p);
	void caseCollectionElt(CollectionEltRefValueHolder p);

	void caseMap(MapValueHolder p);
	void caseMapEntry(MapEntryValueHolder p);
	void caseMapEntryKey(MapEntryKeyRefValueHolder p);
	void caseMapEntryValue(MapEntryValueRefValueHolder p);

	
}
