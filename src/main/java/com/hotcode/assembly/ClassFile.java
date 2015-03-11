package com.hotcode.assembly;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ClassFile {
	/**
	 * Constructs a class file from a byte stream.
	 */
	private ClassFile(DataInputStream in) throws IOException {
		read(in);
	}

	private static InputStream openClassfile(String classname,
			ClassLoader appClassLoader) {
		String className = classname.replace('.', '/') + ".class";
		return appClassLoader != null ? appClassLoader
				.getResourceAsStream(className) : Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(className);
		// return Utils.loadConfigFile(className, ClassFile.class) ;
	}

	public static ClassFile createClassFile(String classQualifiedName) {
		return createClassFile(classQualifiedName, null) ;
	}
	
	
	public static ClassFile createClassFile(String classQualifiedName, ClassLoader appClassLoader) {
		InputStream is = null;
		try {
			is = openClassfile(classQualifiedName, appClassLoader);
			is = new BufferedInputStream(is);
			ClassFile classFile = new ClassFile(new DataInputStream(is));
			return classFile;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private int[] interfaces;
	private List<FieldInfo> fields;
	private List<MethodInfo> methods;

	@SuppressWarnings("unused")
	private void read(DataInputStream in) throws IOException {

		int magic = in.readInt();
		if (magic != 0xCAFEBABE)
			throw new IOException("bad magic number: "
					+ Integer.toHexString(magic));
		int minor = in.readUnsignedShort();
		int major = in.readUnsignedShort();
		ConstPool constPool = new ConstPool(in);
		int accessFlags = in.readUnsignedShort();
		int thisClass = in.readUnsignedShort();
		int superClass = in.readUnsignedShort();

		int interfaceNum = in.readUnsignedShort();
		if (interfaceNum > 0) {
			interfaces = new int[interfaceNum];
			for (int i = 0; i < interfaceNum; ++i)
				interfaces[i] = in.readUnsignedShort();
		}

		ConstPool cp = constPool;

		int fieldNum = in.readUnsignedShort();
		fields = new ArrayList<FieldInfo>();
		for (int i = 0; i < fieldNum; ++i)
			addField(new FieldInfo(cp, in));

		int methodNum = in.readUnsignedShort();
		methods = new ArrayList<MethodInfo>();
		for (int i = 0; i < methodNum; ++i)
			addMethod(new MethodInfo(cp, in));
	}

	/**
	 * Just appends a method to the class. It does not check method duplication
	 * or remove a bridge method. Use this method only when minimizing
	 * performance overheads is seriously required.
	 * 
	 * @since 3.13
	 */
	public final void addMethod(MethodInfo minfo) {
		methods.add(minfo);
	}

	/**
	 * Just appends a field to the class. It does not check field duplication.
	 * Use this method only when minimizing performance overheads is seriously
	 * required.
	 * 
	 * @since 3.13
	 */
	public final void addField(FieldInfo finfo) {
		fields.add(finfo);
	}

	/**
	 * Returns the method with the specified name. If there are multiple methods
	 * with that name, this method returns one of them.
	 * 
	 * @return null if no such method is found.
	 */
	public MethodInfo getMethod(String name) {
		for (MethodInfo mi : methods) {
			if (mi.getName().equals(name)) {
				return mi;
			}
		}
		return null;
	}
	
	
	public LocalVariableAttribute getMethodLocalVariableAttribute(String methodName) {
		MethodInfo methodInfo = getMethod(methodName);
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		return codeAttribute == null ? null
				: (LocalVariableAttribute) codeAttribute
						.getAttribute(LocalVariableAttribute.tag);
	}

}
