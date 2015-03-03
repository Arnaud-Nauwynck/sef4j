package org.sef4j.callstack;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sef4j.callstack.stats.ThreadTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * element of a CallStack.
 * This class is managed from current thread facade: LocalCallStack
 * 
 * <PRE>
 *                    <-  when entering new "method" : push new element on stack
 *                       \
 *   +-------------+     /
 *   | callElt Curr|    <-- curr stack position
 *   +-------------+     \
 *   |                   /
 *   |                <-   when exiting curr "method" : pop element on stack
 *   |  ..
 *   |  ..
 *   +-------------+
 *   | callElt 2   |
 *   +-------------+
 *   |  callElt 1  |
 *   +-------------+
 * </PRE>
 */
public final class CallStackElt {

	private static final Logger LOG = LoggerFactory.getLogger(CallStackElt.class);
	
	private final CallStack ownerStack;
	private final CallStackElt parentCallStackElt;
	private final int stackEltIndex;
	
	private String name;

	private Map<String,Object> params;
	private Map<String,Object> inheritableProps;
	
	/* lazily computed from inheritableProps + parentCallStackElt...  **/
	private Map<String,Object> inheritedProps;
	
	/*pp*/ StackPusher pusher;
	/*pp*/ StackPopper popper;

	private long startTime;
	private long threadCpuStartTime;
	private long threadUserStartTime;
	
	private long endTime;
	private long threadCpuEndTime;
	private long threadUserEndTime;

	private int pushPopHandlersLen;
	private CallStackPushPopHandler[] pushPopHandlers = new CallStackPushPopHandler[2];
	
	private int progressExpectedCount;
	private int progressIndex;
	private String progressMessage;
	
	// ------------------------------------------------------------------------
	
	public CallStackElt(CallStack ownerStack, int stackEltIndex, CallStackElt parentCallStackElt) {
		super();
		this.ownerStack = ownerStack;
		this.stackEltIndex = stackEltIndex;
		this.parentCallStackElt = parentCallStackElt;
		// this.pusher .. initialized in parent stack with next stack elt on stack
		this.popper = new StackPopper(this);
	}	
	
	// ------------------------------------------------------------------------
	
	/*pp*/ StackPusher pusher(String name) {
		return pusher.withName(name);
	}

	/*pp*/ void onPush() {
		this.startTime = ThreadTimeUtils.getTime();
		this.threadUserStartTime = ThreadTimeUtils.getCurrentThreadUserTime();
		this.threadCpuStartTime = ThreadTimeUtils.getCurrentThreadCpuTime();
	
		final int handlersLen = parentCallStackElt.pushPopHandlersLen;
		final CallStackPushPopHandler[] handlers = parentCallStackElt.pushPopHandlers;
		for (int i = 0; i < handlersLen; i++) {
			try {
				// implementation note: this is up to each handler responsibility to re-register a handler 
				// on newly push (this) CallStackElt
				// => cf   this.addCallStackPushPopHandler(newChildHandler)
				handlers[i].onPush(this);
			} catch(Exception ex) {
				LOG.error("Failed to call CallStackPushPopHandler.onPush()! .. ignore, no rethrow", ex);
			}
		}
	}

    /*pp*/ void onPushWithParentStartTime() {
        CallStackElt parent = parentCallStackElt;
        this.startTime = parent.startTime;
        this.threadUserStartTime = parent.threadUserStartTime;
        this.threadCpuStartTime = parent.threadCpuStartTime;
        
        // cf remaining code as copy&paste from onPush()
        final int handlersLen = parentCallStackElt.pushPopHandlersLen;
        final CallStackPushPopHandler[] handlers = parentCallStackElt.pushPopHandlers;
        for (int i = 0; i < handlersLen; i++) {
            try {
                // implementation note: this is up to each handler responsibility to re-register a handler 
                // on newly push (this) CallStackElt
                // => cf   this.addCallStackPushPopHandler(newChildHandler)
                handlers[i].onPush(this);
            } catch(Exception ex) {
                LOG.error("Failed to call CallStackPushPopHandler.onPush()! .. ignore, no rethrow", ex);
            }
        }
    }

	   
	/*pp*/ void onPop() {
		this.threadCpuEndTime = ThreadTimeUtils.getCurrentThreadCpuTime();
		this.threadUserEndTime = ThreadTimeUtils.getCurrentThreadUserTime();
		this.endTime = ThreadTimeUtils.getTime();
		
		final int handlersLen = pushPopHandlersLen;
		final CallStackPushPopHandler[] handlers = pushPopHandlers;
		for (int i = 0; i < handlersLen; i++) {
			try {
				handlers[i].onPop(this);
			} catch(Exception ex) {
				LOG.error("Failed to call CallStackPushPopHandler.onPop()! .. ignore, no rethrow", ex);
			}
			
			handlers[i] = null; // clear handler array
		}
		this.pushPopHandlersLen = 0;
		
		this.progressExpectedCount = 0;
		this.progressIndex = 0;
		this.progressMessage = null;		
	}

