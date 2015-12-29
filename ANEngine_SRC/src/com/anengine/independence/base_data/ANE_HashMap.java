package com.anengine.independence.base_data;

import java.util.HashMap;

public class ANE_HashMap<K,V> {
	
	private HashMap<K,V> map = new HashMap<K,V>();
	
	public ANE_HashMap(){}
	
	public ANE_HashMap(HashMap<K,V> map)
	{
		this.map = map;
	}
	
	public V put(K key,V value)
	{
		return map.put(key, value);
	}
	
	public V get(Object key)
	{
		return map.get(key);
	}
	
	public boolean containsKey(Object key)
	{
		return map.containsKey(key);
	}
	
	public boolean containsValue(Object value)
	{
		return map.containsValue(value);
	}
	
	public ANE_HashMap<K,V> clone()
	{
		ANE_HashMap<K,V> tmap = new ANE_HashMap<K,V>((HashMap<K,V>)map.clone());
		return tmap;
	}
}
