package org.sef4j.testwebapp.web;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.sef4j.testwebapp.dto.ProductDTO;
import org.sef4j.testwebapp.service.InMemoryProductService;
import org.sef4j.testwebapp.service.MetricsStatsTreeRegistry;
import org.sef4j.testwebapp.service.MetricsStatsTreeRegistry.StatsHandlerPopper;
import org.sef4j.testwebapp.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="app/rest/products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

	private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    
	@Inject
	private ProductService productService;

	@Inject
    private InMemoryProductService inMemoryProductService;

	public static ExecutorService wsThreadPool = Executors.newFixedThreadPool(5
//			, new ThreadFactory() {
//				private int idGenerator = 1;
//				@Override
//				public Thread newThread(Runnable r) {
//					return new Thread("WS Pool Thread " + (idGenerator++));
//				}
//			}
			);
	static {
		// wsThreadPool.
	}
	
	@RequestMapping(value="all", method=RequestMethod.GET)
	public List<ProductDTO> findAll() {
	    try (StatsHandlerPopper toPop = pushTopLevelWSStatsHandler("findAll")) {
	    	List<ProductDTO> res = productService.findAll();
	    	return res;
	    }
	}

	@RequestMapping(value="all-in-memory", method=RequestMethod.GET)
	public List<ProductDTO> findInMemoryAll() {
        try (StatsHandlerPopper toPop = pushTopLevelWSStatsHandler("findInMemoryAll")) {
            LOG.info("findInMemoryAll");
            List<ProductDTO> res = inMemoryProductService.findAll();
            return res;
        }
	}

	
	@RequestMapping(value="launchTasks", method=RequestMethod.GET)
	public String launchTasks() {
		int threadCount = 5;
		int repeatCount = 5;
		int randomSleepMaxMillis = 2000;
	    try (StatsHandlerPopper toPop = pushTopLevelWSStatsHandler("launchThreads")) {
	        for (int i = 0; i < threadCount; i++) {
	        	final String taskName = "task " + i;
	        	wsThreadPool.execute(() -> {
	    	    	repeatFindAllTask(taskName, repeatCount, randomSleepMaxMillis);
	        	});
	        }
	        return "OK";
	    }
	}

	private void repeatFindAllTask(String taskName, int repeatCount, int randomSleepMaxMillis) {
		Random rand = new Random();
		try (StatsHandlerPopper toPop = pushTopLevelWSStatsHandler("repeatFindAllTask")) {
	        for (int i = 0; i < repeatCount; i++) {
				LOG.info("findAll .." + i + "/" + repeatCount);
				// productService.findAll();
				inMemoryProductService.findAll();
				
				int sleepMillis = rand.nextInt(randomSleepMaxMillis);
				try {
					Thread.sleep(sleepMillis);
		        } catch (InterruptedException e) {
		        }
	        }
		}
	}

	
	
	
    public StatsHandlerPopper pushTopLevelWSStatsHandler(String methodName) {
        String className = LOG.getName();
        return MetricsStatsTreeRegistry.pushTopLevelStats(className, "ws", methodName);
    }
	
}
