package com.google.code.joto.value2java;

import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;
import com.google.code.joto.value2java.impl.ObjectStmtInfo;


/**
 * delegate class for implementing "object to stmt" converter. 
 * used as part of the algorithm in class ValueHolderToBeanASTStmt.
 * 
 * implementations sub-classes must be thread safe, and stateless,
 * but the owner ValueHolderToBeanASTStmt is used as a context/callback for processing.
 */
public interface ObjectVHToStmtConverter {

	public boolean canConvert(Class<?> type);
	
	public void convert(VHToStmt owner,
			AbstractObjectValueHolder obj,
			ObjectStmtInfo objInfo);
	
}
