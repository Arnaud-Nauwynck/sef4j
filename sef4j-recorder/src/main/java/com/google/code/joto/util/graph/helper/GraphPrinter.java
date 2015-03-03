package com.google.code.joto.util.graph.helper;

import java.io.PrintStream;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.google.code.joto.util.ToStringFormatter;
import com.google.code.joto.util.graph.IGraph;

public class GraphPrinter<Vertex> {

	private PrintStream out;
	private ToStringFormatter<Vertex> vertexFormat;
	
	private Map<Vertex,Integer> vertex2id = new IdentityHashMap<Vertex,Integer>(); 
	
	//-------------------------------------------------------------------------

	public GraphPrinter(PrintStream out, ToStringFormatter<Vertex> vertexFormat) {
		this.out = out;
		this.vertexFormat = vertexFormat;
	}

	//-------------------------------------------------------------------------

	public void printGraph(IGraph<Vertex> g) {
		int i = 0;
		out.println("Vertexes: " + g.getVertexes().size() + "elts");
		for(Vertex v : g.getVertexes()) {
			Integer id = i; 
			vertex2id.put(v, id);
			String vStr = vertexFormat.objectToString(v);
			out.println("[" + id + "] " + vStr);
			i++;
		}
		out.println();
		
		out.println("Forward dependencies:");
		i = 0;
		for(Vertex v : g.getVertexes()) {
			List<Vertex> tos = g.getVertexToList(v);
			out.println("[" + i + "] => " + vertexesToIdsString(tos));
			i++;
		}
		
		out.println();
		out.println("Backward dependencies:");
		i = 0;
		for(Vertex v : g.getVertexes()) {
			List<Vertex> froms = g.getVertexFromList(v);
			out.println("[" + i + "] <= " + vertexesToIdsString(froms));
			i++;
		}
		out.println();

	}
	
	private String vertexesToIdsString(List<Vertex> ls) {
		StringBuilder sb = new StringBuilder();
		if (ls != null && !ls.isEmpty()) {
			for(Vertex v : ls) {
				Integer id = vertex2id.get(v);
				if (id == null) {
					// should not occur!!!
					sb.append("(id not found) '" + vertexFormat.objectToString(v) + "' ");
				}
				sb.append(id + ", ");
			}
			sb.delete(sb.length() - 2, sb.length());
		} 
		return sb.toString();
	}
	
}
