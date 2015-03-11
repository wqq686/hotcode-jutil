package com.hotcode.common;

public class BytesUtils {

	
	
	/**
	 * 前2bit表示要用几个byte存储,只能存非负数,存储最大值1,073,741,824
	 * 
	 * @param value must > 0
	 * @return
	 */
	public static byte[] encodeUnsignInt(int value) {
		if (value < 0) throw new IllegalStateException("Value must > 0.");
		int len = 0;
		if (value <= 63) {// 用1字节存
			len = 1;
		} else if (value <= 16383) {// 用2字节存
			len = 2;
		} else if (value <= 4194303) {// 用3字节存
			len = 3;
		} else if (value <= 1073741823) {// 用4字节存
			len = 4;
		} else {// 超出可存储最大值
			throw new IllegalStateException("Value too large!");
		}

		byte[] data = new byte[len];
		for (int i = 0; i < len; i++) {
			data[len - i - 1] = (byte) (value >> 8 * i & 0xFF);
		}
		data[0] |= (len - 1) << 6;
		return data;
	}
}