	/*pp*/ void onProgressStep(int incr, String progressMessage) {
		this.progressIndex += incr;
		this.progressMessage = progressMessage;
		
		final int handlersLen = pushPopHandlersLen;
		final CallStackPushPopHandler[] handlers = pushPopHandlers;
		for (int i = 0; i < handlersLen; i++) {
			try {
				handlers[i].onProgressStep(this);
			} catch(Exception ex) {
				LOG.error("Failed to call CallStackPushPopHandler.onProgressStep()! .. ignore, no rethrow", ex);
			}
		}
	}

	/**
	 * there is no corresponding remove()! .. all handlers will  be automatically removed during onPop()
	 * @param handler
	 * 
	 * this method is unsynchronized... should be called only by child handler, from self-thread
	 */
	public void onPushAddCallStackPushPopHandler(CallStackPushPopHandler handler) {
		doAddCallStackPushPopHandler(handler);
	}

	private void doAddCallStackPushPopHandler(CallStackPushPopHandler handler) {
		if (pushPopHandlersLen+1 >= pushPopHandlers.length) {
			// realloc larger buffer
			CallStackPushPopHandler[] tmpnew = new CallStackPushPopHandler[pushPopHandlers.length + 1];
			System.arraycopy(pushPopHandlers, 0, tmpnew, 0, pushPopHandlers.length);
			this.pushPopHandlers = tmpnew;
		}
		this.pushPopHandlers[pushPopHandlersLen] = handler;
		this.pushPopHandlersLen++;
	}

	public int tmpMaskOnPushAddCallStackPushPopHandler() {
		return pushPopHandlersLen;
	}

	public void tmpUnmaskOnPushAddCallStackPushPopHandler(int prevPushPopHandlersLen) {
		if (pushPopHandlersLen >= prevPushPopHandlersLen) {
			for (int i = prevPushPopHandlersLen+1; i <= pushPopHandlersLen; i++) {
				this.pushPopHandlers[i] = null;
			}
		}
		this.pushPopHandlersLen = prevPushPopHandlersLen;
	}

	public void addRootCallStackHandler(CallStackPushPopHandler handler) {
		onPushAddCallStackPushPopHandler(handler);
	}

	public void removeRootCallStackHandler(CallStackPushPopHandler handler) {
		int foundIndex = -1;
		for (int i = 0; i < pushPopHandlersLen; i++) {
			if (pushPopHandlers[i] == handler) {
				foundIndex = i;
				break;
			}
		}
		if (foundIndex != -1) {
			System.arraycopy(pushPopHandlers, foundIndex+1, pushPopHandlers, foundIndex, pushPopHandlersLen-foundIndex);
			pushPopHandlers[pushPopHandlersLen-1] = null;
			pushPopHandlersLen--;
		}
	}
	
	public List<CallStackPushPopHandler> getPushPopHandlers() {
		ArrayList<CallStackPushPopHandler> res = new ArrayList<CallStackPushPopHandler>();
		for (int i = 0; i < pushPopHandlersLen; i++) {
			res.add(pushPopHandlers[i]);
		}
		return res;
	}
	
	// public getter (value are immutable after push(), until pop() is called)
	// private accessor, cf corresponding Pusher
	// ------------------------------------------------------------------------

	public CallStack getOwnerStack() {
		return ownerStack;
	}
	
	public CallStackElt getParentCallStackElt() {
		return parentCallStackElt;
	}
	
	public int getStackEltIndex() {
		return stackEltIndex;
	}
	
	public String getName() {
		return name;
	}

