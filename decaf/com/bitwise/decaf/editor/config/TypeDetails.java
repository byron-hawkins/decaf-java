/*
 * Copyright (C) 2003 HawkinsSoftware
 *
 * This prototype of the Decaf Java development environment is free 
 * software.  You can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software 
 * Foundation.  However, no compilation of this code or a derivative
 * of it may be used with or integrated into any commercial application,
 * except by the written permisson of HawkinsSoftware.  Future versions 
 * of this product will be sold commercially under a different license.  
 * HawkinsSoftware retains all rights to this product, including its
 * concepts, design and implementation.
 *
 * This prototype is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
 
package com.bitwise.decaf.editor.config;

import java.util.*;

import javax.xml.bind.Dispatcher;
import javax.xml.bind.UnmarshalException;
import javax.xml.marshal.XMLScanner;

import com.bitwise.decaf.editor.Method;
import com.bitwise.decaf.editor.Type;

public class TypeDetails extends TypeDefinition
{
    public List getMethods() 
    {
    	return super.getMethod();
    }
    
    public List getFields()
    {
    	return super.getField();
    }
    
    public MethodDetails getMethodDetails(Method method)
    {
    	MethodDetails details;
    	Iterator iterator = this.getMethods().iterator();
    	while (iterator.hasNext())
    	{
    		details = (MethodDetails)iterator.next();
   			if (details.matches(method))
   			{
   				return details;
   			}
   		}
   		return null;
   	}

	public FieldDetails getFieldDetails(Type.Field field)
	{
		FieldDetails details;
		Iterator iterator = this.getFields().iterator();
		while (iterator.hasNext())
		{
			details = (FieldDetails)iterator.next();
			if (details.matches(field))
			{
				return details;
			}
		}
		
		return null;
	}
	
	public boolean superMethodsRequireDetails()
	{
		String s = super.getSuperMethods();
		
		if (!(s.equals("DETAILED") || s.equals("ALL")))
		{
			throw (new IllegalArgumentException("Invalid specifier for super methods: '" + s + "'; must be 'ALL' or 'DETAILED'"));
		}
		
		return s.equals("DETAILED");
	}
	
	public boolean declaredMethodsRequireDetails()
	{
		String s = super.getDeclaredMethods();
		
		if (!(s.equals("DETAILED") || s.equals("ALL")))
		{
			throw (new IllegalArgumentException("Invalid specifier for declared methods: '" + s + "'; must be 'ALL' or 'DETAILED'"));
		}
		
		return s.equals("DETAILED");
	}
		   	
   	public boolean hasJavadoc()
   	{
   		return false;
   	}

    public static TypeDefinition unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((TypeDefinition) d.unmarshal(xs, (TypeDetails.class)));
    }
}
