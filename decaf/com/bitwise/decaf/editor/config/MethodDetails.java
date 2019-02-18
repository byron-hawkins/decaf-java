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
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.marshal.XMLScanner;

import com.bitwise.decaf.editor.Utils;
import com.bitwise.decaf.editor.Type;
import com.bitwise.decaf.editor.Method;

public class MethodDetails extends com.bitwise.decaf.editor.config.Method
{
	protected static Utils.CollectionMap userMethods;
	
	static
	{
		userMethods = new Utils.CollectionMap();
	}
	
	static void addUserMethodDetails(Iterator methods)
	{
		MethodDetails details;
		while (methods.hasNext())
		{
			details = (MethodDetails)methods.next();
			userMethods.add(details.getName(), details);
		}
	}
	
	public static MethodDetails getUserMethodDetails(Method method)
	{
		MethodDetails details;
		ParameterDetails parameterDetails;
		Method.Parameter methodParameter;
		int size;
		Collection byName = userMethods.getCollection(method.getName());
		if (byName == null)
		{
			return null;
		}
		Iterator candidates = byName.iterator();
		while (candidates.hasNext())
		{
			details = (MethodDetails)candidates.next();
			if (details.matches(method))
			{
				return details;
			}
		}
		
		return null;
	}
	
	protected Vector parameters;	// init!!
	
	public boolean matches(Method method)
	{
		String name = super.getName();
		if (name.equals("#init"))
		{
			name = "<init>";
		}
		if (!name.equals(method.getName()))
		{
			return false;
		}
		
		if (this.getParameters().size() != method.getParameters().size())
		{
			return false;
		}
		
		Iterator detailParameters = this.getParameters().iterator();
		Iterator methodParameters = method.getParameters().iterator();
		ParameterDetails parameterDetails;
		Type parameterType;
		while (detailParameters.hasNext())
		{
			parameterDetails = (ParameterDetails)detailParameters.next();
			parameterType = ((Method.Parameter)methodParameters.next()).getType();
			
			if (parameterDetails.countDimensions() != parameterType.typeSource().getDimensions())
			{
				return false;
			}
			
			if (!parameterDetails.getClassname().equals(parameterType.getQualifiedName()))
			{
				return false;
			}
		}
		
		return true;
	}
	
    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
    	super.unmarshal(u);
    	this.parameters = new Vector(this.getParameters());
    }

	public boolean hide()
	{
		return Boolean.valueOf(super.getHide()).booleanValue();
	}

	public List getParameters()
	{
		return super.getParameter();
	}
	
	public ParameterDetails getParameterDetails(int index)
	{
		return (ParameterDetails)this.parameters.elementAt(index);
	}
	
    public static com.bitwise.decaf.editor.config.Method unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((com.bitwise.decaf.editor.config.Method) d.unmarshal(xs, (MethodDetails.class)));
    }
}
