package com.google.code.joto.util;

import java.util.Iterator;

import org.apache.commons.collections.buffer.BoundedFifoBuffer;

/**
 * a bounded buffer, that automatically remove first element, when adding new one and preventing overflow 
 */
public class BoundedPurgeFifoBuffer<T> extends BoundedFifoBuffer {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;
	
	// ------------------------------------------------------------------------

	public BoundedPurgeFifoBuffer() {
		this(32);
	}

	public BoundedPurgeFifoBuffer(int maxElements) {
		super(maxElements);
	}

	// ------------------------------------------------------------------------

	@Override
	public boolean add(Object element) {
		if (super.isFull()) {
			super.remove();
		}
		return super.add(element);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		return (Iterator<T>) super.iterator();
	}

	
}
