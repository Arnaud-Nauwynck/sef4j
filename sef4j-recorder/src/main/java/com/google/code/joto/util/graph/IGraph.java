package com.google.code.joto.util.graph;

import java.util.List;

import com.google.code.joto.util.attr.IAttributeSupport;

/**
 *
 */
public interface IGraph<Vertex> {

	public abstract void addVertex(Vertex p);
	public abstract void addLink(Vertex from, Vertex to);

	public abstract List<Vertex> getVertexes();
	public abstract List<Vertex> getVertexFromList(Vertex p);
	public abstract List<Vertex> getVertexToList(Vertex p);

	public IAttributeSupport getVertexAttributeSupport(Vertex p);
	
}