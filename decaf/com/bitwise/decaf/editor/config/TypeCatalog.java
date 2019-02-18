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

import com.bitwise.decaf.editor.Type;
import com.bitwise.decaf.editor.Utils;

public class TypeCatalog extends TypeDefinitions
{
	protected TreeMap catalog;
	
	public TypeCatalog()
	{
		super();
		
		this.catalog = new TreeMap();
	}
	
    public List getTypeDefinitions() 
    {
        return super.getTypeDefinition();
    }
    
    public TypeDetails getTypeDetails(String classname)
    {
    	return (TypeDetails)this.catalog.get(classname);
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
    	super.unmarshal(u);
    	
    	TypeDetails details;
    	Iterator types = this.getTypeDefinitions().iterator();
    	Type type;
    	while (types.hasNext())
    	{
    		details = (TypeDetails)types.next();
    		if (details.getClassname().equals("#user"))
    		{
    			MethodDetails.addUserMethodDetails(details.getMethods().iterator());
    		}
    		else
    		{
    			try
    			{
    				type = Utils.getType(details.getClassname());	// establish this type in the cache
    				catalog.put(details.getClassname(), details);
    			}
    			catch (ClassNotFoundException e)
    			{
    				com.bitwise.decaf.editor.DecafEditor.log("TypeCatalog.unmarshal(): Unable to locate class " + details.getClassname() + "; skipping type " + details.getUid());
    			}
    		}
    	}
    	
    	types = this.getTypeDefinitions().iterator();
    	while (types.hasNext())
    	{
    		details = (TypeDetails)types.next();
			try
			{
				type = Utils.getType(details.getClassname());
				type.apply(details);
			}
			catch (ClassNotFoundException alreadySeenIt)
			{
			}
    	}
    }
    
    public static TypeDefinitions unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((TypeDefinitions) d.unmarshal(xs, (TypeDetails.class)));
    }
}
