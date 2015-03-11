package com.hotcode.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtils {

	/**
	 * dbclient查找出来的map,无法通过ks进行传输，需要转成hashmap
	 * 
	 * @param list
	 * @return
	 */
	public static List<Map<String, Object>> toHashMap(List<Map<String, Object>> list) {
		List<Map<String, Object>> result = new ArrayList<>();
		if (list != null && !list.isEmpty()) {
			for (Map<String, Object> m : list) {
				result.add(new HashMap<>(m));
			}
		}
		return result;
	}

	public static Map<String, Object> toHashMap(Map<String, Object> map) {
		if (map == null) {
			return new HashMap<>();
		}
		return new HashMap<>(map);
	}
}
