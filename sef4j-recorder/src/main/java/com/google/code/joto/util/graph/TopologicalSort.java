package com.google.code.joto.util.graph;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.util.ToStringFormatter;

/**
 * helper class for computing the topological sort of a graph
 */
public class TopologicalSort<Vertex> {

	private boolean debug = false;
	private static Logger log = LoggerFactory.getLogger(TopologicalSort.class);

	public static class Counter {
		int count;

		public Counter() {
		}
	}

	private static final Object CounterAttrKey = Counter.class;
	
	private IGraph<Vertex> graph;

	private ToStringFormatter<Vertex> debugVertexFormat;
	private Map<Vertex,Integer> debugVertex2id = new IdentityHashMap<Vertex,Integer>(); 
	
//	private Comparator<Vertex> vertexCountComparator = new Comparator<Vertex>() {
//		public int compare(Vertex o1, Vertex o2) {
//			int c1 = vertexToCounter(o1).count;
//			int c2 = vertexToCounter(o2).count;
//			int res = (c1 < c2)? -1 : (c1 == c2)? 0 : +1;
//			return res;
//		}
//	};
	
	// -------------------------------------------------------------------------
	
	public TopologicalSort(IGraph<Vertex> graph) {
		this.graph = graph;
	}
	
	// -------------------------------------------------------------------------

	
	public List<Vertex> topologicalSort() {
		List<Vertex> res = new ArrayList<Vertex>();

		if (debug) {
			log.info("topological sort...");
		}

		List<Vertex> vertexes = graph.getVertexes();
		
		// init attr count = counter of from vertex list
		int i = 0;
		for(Vertex v : vertexes) {
			List<Vertex> vertexToList = graph.getVertexToList(v);
			if (vertexToList != null && !vertexToList.isEmpty()) {
				for(Vertex vertexTo : vertexToList) {
					vertexToCounter(vertexTo).count++;
				}
			}
			debugVertex2id.put(v, Integer.valueOf(i));
			i++;
		}
		
		// init working queue 
		
		StablePriorityQueue<Vertex> queue = new StablePriorityQueue<Vertex>();
		if (debug) {
			queue.setDebugObj2id(debugVertex2id);
		}
		for(Vertex v : vertexes) {
			int count = vertexToCounter(v).count;
			queue.add(count, v);
		}
		

		if (debug) {
			log.info("queue: " + queue.toString());
		}
		
		// consume elt from queue, decrement counter for each elt polled  
		while(!queue.isEmpty()) {
			Vertex polledVertex = queue.poll();
			if (polledVertex == null) {
				throw new IllegalStateException("should not occur");
			}
			Counter c = vertexToCounter(polledVertex);
			if (c.count != 0) {
				log.info("circular dependency found...");
//				throw new IllegalStateException("topological sort failed... circular dependency found");
			}
			res.add(polledVertex);

			if (debug) {
				log.info("found next: " + debugVertexFormat.objectToString(polledVertex));
				log.info("queue size:" + queue.size()+ " elt(s)");
			}
			
			// decrement (and re-index in queue) all vertex pointed "to" 
			List<Vertex> vertexToList = graph.getVertexToList(polledVertex);
			if (vertexToList != null && !vertexToList.isEmpty()) {
				for(Vertex vertexTo : vertexToList) {
					Counter vertexToCounter = vertexToCounter(vertexTo);
//					int checkLen = queue.size();
//					boolean removed = queue.remove(vertexTo);
//					if (!removed) {
//						throw new IllegalStateException();
//					}
//					vertexToCounter.count--;
//					queue.add(vertexTo);
//					if (checkLen != queue.size()) {
//						throw new IllegalStateException();
//					}
					queue.moveUp(vertexToCounter.count, vertexTo);
					vertexToCounter.count--;

					if (debug) {
						log.info("decremented count: " + vertexToCounter(vertexTo).count + " for: " + debugVertexFormat.objectToString(vertexTo));
					}
				}
			}
		}

		if (debug) {
			log.info("... done topological sort");
		}

		return res;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setDebugVertexFormat(ToStringFormatter<Vertex> debugVertexFormat) {
		this.debugVertexFormat = debugVertexFormat;
	}

	private Counter vertexToCounter(Vertex p) {
		return (Counter) graph.getVertexAttributeSupport(p).getAttrOrPutNewInstance(CounterAttrKey, Counter.class);
	}

//	private String debugQueueContent(PriorityQueue<Vertex> queue) {
//		StringBuilder sb = new StringBuilder();
//		if (!queue.isEmpty()) {
//			for(Vertex v : queue) {
//				Counter c = vertexToCounter(v);
//				Integer vId = debugVertex2id.get(v); 
//				sb.append(vId + "(" + c.count + "), ");
//			}
//			sb.delete(sb.length() - 2, sb.length());
//		}
//		return sb.toString();
//	}
	
	// (re-)implementation of a stable priority queue ???!!!
	// using [count]->List<>
	// -------------------------------------------------------------------------

	private static class StablePriorityQueue<T> {
		// Map<Integer,List<T>> subQueues = new TreeMap<Integer,List<T>>();
		private List<List<T>> subQueues = new ArrayList<List<T>>(); 
		private int size;
		
		Map<T,Integer> debugObj2id;
		
		//-------------------------------------------------------------------------

		public StablePriorityQueue() {
		}
		
		//-------------------------------------------------------------------------

		public boolean isEmpty() {
			return size == 0;
		}
		
		public int size() {
			return size;
		}
		
		public T poll() {
			List<T> subQueue = getSubQueue(0);
			if (subQueue.isEmpty()) {
				return null;
			}
			size--;
			T res = subQueue.remove(0);
			return res;
		}
		
		public void remove(int priority, T obj) {
			List<T> subQueue = getSubQueue(priority);
			subQueue.remove(obj);
			size--;
		}

		public void add(int priority, T obj) {
			List<T> subQueue = getSubQueue(priority);
			subQueue.add(obj);
			size++;
		}

		public void moveUp(int priority, T obj) {
			remove(priority, obj);
			add(priority - 1, obj);
		}

		
		public List<T> getSubQueue(int priority) {
//			Integer key = Integer.valueOf(priority);
//			List<T> res = subQueues.get(key);
//			if (res == null) {
//				res = new ArrayList();
//				subQueues.put(key, res);
//			}
			int len = subQueues.size();
			if (priority >= len) {
				// alloc!
				for(int i = len; i <= priority; i++) {
					subQueues.add(new ArrayList<T>());
				}
			}
			List<T> res = subQueues.get(priority);
			return res;
		}
		
		
		public void setDebugObj2id(Map<T, Integer> debugObj2id) {
			this.debugObj2id = debugObj2id;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("PriorityQueue[" + size + " elt(s)\n");
			for(int i = 0; i < subQueues.size(); i++) {
				List<T> subQueue = subQueues.get(i);
				if (subQueue != null && !subQueue.isEmpty()) {
					sb.append("[" + i + "] " + subQueue.size() + " elt(s) : ");
					for(T elt : subQueue) {
						Integer id = debugObj2id.get(elt);
						sb.append(id + ", ");
					}
					sb.delete(sb.length() - 2, sb.length());
					sb.append("\n");
				}
			}
			sb.append("\n]");
			return sb.toString();
		}
	}
	
}
