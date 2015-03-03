package com.google.code.joto.ast.valueholder.util;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderVisitor;

/**
 * PrettyPrinter for  AbstractObjectValueHolder to String representation.
 * <p/>
 * Implemented as a Visitor design pattern on AbstractObjectValueHolder class hierarchy
 */
public class ValueHolderPrettyPrinter implements ValueHolderVisitor {

	private PrintStream out;
	
	private boolean printLinksFrom;
	
	private int indent;
	
	private int refIdGenerator = 1;
	private Map<AbstractObjectValueHolder,Integer> obj2id =
		new IdentityHashMap<AbstractObjectValueHolder,Integer>();
	
	private Map<AbstractObjectValueHolder,Boolean> alreadySeenRefs =
		new IdentityHashMap<AbstractObjectValueHolder,Boolean>();
	
	// -------------------------------------------------------------------------
	
	public ValueHolderPrettyPrinter(PrintStream out) {
		this.out = out;
	}
	
	
	// -------------------------------------------------------------------------

	public boolean isPrintLinksFrom() {
		return printLinksFrom;
	}

	public void setPrintLinksFrom(boolean printLinksFrom) {
		this.printLinksFrom = printLinksFrom;
	}

	// -------------------------------------------------------------------------

	public int getObjId(AbstractObjectValueHolder p) {
		Integer refId = obj2id.get(p);
		if (refId == null) {
			int id = refIdGenerator++;
			refId = new Integer(id);
			obj2id.put(p, refId);
		}
		return refId.intValue();
	}

	public void visitOrPrintRef(AbstractObjectValueHolder p) {
		if (p == null) {
			return;
		}
		Boolean marked = alreadySeenRefs.get(p);
		Integer refId = getObjId(p);
		if (marked != null) {
			println(" object refId=" + refId);
			// no recurse!
		} else {
			alreadySeenRefs.put(p, Boolean.TRUE);
			println(" newobject id=" + refId);
			incrIndent();
			// recurse
			p.visit(this);
			decrIndent();
		}
	}
	
	protected void printObjLinksFrom(AbstractObjectValueHolder p) {
		List<RefObjectValueHolder> linksFrom = p.getLinksFrom();
		indentPrint("linksFrom " + linksFrom.size() + " elt");
		if (linksFrom != null && !linksFrom.isEmpty()) {
			if (linksFrom.size() > 1) {
				print("s");
			}
			print(": ");
			for (Iterator<RefObjectValueHolder> iter = linksFrom.iterator(); iter.hasNext();) {
				RefObjectValueHolder link = iter.next();
				AbstractObjectValueHolder from = link.getFrom();
				int fromId = getObjId(from);
				print(Integer.toString(fromId));
				if (iter.hasNext()) {
					print(", "); 
				}
			}
		}
		println();
	}

	// -------------------------------------------------------------------------
	
	@Override
	public void caseObject(ObjectValueHolder p) {
		indentPrintln("Object class:" + p.getObjClass().getName());
		incrIndent();
		printObjLinksFrom(p);
		for(Map.Entry<Field,FieldValueHolder> entry : p.getFieldsValuesMap().entrySet()) {
			indentPrint("field: " + entry.getKey().getType().getName() + " " + entry.getKey().getName());
			FieldValueHolder fvh = entry.getValue();
			fvh.visit(this);
		}
		decrIndent();
	}

	@Override
	public void casePrimitiveField(PrimitiveFieldValueHolder node) {
		Object value = node.getValue();
		print(" = " + value);
		println();
	}

	@Override
	public void caseRefField(RefFieldValueHolder node) {
		AbstractObjectValueHolder refVH = node.getTo();
		if (refVH == null) {
			println(" = Null");
		} else if (refVH != null) {
			visitOrPrintRef(refVH);
		}
	}

