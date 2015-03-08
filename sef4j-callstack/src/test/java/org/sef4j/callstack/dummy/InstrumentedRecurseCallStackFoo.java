package org.sef4j.callstack.dummy;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.ThreadCpuTstUtils;

public class InstrumentedRecurseCallStackFoo {

	private static final String CNAME = InstrumentedRecurseCallStackFoo.class.getName();
	
	public int repeatFooCount = 1;
	public long fooThreadSleep = 0;
	public long fooThreadCpuLoop = 0; 
	public int recurseBarLevel = 1;
	public int repeatBarRecurseCount = 1;
	public int repeatBazCount = 1;
	public long bazThreadSleep = 0;
	public long bazThreadCpuLoop = 0;

	public void fooBar() {
		StackPopper toPop = LocalCallStack.meth(CNAME, "foo").push();
		try {
			bar();
		} finally {
			toPop.close();
		}
	}

	public void bar() {
		StackPopper toPop = LocalCallStack.meth(CNAME, "bar").push();
		try {
		} finally {
			toPop.close();
		}
	}


	public void fooProgress(int progressCount) {
		StackPopper toPop = LocalCallStack.meth(CNAME, "foo")
				.withProgressExpectedCount(progressCount)
				.push();
		try {
			for (int i = 0; i < progressCount; i++) {
				toPop.progressStep(1, null);
			}
		} finally {
			toPop.close();
		}
	}
	
	
	public void fooRecurseBarBaz() {
		for(int i = 0; i < repeatFooCount; i++) {
			StackPopper toPop = LocalCallStack.meth(CNAME, "foo").push();
			try {
				ThreadCpuTstUtils.sleepAndCpu(fooThreadSleep, fooThreadCpuLoop);
				for (int j = 0; j < repeatBarRecurseCount; j++) {
					recurseBar(recurseBarLevel);
				}
			} finally {
				toPop.close();
			}
		}
	}

	public void recurseBar(int recurse) {
		StackPopper toPop = LocalCallStack.meth(CNAME, "recurseBar").push();
		try {
			if (recurse > 0) {
				// *** recurse ***
				recurseBar(recurse - 1);
			} else {
				barBaz();
			}
		} finally {
			toPop.close();
		}
	}

	public void barBaz() {
		StackPopper toPop = LocalCallStack.meth(CNAME, "bar").push();
		try {
			for(int i = 0; i < repeatBazCount; i++) {
				baz();
			}
		} finally {
			toPop.close();
		}
	}

	private void baz() {
		StackPopper toPop = LocalCallStack.meth(CNAME, "baz").push();
		try {
			ThreadCpuTstUtils.sleepAndCpu(bazThreadSleep, bazThreadCpuLoop);
		} finally {
			toPop.close();
		}
	}
	
}
