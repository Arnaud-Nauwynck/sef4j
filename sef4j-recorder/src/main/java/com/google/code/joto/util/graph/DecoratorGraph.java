package com.google.code.joto.util.graph;

import java.util.ArrayList;
import java.util.List;

import com.google.code.joto.util.attr.IAttributeSupport;
import com.google.code.joto.util.attr.IAttributeSupportDelegate;

/**
 *
 */
public class DecoratorGraph<Vertex extends IAttributeSupportDelegate> implements IGraph<Vertex> {

	private List<Vertex> vertexes = new ArrayList<Vertex>(); 
	
	private static enum InnerGraphAttr{
		ATTR_KEY_FROM_VERTEXES,
		ATTR_KEY_TO_VERTEXES
	}

	// -------------------------------------------------------------------------
	
	public DecoratorGraph() {
	}
	
	// -------------------------------------------------------------------------

	public void addVertex(Vertex p) {
		vertexes.add(p);	
	}

	@SuppressWarnings("unchecked")
	public void addLink(Vertex from, Vertex to) {
		IAttributeSupport fromAttr = from.getAttributeSupport();
		List<Vertex> vertexFrom_ToAttrList = 
			(List<Vertex>) fromAttr.getAttrOrPutNewInstance(InnerGraphAttr.ATTR_KEY_TO_VERTEXES, ArrayList.class);
		vertexFrom_ToAttrList.add(to);

		IAttributeSupport toAttr = to.getAttributeSupport();
		List<Vertex> vertexTo_FromAttrList = 
			(List<Vertex>) toAttr.getAttrOrPutNewInstance(InnerGraphAttr.ATTR_KEY_FROM_VERTEXES, ArrayList.class);
		vertexTo_FromAttrList.add(from);
	}
	
	public List<Vertex> getVertexes() {
		return vertexes;
	}

	@SuppressWarnings("unchecked")
	public List<Vertex> getVertexFromList(Vertex p) {
		return (List<Vertex>) p.getAttributeSupport().getAttr(InnerGraphAttr.ATTR_KEY_FROM_VERTEXES);
	}

	@SuppressWarnings("unchecked")
	public List<Vertex> getVertexToList(Vertex p) {
		return (List<Vertex>) p.getAttributeSupport().getAttr(InnerGraphAttr.ATTR_KEY_TO_VERTEXES);
	}

	public IAttributeSupport getVertexAttributeSupport(Vertex p) {
		return p.getAttributeSupport();
	}

}
