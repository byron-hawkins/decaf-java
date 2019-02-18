package org.hs.util;

import java.util.*;

public class BitSetMap
{
	protected Vector values;
	
	public BitSetMap()
	{
		this.values = new Vector();
	}
	
	public void put(BitSet id, Object value)
	{
		Mapping mapping = new Mapping(id, value);
		this.values.add(mapping);
	}
	
	public void remove(Object value)
	{
		this.values.remove(value);
	}
	
	public Iterator get(BitSet lookup)
	{
		Vector results = new Vector();
		Mapping mapping;
		
		for (int i = 0; i < this.values.size(); i++)
		{
			mapping = (Mapping)this.values.elementAt(i);
			if (mapping.matches(lookup))
			{
				results.add(mapping.getValue());
			}
		}
		return results.iterator();
	}
	
	class Mapping 
	{
		protected BitSet id;
		protected Object value;
		
		public Mapping(BitSet id, Object value)
		{
			this.id = id;
			this.value = value;
		}
		
		public boolean matches(BitSet lookup)
		{
			for (int i = 0; i < lookup.length(); i++)
			{
				if (this.id.get(i) && lookup.get(i))
				{
					return true;
				}
			}
			return false;
		}
		
		public Object getValue()
		{
			return this.value;
		}
		
		public boolean equals(Object o)
		{
			return (this.value.equals(o));
		}
	}
}