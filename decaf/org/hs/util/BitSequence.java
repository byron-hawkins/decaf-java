package org.hs.util;

import java.util.*;

public class BitSequence
{
	public static final int BITWISE = 0;
	public static final int INTEGRAL = 1;
	
	protected int index = 0;
	
	public BitSequence()
	{
	}
	
	public BitSequence(int start)
	{
		this.index = start;
	}
	
	public BitSequence(Bits start, int method)
	{
		if (method == BITWISE)
		{
			this.index = start.length();
		}
		else
		{
			throw (new RuntimeException("Not implemented: BitsSequence(Bits, int) with method INTEGRAL"));
		}
	}
	
	public static int indexOfFirstBit(int bits)
	{
		int index = 0;
		while (bits > 0)
		{
			if ((bits % 2) == 1)
			{
				return index;
			}
			
			bits >>= 1;
			index++;
		}
		
		return -1;
	}
	
	public static int indexOfNthBit(int n, int bits)
	{
		int counter = 0;
		int index = 0;
		int stepsToNext;

		do		
		{
			stepsToNext = indexOfFirstBit(bits);
			if (stepsToNext == -1)
			{
				return -1;
			}
			index += stepsToNext;
			bits >>= stepsToNext;
			counter++;
		}
		while (counter < n);
		
		return index;
	}
	
	public static int setFirstBit(int index)
	{
		int value = 1;
		
		while (index-- > 0)
		{
			value <<= 1;
		}
		
		return value;
	}
	
	public Bits next(int method)
	{
		Bits Bits;
		
		if (method == BITWISE)
		{
			Bits = new Bits(index + 1);
			Bits.set(this.index++);
			return Bits;
		}
		else
		{
			Bits = new Bits();
			
			int value = this.index++;
			int position = 0;
			
			while (value > 0)
			{
				if ((value % 2) > 0)
				{
					Bits.set(position);
				}
				value >>= 1;
				position++;
			}
		}
		return null;
	}
}
	