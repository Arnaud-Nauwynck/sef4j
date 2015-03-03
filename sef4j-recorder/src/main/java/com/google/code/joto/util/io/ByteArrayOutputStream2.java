package com.google.code.joto.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * This class is similar to java.io.ByteArrayOutputStream in jdk,
 * BUT... is for ThreadLocal usage:
 *    it does not use "synchronized"
 *    it offers public read/write access to the underlying byte array (without copy)  
 */
public class ByteArrayOutputStream2 extends OutputStream {

	/** 
	 * The buffer where data is stored. 
	 */
	protected byte buf[];

	/**
	 * The number of valid bytes in the buffer. 
	 */
	protected int count;

	// -------------------------------------------------------------------------

	public ByteArrayOutputStream2() {
		this(512);
	}

	public ByteArrayOutputStream2(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Negative initial size: " + size);
		}
		buf = new byte[size];
	}

	// -------------------------------------------------------------------------

	public byte[] getBuffer() {
		return buf;
	}

	public void setBuffer(byte[] p) {
		this.buf = p;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int p) {
		this.count = p;
	}

	public void ensureCapacity(int len) {
		if (len > buf.length) {
			buf = Arrays.copyOf(buf, Math.max(buf.length << 1, len));
		}
	}
	
	/**
	 * Writes the specified byte to this byte array output stream. 
	 *
	 * @param   b   the byte to be written.
	 */
	public void write(int b) {
		int newcount = count + 1;
		if (newcount > buf.length) {
			buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
		}
		buf[count] = (byte)b;
		count = newcount;
	}

	/**
	 * Writes <code>len</code> bytes from the specified byte array 
	 * starting at offset <code>off</code> to this byte array output stream.
	 *
	 * @param   b     the data.
	 * @param   off   the start offset in the data.
	 * @param   len   the number of bytes to write.
	 */
	public void write(byte b[], int off, int len) {
		if ((off < 0) || (off > b.length) || (len < 0) ||
				((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		int newcount = count + len;
		if (newcount > buf.length) {
			buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
		}
		System.arraycopy(b, off, buf, count, len);
		count = newcount;
	}

	/**
	 * Writes the complete contents of this byte array output stream to 
	 * the specified output stream argument, as if by calling the output 
	 * stream's write method using <code>out.write(buf, 0, count)</code>.
	 *
	 * @param      out   the output stream to which to write the data.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public void writeTo(OutputStream out) throws IOException {
		out.write(buf, 0, count);
	}

	/**
	 * Resets the <code>count</code> field of this byte array output 
	 * stream to zero, so that all currently accumulated output in the 
	 * output stream is discarded. The output stream can be used again, 
	 * reusing the already allocated buffer space. 
	 *
	 * @see     java.io.ByteArrayInputStream#count
	 */
	public void reset() {
		count = 0;
	}

	/**
	 * Creates a newly allocated byte array. Its size is the current 
	 * size of this output stream and the valid contents of the buffer 
	 * have been copied into it. 
	 *
	 * @return  the current contents of this output stream, as a byte array.
	 * @see     java.io.ByteArrayOutputStream#size()
	 */
	public byte toByteArray()[] {
		return Arrays.copyOf(buf, count);
	}

	/**
	 * Returns the current size of the buffer.
	 *
	 * @return  the value of the <code>count</code> field, which is the number
	 *          of valid bytes in this output stream.
	 * @see     java.io.ByteArrayOutputStream#count
	 */
	public int size() {
		return count;
	}

	/**
	 * Converts the buffer's contents into a string decoding bytes using the
	 * platform's default character set. The length of the new <tt>String</tt>
	 * is a function of the character set, and hence may not be equal to the 
	 * size of the buffer.
	 *
	 * <p> This method always replaces malformed-input and unmappable-character
	 * sequences with the default replacement string for the platform's
	 * default character set. The {@linkplain java.nio.charset.CharsetDecoder}
	 * class should be used when more control over the decoding process is
	 * required.
	 *
	 * @return String decoded from the buffer's contents.
	 * @since  JDK1.1
	 */
	public String toString() {
		return new String(buf, 0, count);
	}

	/**
	 * Converts the buffer's contents into a string by decoding the bytes using
	 * the specified {@link java.nio.charset.Charset charsetName}. The length of
	 * the new <tt>String</tt> is a function of the charset, and hence may not be
	 * equal to the length of the byte array.
	 *
	 * <p> This method always replaces malformed-input and unmappable-character
	 * sequences with this charset's default replacement string. The {@link
	 * java.nio.charset.CharsetDecoder} class should be used when more control
	 * over the decoding process is required.
	 *
	 * @param  charsetName  the name of a supported
	 *		    {@linkplain java.nio.charset.Charset </code>charset<code>}
	 * @return String decoded from the buffer's contents.
	 * @exception  UnsupportedEncodingException
	 *             If the named charset is not supported
	 * @since   JDK1.1
	 */
	public String toString(String charsetName)
	throws UnsupportedEncodingException
	{
		return new String(buf, 0, count, charsetName);
	}

	/**
	 * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in
	 * this class can be called after the stream has been closed without
	 * generating an <tt>IOException</tt>.
	 * <p>
	 *
	 */
	public void close() throws IOException {
	}

}
