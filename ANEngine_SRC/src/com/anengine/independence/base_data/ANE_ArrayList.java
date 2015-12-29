package com.anengine.independence.base_data;

import java.util.ArrayList;
import java.util.Iterator;

public class ANE_ArrayList<T>{
	public ArrayList<T> list;
	
	public ANE_ArrayList()
	{
		list = new ArrayList<T>();
	}
	
	public ANE_ArrayList(int len)
	{
		list = new ArrayList<T>(len);
	}
	
	public ANE_ArrayList(ArrayList<T> list)
	{
		this.list = list;
	}
	
	public ANE_ArrayList<T> clone()
	{
		return new ANE_ArrayList<T>((ArrayList<T>)list.clone());
	}
	
	public boolean add(T o)
	{
		return list.add(o);
	}
	
	public T remove(int index)
	{
		T t = list.remove(index);
		return t;
	}
	
	public boolean remove(T obj)
	{
		boolean result = list.remove(obj);
		return result;
	}
	
	public boolean contains(Object o)
	{
		return list.contains(o);
	}
	
	public Object[] toArray()
	{
		return list.toArray();
	}
	
	public T[] toArray(T[] arr)
	{
		return list.toArray(arr);
	}
	
	public void clear()
	{
		list.clear();
	}
	
	public int length()
	{
		return list.size();
	}
	
	public T get(int i)
	{
		return list.get(i);
	}
	
	public ANE_Iterator iterator()
	{
		ANE_Iterator<T> ite = new ANE_Iterator<T>(list.iterator());
		return ite;
	}
}
