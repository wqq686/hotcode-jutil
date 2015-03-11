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

/**
 * <code>LocalVariableTable_attribute</code>.
 */
public class LocalVariableAttribute extends AttributeInfo {
    /**
     * The name of this attribute <code>"LocalVariableTable"</code>.
     */
    public static final String tag = "LocalVariableTable";

    /**
     * The name of the attribute <code>"LocalVariableTypeTable"</code>.
     */
    public static final String typeTag = "LocalVariableTypeTable";


    public LocalVariableAttribute(ConstPool cp, int n, DataInputStream in)
        throws IOException
    {
        super(cp, n, in);
    }


	public String variableName(int i) {
		return getConstPool().getUtf8Info(nameIndex(i));
	}
	
    /**
     * Returns the value of <code>local_variable_table[i].name_index</code>.
     * This represents the name of the local variable.
     *
     * @param i         the i-th entry.
     */
    public int nameIndex(int i) {
        return ByteArray.readU16bit(info, i * 10 + 6);
    }
    
    private boolean findThisTag = false;
    private int thisTagIndex = 0 ;
    
    /**
     * 新增方法 过滤掉this之前的变量名
     * @param index 
     * 从1开始
     * @return
     */
	public String methodVariableName(int index) {
		
		try{
			if(!findThisTag){
				for(;;thisTagIndex++){
					String name = variableName(thisTagIndex).trim() ;
					if("this".equals(name)) {
						findThisTag = true ;
						break ;
					}
				}
			}
			//没有出错,返回对应的变量名
			return getConstPool().getUtf8Info(nameIndex(thisTagIndex+index));
		}catch (Exception e) {
			e.printStackTrace() ;
			findThisTag = false ;
			thisTagIndex = 0 ;
			return null ;//如果出错,返回
		}
	}
    
}
