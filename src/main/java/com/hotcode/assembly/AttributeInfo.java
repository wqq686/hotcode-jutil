package com.hotcode.assembly;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * <code>attribute_info</code> structure.
 */
public class AttributeInfo {
	protected ConstPool constPool;
	int name;
	byte[] info;

	public AttributeInfo(ConstPool cp, int attrname, byte[] attrinfo) {
		constPool = cp;
		name = attrname;
		info = attrinfo;
	}

	public AttributeInfo(ConstPool cp, String attrname) {
		this(cp, attrname, (byte[]) null);
	}

	/**
	 * Constructs an <code>attribute_info</code> structure.
	 * 
	 * @param cp
	 *            constant pool table
	 * @param attrname
	 *            attribute name
	 * @param attrinfo
	 *            <code>info</code> field of <code>attribute_info</code>
	 *            structure.
	 */
	public AttributeInfo(ConstPool cp, String attrname, byte[] attrinfo) {
		this(cp, cp.addUtf8Info(attrname), attrinfo);
	}

	public AttributeInfo(ConstPool cp, int n, DataInputStream in)
			throws IOException {
		constPool = cp;
		name = n;
		int len = in.readInt();
		info = new byte[len];
		if (len > 0)
			in.readFully(info);
	}

	protected static void doRead(ConstPool cp, int n, DataInputStream in)
			throws IOException {
		int len = in.readInt();
		byte[] info = new byte[len];
		if (len > 0)
			in.readFully(info);
	}

	public static AttributeInfo read(ConstPool cp, DataInputStream in)
			throws IOException {
		int name = in.readUnsignedShort();
		String nameStr = cp.getUtf8Info(name);
		if (nameStr.charAt(0) < 'L') {
			if (nameStr.equals(CodeAttribute.tag))
				return new CodeAttribute(cp, name, in);
			doRead(cp, name, in);
		} else {
			/*
			 * Note that the names of Annotations attributes begin with 'R'.
			 */
			if (nameStr.equals(LocalVariableAttribute.tag)) {
				return new LocalVariableAttribute(cp, name, in);
			} else {
				doRead(cp, name, in);
			}
		}
		return null;
	}

	/**
	 * Returns an attribute name.
	 */
	public String getName() {
		return constPool.getUtf8Info(name);
	}

	/**
	 * Returns a constant pool table.
	 */
	public ConstPool getConstPool() {
		return constPool;
	}

	/**
	 * Returns the length of this <code>attribute_info</code> structure. The
	 * returned value is <code>attribute_length + 6</code>.
	 */
	public int length() {
		return info.length + 6;
	}

	public static int getLength(List<AttributeInfo> list) {
		int size = 0;
		int n = list.size();
		for (int i = 0; i < n; ++i) {
			AttributeInfo attr = list.get(i);
			size += attr.length();
		}

		return size;
	}

	public static AttributeInfo lookup(List<AttributeInfo> list, String name) {
		if (list == null)
			return null;

		Iterator<AttributeInfo> iterator = list.iterator() ;
		while (iterator.hasNext()) {
			AttributeInfo ai = (AttributeInfo) iterator.next();
			if (ai == null) {
				continue;
			}
			if (ai.getName().equals(name))
				return ai;
		}

		return null; // no such attribute
	}
}
