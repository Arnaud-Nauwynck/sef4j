package com.google.code.joto.ast.accesspath;

import com.google.code.joto.ast.accesspath.AccessPathAST.AggregateFieldCollectionAccess;
import com.google.code.joto.ast.accesspath.AccessPathAST.AggregateFieldFieldAccess;
import com.google.code.joto.ast.accesspath.AccessPathAST.AggregateFieldMapAccess;
import com.google.code.joto.ast.accesspath.AccessPathAST.ArrayIndexAccess;
import com.google.code.joto.ast.accesspath.AccessPathAST.ClassFieldAccess;
import com.google.code.joto.ast.accesspath.AccessPathAST.CollectionAccess;
import com.google.code.joto.ast.accesspath.AccessPathAST.CompoundPathAccess;
import com.google.code.joto.ast.accesspath.AccessPathAST.MapAccess;
import com.google.code.joto.ast.accesspath.AccessPathAST.ObjectFieldAccess;

/**
 * Visitor for AcessPathEltAST class hierarchy
 */
public interface AccessPathVisitor {
	
	void caseObjectField(ObjectFieldAccess p);
	void caseArrayIndex(ArrayIndexAccess p);
	void caseClassStaticField(ClassFieldAccess p);

	// non built-in Collection/Map type access
	void caseCollectionAccess(CollectionAccess p);
	void caseMapAccess(MapAccess mapAccess);

	// Compound / Chained design pattern: path = pathElt1/pathElt2/...pathEltN
	// output of path_i = input of path_i+1
	void caseCompoundPath(CompoundPathAccess p);

	// aggregate for partial SCC object...
	// same as Coumpound, but for level=2 ... and giving compile-time access restriction to methods
	void caseAggrFieldField(AggregateFieldFieldAccess p);
	void caseAggrFieldCollection(AggregateFieldCollectionAccess p);
	void caseAggrFieldMap(AggregateFieldMapAccess p);
	
}