	@Override
	public void casePrimitiveArray(PrimitiveArrayValueHolder<?> p) {
		PrimitiveArrayEltValueHolder<?>[] array = p.getHolderArray();
		int len = array.length;
		print(p.getObjClass().getComponentType().getName() + "[" + len + "] = { ");
		for (int i = 0; i < len; i++) {
			PrimitiveArrayEltValueHolder<?> elt = array[i];
			elt.visit(this);
			if (i + 1 < len) {
				print(", ");
			}
		}
		println(" }");
	}

	@Override
	public void casePrimitiveArrayElt(PrimitiveArrayEltValueHolder<?> p) {
		Object valueElt = p.getValue();
		if (valueElt != null) {
			print(valueElt.toString());
		} else {
			print("ERROR null primitive value");
		}
	}
	
	@Override
	public void caseRefArray(RefArrayValueHolder p) {
		AbstractObjectValueHolder[] array = p.getElts();
		int len = array.length;
		indentPrintln("object array " + p.getObjClass().getComponentType().getName() + "[" + len + "] = { ");
		for (int i = 0; i < len; i++) {
			AbstractObjectValueHolder elt = array[i];
			indentPrint("elt[" + i + "/" + len + ": ");
			elt.visit(this);
		}
		println(" }");
	}

	@Override
	public void caseRefArrayElt(ArrayEltRefValueHolder p) {
		if (p.getTo() != null) {
			visitOrPrintRef(p.getTo());
		} else {
			print("Null");
		}
	}
	
	@Override
	public void caseImmutableObjectValue(ImmutableObjectValueHolder p) {
		Object value = p.getValue();
		indentPrintln("" + value.getClass().getName() + " " + value);
	}
	

	@Override
	public void caseCollection(CollectionValueHolder p) {
		Collection<CollectionEltRefValueHolder> elts = p.getEltRefs();
		int len = elts.size();
		indentPrintln("collection (" + p.getObjClass() + ") " + len + " elt(s)");
		incrIndent();
		int index = 0;
		for(CollectionEltRefValueHolder elt : elts) {
			indentPrint("elt[" + index + "/" + len + "]: ");
			elt.visit(this);
			index++;
		}
		decrIndent();
	}

	@Override
	public void caseCollectionElt(CollectionEltRefValueHolder p) {
		visitOrPrintRef(p.getTo());
	}
	
	@Override
	public void caseMap(MapValueHolder p) {
		Collection<MapEntryValueHolder> entries = p.getEntries();
		int len = entries.size();
		indentPrintln("map (" + p.getObjClass() + ") " + len + " elt(s)");
		incrIndent();
		int index = 0;
		for(MapEntryValueHolder entry : entries) {
			indentPrintln("entry[" + index + "/" + len + "]:");
			entry.visit(this);
			index++;
		}
		decrIndent();
	}

	@Override
	public void caseMapEntry(MapEntryValueHolder p) {
		// incrIndent();
		// indentPrintln("mapEntry");

		incrIndent();
		p.getKey().visit(this);
		decrIndent();		

		incrIndent();
		p.getValue().visit(this);
		decrIndent();		

		// decrIndent();		
	}

	
	@Override
	public void caseMapEntryKey(MapEntryKeyRefValueHolder p) {
		indentPrint("mapEntry key ");
		visitOrPrintRef(p.getTo());
	}


	@Override
	public void caseMapEntryValue(MapEntryValueRefValueHolder p) {
		indentPrint("mapEntry value ");
		visitOrPrintRef(p.getTo());
	}

	
	// -------------------------------------------------------------------------

	public void incrIndent() {
		indent++;
	}

	public void decrIndent() {
		indent--;
	}
	public void printIndent() {
		for (int i = 0; i < indent; i++) {
			out.print(' ');
		}
	}

	private void print(String text) {
		out.print(text);
	}

	private void println() {
		out.print('\n');
	}

	private void println(String text) {
		print(text);
		println();
	}

	private void indentPrint(String text) {
		printIndent();
		print(text);
	}
	
	private void indentPrintln(String text) {
		printIndent();
		print(text);
		println();
	}

}
