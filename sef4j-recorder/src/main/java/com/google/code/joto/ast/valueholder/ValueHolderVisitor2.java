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

public interface ValueHolderVisitor2<R,A> {

//	R caseNull(A arg);

	R caseObject(ObjectValueHolder p, A arg);
	R casePrimitiveField(PrimitiveFieldValueHolder p, A arg);
	R caseRefField(RefFieldValueHolder p, A arg);
	
	R casePrimitiveArray(PrimitiveArrayValueHolder<?> p, A arg);
	R casePrimitiveArrayElt(PrimitiveArrayEltValueHolder<?> p, A arg);

	R caseRefArray(RefArrayValueHolder p, A arg);
	R caseRefArrayElt(ArrayEltRefValueHolder p, A arg);


	// non primitive helper sub-classes of ObjectValueHolder
	R caseImmutableObjectValue(ImmutableObjectValueHolder p, A arg);
	
	R caseCollection(CollectionValueHolder p, A arg);
	R caseCollectionElt(CollectionEltRefValueHolder p, A arg);
	
	R caseMap(MapValueHolder p, A arg);
	R caseMapEntry(MapEntryValueHolder p, A arg);
	R caseMapEntryKey(MapEntryKeyRefValueHolder p, A arg);
	R caseMapEntryValue(MapEntryValueRefValueHolder p, A arg);
	
}
