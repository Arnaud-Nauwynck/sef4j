package com.google.code.joto.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * extension of java.util.List for supporting sorting with priority 
 */
public class PriorityList<T> implements Iterable<T>, Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;
	
    private final Set<Item<T>> set = new TreeSet<Item<T>>();

    private int idGenerator = 0;

    //-------------------------------------------------------------------------

	public PriorityList() {
	}

	//-------------------------------------------------------------------------

    public void add(T item, int priority) {
        this.set.add(new Item<T>(item, priority, ++idGenerator));
    }

    /**
     * implements Iterable<T> ... return a sorted iterator (on a temporary copy)
     */
    @Override
    public Iterator<T> iterator() {
    	List<T> tmp = new ArrayList<T>(set.size());
    	for(Item<T> item : set) {
    		tmp.add(item.value);
    	}
    	return tmp.iterator();
    }

    public void addAllWithPriority(PriorityList<T> src) {
    	for(Item<T> srcItem : src.set) {
    		add(srcItem.value, srcItem.priority);
    	}
    }
    
    // -------------------------------------------------------------------------
    
    /**
     * internal
     */
    private static class Item<T> implements Comparable<Item<T>>, Serializable {

    	/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
        private final T value;
        private final int priority;
        private final int id; // for comparing with equals priority

        public Item(T value, int priority, int id) {
            this.value = value;
            this.priority = priority;
            this.id = id;
        }

        public int compareTo(Item<T> other) {
            if (this.priority != other.priority) {
                return (other.priority - this.priority);
            }
            return (other.id - this.id);
        }

        public boolean equals(Object obj) {
        	if (obj == this) return true;
        	if (!(obj instanceof Item<?>)) return false;
            return this.id == ((Item<?>)obj).id;
        }

    }
}
