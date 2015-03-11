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
 * <code>Code_attribute</code>.
 * 
 * <p>
 * To browse the <code>code</code> field of a <code>Code_attribute</code>
 * structure, use <code>CodeIterator</code>.
 * 
 * @see CodeIterator
 */
public class CodeAttribute extends AttributeInfo {
	/**
	 * The name of this attribute <code>"Code"</code>.
	 */
	public static final String tag = "Code";
	@SuppressWarnings("unused")
	private int maxStack;
	@SuppressWarnings("unused")
	private int maxLocals;
	private List<AttributeInfo> attributes;

	CodeAttribute(ConstPool cp, int name_id, DataInputStream in)
			throws IOException {
		super(cp, name_id, (byte[]) null);
		@SuppressWarnings("unused")
		int attr_len = in.readInt();

		maxStack = in.readUnsignedShort();
		maxLocals = in.readUnsignedShort();

		int code_len = in.readInt();
		info = new byte[code_len];
		in.readFully(info);

		shiftExceptionTable(cp, in);
		attributes = new ArrayList<AttributeInfo>();
		int num = in.readUnsignedShort();
		for (int i = 0; i < num; ++i)
			attributes.add(AttributeInfo.read(cp, in));
	}

	public LocalVariableAttribute getAttribute(String name) {
		return (LocalVariableAttribute) AttributeInfo.lookup(attributes, name);
	}

	public static void shiftExceptionTable(ConstPool cp, DataInputStream in)
			throws IOException {
		int length = in.readUnsignedShort();
		for (int i = 0; i < length; ++i) {
			@SuppressWarnings("unused")
			int start = in.readUnsignedShort();
			@SuppressWarnings("unused")
			int end = in.readUnsignedShort();
			@SuppressWarnings("unused")
			int handle = in.readUnsignedShort();
			@SuppressWarnings("unused")
			int type = in.readUnsignedShort();
		}
	}
}
