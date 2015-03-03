package com.google.code.joto.value2java;

import com.google.code.joto.ast.valueholder.ValueHolderAST.RefObjectValueHolder;

/**
 * delegate class for implementing "object->link->object to stmt" part of the algorithm in 
 * class ValueHolderToBeanASTStmt.
 * 
 * implementations sub-classes must be thread safe, and stateless,
 * but the owner ValueHolderToBeanASTStmt is used as a context/callback for processing.
 * 
 * 
 * This class is used to extends ObjectVHToStmtProcessor capabilities, 
 * for objects that are not accessible by references, and 
 * can not be converted without analysing the dependency links between objects.
 * 
 * Typical examples are objects protected/private in other objects, 
 * and partially accessible only.
 * example: 
 * <ul>
 * <li>aggregate objet</li>
 * <li>SCC object (=Second Citizen Class... usually for List/Set/Map in orms..)</li>
 * <li>inverse relationship objects... </li>
 * </ul>  
 * 
 * 
 * see also ObjectVHToStmtProcessor, for simpler conversions, 
 * where object is accessible by reference.
 */
public interface RefObjectVHToStmtConverter {

	public boolean canConvert(Class<?> fromType, String[] path, Class<?> toType);
	
	public void convert(VHToStmt owner,
			RefObjectValueHolder refObjToObj);
	
}
