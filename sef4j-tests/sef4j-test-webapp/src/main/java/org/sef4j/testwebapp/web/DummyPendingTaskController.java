package org.sef4j.testwebapp.web;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.testwebapp.service.MetricsStatsTreeRegistry;
import org.sef4j.testwebapp.service.MetricsStatsTreeRegistry.StatsHandlerPopper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="app/rest/dummyPendingTask", produces = MediaType.APPLICATION_JSON_VALUE)
public class DummyPendingTaskController {

	private static final Logger LOG = LoggerFactory.getLogger(DummyPendingTaskController.class);

	protected static ExecutorService dummyTaskThreadPool = Executors.newFixedThreadPool(5);

	protected Object taskLock = new Object();
	protected int taskWaitCount = 0;
	
    // ------------------------------------------------------------------------
    
    public DummyPendingTaskController() {
    }
    
    
    @RequestMapping(value="startPendingTask", method=RequestMethod.GET)
	public void startOnePendingTask(int count, int depth) {
    	LOG.info("startPendingTask count:" + count + ", depth:" + depth);
    	for (int i = 0; i < count; i++) {
    		dummyTaskThreadPool.execute(() -> dummyTask(depth));
    	}
    	LOG.info("... startPendingTask count:" + count + " (taskWaitCount:" + taskWaitCount + ")");
	}

    @RequestMapping(value="stopPendingTask", method=RequestMethod.GET)
	public void stopOnePendingTask(int count) {
    	LOG.info("stopPendingTask count:" + count);
    	for (int i = 0; i < count; i++) {
	    	synchronized (taskLock) {
				taskLock.notify();
			}
    	}
    	LOG.info("... stopPendingTask count:" + count + " (taskWaitCount:" + taskWaitCount + ")");
	}

    protected void dummyTask(int depth) {
    	LOG.info("dummyTask " + depth + " ...");
    	try (StatsHandlerPopper toPop = pushTopLevelTaskStatsHandler("dummyTask")) {
    	    recursiveFoo(depth);
    	}
    	LOG.info("... done dummyTask " + depth);
    }
    
    private void recursiveFoo(int depth) {
    	LOG.info("recursiveFoo " + depth + " ..." + " (taskWaitCount:" + taskWaitCount + ")");
    	try (StackPopper toPop = LocalCallStack.meth(LOG, "recursiveFoo").push()) {
    		if (depth > 1) {
    			recursiveFoo(depth - 1);
    		} else {
    			synchronized(taskLock) {
    				try {
    					taskWaitCount++;
    					LOG.info("wait... " + " (taskWaitCount:" + taskWaitCount + ")");

    					taskLock.wait();

    					taskWaitCount--;
    					LOG.info("... done wait " + " (taskWaitCount:" + taskWaitCount + ")");

    				} catch (InterruptedException e) {
    				}
    			}
    		}
    	}
    	LOG.info("... done recursiveFoo " + depth);
	}


	public StatsHandlerPopper pushTopLevelWSStatsHandler(String methodName) {
        String className = LOG.getName();
        return MetricsStatsTreeRegistry.pushTopLevelStats(className, "dummyWS", methodName);
    }

	public StatsHandlerPopper pushTopLevelTaskStatsHandler(String methodName) {
        String className = LOG.getName();
        return MetricsStatsTreeRegistry.pushTopLevelStats(className, "dummyTasks", methodName);
    }

}
