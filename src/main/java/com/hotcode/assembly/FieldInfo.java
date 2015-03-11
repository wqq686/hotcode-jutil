/*
 * Javassist, a Java-bytecode translator toolkit.
 * Copyright (C) 1999- Shigeru Chiba. All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License.  Alternatively, the contents of this file may be used under
 * the terms of the GNU Lesser General Public License Version 2.1 or later,
 * or the Apache License Version 2.0.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 */

package com.hotcode.assembly;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>field_info</code> structure.
 * 
 * @see javassist.CtField#getFieldInfo()
 */
public final class FieldInfo {
	private ConstPool constPool;
	@SuppressWarnings("unused")
	private int accessFlags;
	private int name;
	private String cachedName;
	private int descriptor;
	private List<AttributeInfo> attribute; // may be null.

	private FieldInfo(ConstPool cp) {
		constPool = cp;
		accessFlags = 0;
		attribute = null;
	}

	/**
	 * Constructs a <code>field_info</code> structure.
	 * 
	 * @param cp
	 *            a constant pool table
	 * @param fieldName
	 *            field name
	 * @param desc
	 *            field descriptor
	 * 
	 * @see Descriptor
	 */
	public FieldInfo(ConstPool cp, String fieldName, String desc) {
		this(cp);
		name = cp.addUtf8Info(fieldName);
		cachedName = fieldName;
		descriptor = cp.addUtf8Info(desc);
	}

	FieldInfo(ConstPool cp, DataInputStream in) throws IOException {
		this(cp);
		read(in);
	}

	/**
	 * Returns a string representation of the object.
	 */
	public String toString() {
		return getName() + " " + getDescriptor();
	}

	/**
	 * Returns the constant pool table used by this <code>field_info</code>.
	 */
	public ConstPool getConstPool() {
		return constPool;
	}

	/**
	 * Returns the field name.
	 */
	public String getName() {
		if (cachedName == null)
			cachedName = constPool.getUtf8Info(name);

		return cachedName;
	}

	/**
	 * Sets the field name.
	 */
	public void setName(String newName) {
		name = constPool.addUtf8Info(newName);
		cachedName = newName;
	}



	/**
	 * Returns the field descriptor.
	 * 
	 * @see Descriptor
	 */
	public String getDescriptor() {
		return constPool.getUtf8Info(descriptor);
	}

	/**
	 * Sets the field descriptor.
	 * 
	 * @see Descriptor
	 */
	public void setDescriptor(String desc) {
		if (!desc.equals(getDescriptor()))
			descriptor = constPool.addUtf8Info(desc);
	}

	/**
	 * Returns all the attributes. The returned <code>List</code> object is
	 * shared with this object. If you add a new attribute to the list, the
	 * attribute is also added to the field represented by this object. If you
	 * remove an attribute from the list, it is also removed from the field.
	 * 
	 * @return a list of <code>AttributeInfo</code> objects.
	 * @see AttributeInfo
	 */
	public List<AttributeInfo> getAttributes() {
		if (attribute == null)
			attribute = new ArrayList<AttributeInfo>();

		return attribute;
	}

	private void read(DataInputStream in) throws IOException {
		accessFlags = in.readUnsignedShort();
		name = in.readUnsignedShort();
		descriptor = in.readUnsignedShort();
		int n = in.readUnsignedShort();
		attribute = new ArrayList<AttributeInfo>();
		AttributeInfo a = null;
		for (int i = 0; i < n; ++i)
			a = AttributeInfo.read(constPool, in);
		if (a != null)
			attribute.add(a);
	}
}
