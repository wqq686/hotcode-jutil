package com.hotcode.assembly;

import java.util.List;
import java.util.Map;

public class ReadMethodParameterNameTest {

	public void say(int intNum, long longNum, short shortNum,
			Map<String, String> map, List<String> list, Object[] args) {

	}

	public static void main(String[] args) throws Exception {
		ClassFile classFile = ClassFile.createClassFile(Test.class
				.getName(), null);
		MethodInfo methodInfo = classFile.getMethod("say");
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
				.getAttribute(LocalVariableAttribute.tag);

		int pos = 1;
		for (int j = 0; j < 6; j++) {
			String variableName = attr.variableName(j + pos);
			System.out.println(variableName);
		}
	}

	private static interface Test {
		public void say(int intNum, long longNum, short shortNum,
				Map<String, String> map, List<String> list, Object[] args) ;
	}
}
