package org.hs.util;

import java.util.*;

public class RandomUtility extends Random
{
	public RandomUtility()
	{
		super();
	}
	
	public int nextInt(int bound)
	{
		if (bound == 0)
		{
			return 0;
		}
		
		int sign = 1;
		
		if (bound < 0)
		{
			bound = -bound;
			sign = -1;
		}
		
		int random = super.nextInt();
		if (random < 0)
		{
			random = -random;
		}
		
		return sign * (random % bound);
	}
	
	public boolean choose(int select, int range)
	{
		if (select >= range)
		{
			return true;
		}
		
		if ((select <= 0) || (range <= 0))
		{
			return false;
		}
		
		int random = nextInt(range);
		
		return (random <= select);
	}
}
