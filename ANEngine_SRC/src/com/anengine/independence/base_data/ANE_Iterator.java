package com.anengine.independence.base_data;

import java.util.Iterator;

public class ANE_Iterator<T> implements Iterator{

	private Iterator<T> iterator = null;
	
	public ANE_Iterator(Iterator<T> iterator)
	{
		this.iterator = iterator;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return this.iterator.hasNext();
	}

	@Override
	public T next() {
		// TODO Auto-generated method stub
		return this.iterator.next();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		this.iterator.remove();
	}

}
