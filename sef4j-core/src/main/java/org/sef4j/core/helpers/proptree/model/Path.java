package org.sef4j.core.helpers.proptree.model;

import java.io.Serializable;

/**
 * immutable Path
 * 
 * implementation note: datastorage is 2 pointers: parentPath+last <br/>
 * getParentPath() takes o(1) as a pure getter
 * ... but <code>elementAt(i)<code> takes o(length-i) (not efficient... in particular for iterating)!
 */
public final class Path implements Comparable<Path>, Serializable {
	
	/** */
	private static final long serialVersionUID = 1L;

	private final Path parentPath;
	private final String last;
	private final int length;
	
    // ------------------------------------------------------------------------

	public Path(Path parentPath, String last) {
		this.parentPath = parentPath;
		this.last = last;
		this.length = 1 + ((parentPath != null)? parentPath.getLength() : 0);
	}

	public static Path of(String element) {
		return new Path(null, element);
	}

	public static Path of(Path parentPath, String element) {
		return new Path(parentPath, element);
	}

	public static Path of(String... elements) {
		return of(elements, 0, elements.length);
	}

	public static Path of(String[] elements, int from, int to) {
		if (from == to-1) return of(elements[from]);
		return new Path(of(elements, from, to-1), elements[to-1]);
	}

	// ------------------------------------------------------------------------
	
	public Path getParentPath() {
		return parentPath;
	}

	public Path getNthParentPath(int n) {
		Path p = this;
		for(int i = 0; i < n; i++) {
			p = p.getParentPath();
		}
		return p;
	}

	public String getLast() {
		return last;
	}
	
	public int getLength() {
		return length;
	}

	public String elementAt(int index) {
		if (index < 0 || index >= length) throw new ArrayIndexOutOfBoundsException();
		String res = (index == length-1)? last : parentPath.elementAt(index);
		return res;
	}

    public String[] toArray() {
    	int len = getLength();
    	String[] res = new String[len];
    	int i = len-1;
    	for(Path p = this; p != null; p = p.parentPath) {
    		res[i--] = p.last;
    	}
    	return res;
    }

    public String[] toArray(int fromIndex, int toIndex) {
    	int resLen = toIndex - fromIndex;
    	String[] res = new String[resLen];
    	int i = resLen - 1;
    	for(Path p = getNthParentPath(length - toIndex); i >= 0; p = p.parentPath) {
    		res[i--] = p.last;
    	}
    	return res;
    }

    public boolean startsWith(Path other) {
    	int otherLength = other.getLength();
		int remainParent = length - otherLength;
		if (getLength() < otherLength) return false;
		Path curr = getNthParentPath(remainParent);
		Path currOther = other;
		for(; curr != null; curr = curr.parentPath, currOther = currOther.parentPath) {
			if (! curr.last.equals(currOther.last)) {
				return false;
			}
		}
    	return true;
    }
    
    public boolean endsWith(Path other) {
    	int otherLength = other.getLength();
		int length = getLength();
		if (length < otherLength) return false;
		Path curr = this;
		Path currOther = other;
		for(int i = 0; i < otherLength; i++, curr = curr.parentPath, currOther = currOther.parentPath) {
			if (! curr.last.equals(currOther.last)) {
    			return false;
    		}
    	}
    	return true;
    }

    // ------------------------------------------------------------------------

	@Override
	public String toString() {
		int textLen = 0;
		for (Path p = this; p != null; p = p.parentPath) {
			textLen += p.last.length();
			if (p.parentPath != null) {
				textLen += 1;
			}
		}
		char[] text = new char[textLen];
		int pos = textLen;
		for (Path p = this; p != null; p = p.parentPath) {
			pos -= p.last.length();
			p.last.getChars(0, p.last.length(), text, pos); 
			if (p.parentPath != null) {
				pos -= 1;
				text[pos] = '/';
			}
		}
		return new String(text);
	}

	@Override
	public int compareTo(Path other) {
		int res = 0;
		int len = getLength();
		int otherLen = other.getLength();
		Path thisParent = this;
		Path otherParent = other;
		if (len < otherLen) {
			otherParent = otherParent.getNthParentPath(otherLen-len);
		} else if (len > otherLen) {
			thisParent = getNthParentPath(len - otherLen);
		}
		res = recursiveCompare(thisParent, otherParent);
		if (res == 0) {
			res = Integer.compare(len, otherLen);
		}
		return res;
	}

	/*pp*/ static int recursiveCompare(Path left, Path right) {
		int res = 0;
		if (left.length > 1) {
			res = recursiveCompare(left.parentPath, right.parentPath);
		}
		if (res == 0) {
			res = left.last.compareTo(right.last);
		}
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int res = 1;
		for (Path p = this; p != null; p = p.parentPath) {
			res = prime * res + p.last.hashCode();
		}
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Path)) {
			return false;
		}
		Path other = (Path) obj;
		if (length  != other.length) {
			return false;
		}
		Path curr = this;
		Path currOther = other;
		for(; curr != null; curr = curr.parentPath, currOther = currOther.parentPath) {
			if (! curr.last.equals(currOther.last)) {
				return false;
			}
		}
    	return true;
	}

}
