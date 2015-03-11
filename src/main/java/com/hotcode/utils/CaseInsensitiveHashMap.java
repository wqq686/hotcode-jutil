package com.hotcode.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class CaseInsensitiveHashMap implements Map<String, String> {
    private HashMap<String, String> map = new HashMap<String, String>();

    private final String _key(String key) {
        return key == null ? null : key.toLowerCase();
    }

    /**
     * @param key
     * @return
     * @see java.util.HashMap#get(java.lang.Object)
     */
    public String get(Object key) {
        return map.get(_key(String.valueOf(key)));
    }

    /**
     * @param key
     * @param value
     * @return
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    public String put(String key, String value) {
        return map.put(_key(key), value);
    }

    /**
     * @param key
     * @return
     * @see java.util.HashMap#remove(java.lang.Object)
     */
    public String remove(Object key) {
        return map.remove(_key(String.valueOf(key)));
    }

    /**
     * @return
     * @see java.util.HashMap#keySet()
     */
    public Set<String> keySet() {
        return map.keySet();
    }

    @Deprecated
    public int size() {
        return map.size();
    }

    @Deprecated
    public boolean isEmpty() {
    	throw new UnsupportedOperationException();
    }

    @Deprecated
    public boolean containsKey(Object key) {
    	throw new UnsupportedOperationException();
    }

    @Deprecated
    public boolean containsValue(Object value) {
    	throw new UnsupportedOperationException();
    }

    @Deprecated
    public void putAll(Map<? extends String, ? extends String> m) {
    	throw new UnsupportedOperationException();
    }

    public void clear() {
        map.clear();
    }

    @Deprecated
    public Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    public Set<java.util.Map.Entry<String, String>> entrySet() {
        return map.entrySet();
    }
}