	public String[] getPath() {
		String[] res = new String[stackEltIndex+1];
		CallStackElt curr = this;
		for (int i = stackEltIndex; i >= 0; i--, curr = curr.getParentCallStackElt()) {
			res[i] = curr.getName();
		}
		return res;
	}
	
	// return pointer, should return unmodifiable ref
	public Map<String, Object> getParams() {
		if (params == null) return Collections.emptyMap();
		return params;
	}
	
	public Map<String, Object> getInheritableProps() {
		if (inheritableProps == null) return Collections.emptyMap();
		return inheritableProps;
	}
	
	public Map<String, Object> getInheritedProps() {
		if (inheritedProps == null) {
			Map<String,Object> tmpres = new HashMap<String,Object>();
			if (parentCallStackElt != null) {
				tmpres.putAll(parentCallStackElt.getInheritedProps()); // **recurse ***
			}
			if (inheritableProps != null) {
				tmpres.putAll(inheritableProps);
			}
			inheritedProps = tmpres;
		}
		return inheritedProps;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getThreadCpuStartTime() {
		return threadCpuStartTime;
	}
	
	public long getThreadUserStartTime() {
		return threadUserStartTime;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public long getElapsedTime() {
		return endTime - startTime;
	}
	
	public long getThreadCpuEndTime() {
		return threadCpuEndTime;
	}
	
	public long getThreadUserEndTime() {
		return threadUserEndTime;
	}
	
	public int getProgressExpectedCount() {
		return progressExpectedCount;
	}
	
	public int getProgressIndex() {
		return progressIndex;
	}
	
	public String getProgressMessage() {
		return progressMessage;
	}
	
	// ------------------------------------------------------------------------

	/** called from Pusher */
	private void putParam(String paramName, Object value) {
		if (params == null) params = new HashMap<String,Object>();
		params.put(paramName, value);
	}

	/** called from Pusher */
	private void putAllParams(Map<String,Object> p) {
		if (params == null) params = new HashMap<String,Object>();
		params.putAll(p);
	}

	/** called from Pusher */
	private void putInheritableProp(String paramName, Object value) {
		if (inheritedProps == null) inheritedProps = new HashMap<String,Object>();
		inheritedProps.put(paramName, value);
	}

	/** called from Pusher */
	private void putAllInheritableProps(Map<String,Object> p) {
		if (inheritedProps == null) inheritedProps = new HashMap<String,Object>();
		inheritedProps.putAll(p);
	}


	
	// ------------------------------------------------------------------------
	
	/**
	 * This is the builder pattern for configuring the information passed to CallStack.push()
	 *
	 */
	public static class StackPusher {
		
		private final CallStackElt pushedElt;
		
		/*pp*/ StackPusher(CallStackElt pushedElt) {
			this.pushedElt = pushedElt;
		}
		
		public StackPopper push() {
			return pushedElt.ownerStack.doPush(pushedElt);
		}

		public StackPopper pushWithParentStartTime() {
		    return pushedElt.ownerStack.doPushWithParentStartTime(pushedElt);
	    }

		/*pp?*/ 
		public StackPusher withName(String name) {
			pushedElt.name = name;
			return this;
		}
		
		/** alias for withParam() */
		public StackPusher p(String paramName, Object value) {
			return withParam(paramName, value);
		}

		public StackPusher withParam(String paramName, Object value) {
			pushedElt.putParam(paramName, value);
			return this;
		}

		public StackPusher withParams(Map<String,Object> p) {
			pushedElt.putAllParams(p);
			return this;
		}

		public StackPusher withInheritableProp(String paramName, Object value) {
			pushedElt.putInheritableProp(paramName, value);
			return this;
		}

		public StackPusher withAllInheritableProps(Map<String,Object> p) {
			pushedElt.putAllInheritableProps(p);
			return this;
		}

		public StackPusher withProgressExpectedCount(int p) {
			pushedElt.progressExpectedCount = p;
			return this;
		}

	}

	// ------------------------------------------------------------------------
	
	public static class StackPopper implements Closeable {

		private final CallStackElt callStackElt;
		
		public StackPopper(CallStackElt callStackElt) {
			this.callStackElt = callStackElt;
		}

		@Override
		public void close() {
			callStackElt.ownerStack.doPop(callStackElt);
		}
		
	}

}
