package org.hs.util;

import java.util.*;

public class Bits extends BitSet implements Cloneable
{
	public Bits()
	{
		super();
	}
	
	public Bits(int length)
	{
		super(length);
	}
	
	public static Bits represent(int value)
	{
		Bits bits = new Bits();
		int index = 0;
		
		while (value > 0)
		{
			if ((value % 2) > 0)
			{
				bits.set(index);
			}
			index++;
			value >>= 1;
		}
		
		return bits;
	}
	
	public Object clone()
	{
		try
		{
			return (Bits)super.clone();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public Bits orEquals(BitSet other)
	{
		Bits result = (Bits)this.clone();
		result.or(other);
		return result;
	}
	
	public Bits andEquals(BitSet other)
	{
		Bits result = (Bits)this.clone();
		result.and(other);
		return result;
	}
	
	public Bits xorEquals(BitSet other)
	{
		Bits result = (Bits)this.clone();
		result.xor(other);
		return result;
	}
	
	public Bits andNotEquals(BitSet other)
	{
		Bits result = (Bits)this.clone();
		result.andNot(other);
		return result;
	}
	
	public boolean hasTheseOnBits(BitSet these)
	{
		for (int i = 0; i < these.length(); i++)
		{
			if ((i > length()) && (these.get(i)))
			{
				return false;
			}
			
			if (these.get(i) && (!get(i)))
			{
				return false;
			}
		}
		return true;
	}
}