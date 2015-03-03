package com.google.code.joto.util;

import java.util.ArrayList;

/**
 * a simple extension of java.util.ArrayList, 
 * for adding efficient removeRange() as public!
 *
 * @param <T>
 */
public class ArrayList2<T> extends ArrayList<T> {

	/** */
	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------------

	public ArrayList2() {
	}

	//-------------------------------------------------------------------------

	public void removeRange(int fromIndex, int toIndex) {
		super.removeRange(fromIndex, toIndex);
	}

	public int truncateHeadForMaxRows(int maxRows) {
		if (maxRows == -1 || size() < maxRows) {
			return 0;
		} else {
			int truncateTo = size() - maxRows;
			removeRange(0, truncateTo);
			return truncateTo;
		}
	}

}
