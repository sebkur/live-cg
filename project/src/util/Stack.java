package util;

import java.util.ArrayList;

public class Stack<T> extends ArrayList<T>
{

	private static final long serialVersionUID = 2339567309629898300L;

	public void push(T data)
	{
		add(data);
	}
	
	public T top()
	{
		return get(size() - 1);
	}
	
	public T pop()
	{
		return remove(size() - 1);
	}
}
