package org.sef4j.callstack.stats.helpers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Function;

import org.sef4j.callstack.stats.ThreadTimeUtils;
import org.sef4j.callstack.stats.dto.PendingPerfCountDTO;
import org.sef4j.core.api.proptree.PropTreeNodeDTO;

public abstract class PropTreeNodeDTOPrinter extends PropTreeNodeDTOVisitor {

	protected PrintStream out;
	
	public PropTreeNodeDTOPrinter(PrintStream out) {
		this.out = out;
	}

	@Override
	public void onStartVisitNode(PropTreeNodeDTO node) {
		printIndent();
		out.println(node.getName());
	}
	
	protected void printIndent() {
		for (int i = 0; i < currDepth; i++) {
			out.print(' ');
		}
	}

	
	// ------------------------------------------------------------------------
	
	public static class SinglePropPropTreeNodeDTOPrinter<T> extends PropTreeNodeDTOPrinter {
		
		private String propName;
		private Function<T, String> propToString;
		
		public SinglePropPropTreeNodeDTOPrinter(PrintStream out, String propName, Function<T, String> propToString) {
			super(out);
			this.propName = propName;
			this.propToString = propToString;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void visitProp(PropTreeNodeDTO node, String key, Object value) {
			if (! key.equals(propName)) return;
			printIndent();
			out.println(key + ":" + propToString.apply((T) value));
		}

	}

	public static String recursiveDumpPendingCount(PropTreeNodeDTO res) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    PrintStream out = new PrintStream(bout);

	    final long timeNow = ThreadTimeUtils.getTime();
	    // jdk8... toStringUntilNow = x -> x.toStringUntil(timeNow)
	    Function<PendingPerfCountDTO, String> toStringUntilNow = new Function<PendingPerfCountDTO, String>() {
			public String apply(PendingPerfCountDTO x) {
				return x.toStringUntil(timeNow);
			}
		};

	    SinglePropPropTreeNodeDTOPrinter<PendingPerfCountDTO> printer = 
	    		new SinglePropPropTreeNodeDTOPrinter<PendingPerfCountDTO>(out, 
	    		"pending", toStringUntilNow);
	    printer.visitChild(res, -1);
	    out.flush();
		return bout.toString();
	}


}
