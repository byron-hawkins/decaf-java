package org.hs.util;

import java.io.*;
import java.util.*;

public class ComparableBoolean implements Comparable, Serializable
{
	protected boolean value;
	
	public ComparableBoolean(boolean value)
	{
		this.value = value;
	}
	
	public ComparableBoolean(String value)
	{
		this.value = Boolean.valueOf(value).booleanValue();
	}
	
	public int compareTo(Object o)
	{
		ComparableBoolean other = (ComparableBoolean)o;
		
		return compare(this.value, other.value);
	}
	
	public int compare(boolean one, boolean two)
	{
		if (one)
		{
			if (two)
			{
				return 0;
			}
			return -1;
		}
		if (two)
		{
			return 1;
		}
		return 0;
	}

	public boolean booleanValue()
	{
		return this.value;
	}

	public String toString()
	{
		return String.valueOf(this.value);
	}
}
