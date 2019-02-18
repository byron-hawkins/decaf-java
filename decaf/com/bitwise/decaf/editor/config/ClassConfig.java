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

public class ClassConfig extends ClassComponents implements MaskConfig
{
	public List getOptionalComponents()
	{
		return super.getCustomComponent();
	}
	
	public void initConstraints()
	{
		getNameConfig().initConstraints();
		
		Iterator iterator = this.getOptionalComponents().iterator();
		while (iterator.hasNext())
		{
			((OptionalComponent)iterator.next()).initConstraints();
		}
	}
	
    public static ClassComponents unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((ClassConfig) d.unmarshal(xs, (ClassConfig.class)));
    }

    public static Dispatcher newDispatcher() 
    {
        Dispatcher d = new Dispatcher();
        d.register("Setup", (Installation.class));
        d.register("ClassComponents", (ClassConfig.class));
        d.register("CustomComponent", (OptionalComponent.class));
        d.register("EditorConfig", (DecafConfig.class));
        d.register("FieldComponents", (FieldConfig.class));
        d.register("Grammars", (GrammarConfig.class));
        d.register("FormLocation", (FormConstraints.class));
        d.register("LocalComponents", (LocalConfig.class));
        d.register("MethodComponents", (MethodConfig.class));
        d.register("NameComponent", (NameConfig.class));
        d.register("Represents", (Represents.class));
        d.register("ParameterComponents", (ParameterConfig.class));
        d.register("ReturnComponent", (ReturnConfig.class));
        d.register("TypeComponent", (TypeConfig.class));
        d.register("TypeCategory", (TypeCategoryConfig.class));
        d.register("TypeItem", (TypeItem.class));
        d.register("TypeChooserContent", (TypeChooserConfig.class));
        d.register("TypeDefinitions", (TypeCatalog.class));
        d.register("TypeDefinition", (TypeDetails.class));
        d.register("Field", (FieldDetails.class));
        d.register("Method", (MethodDetails.class));
        d.register("Parameter", (ParameterDetails.class));
        d.freezeElementNameMap();
        return d;
    }
    
    public NameConfig getNameConfig()
    {
    	return (NameConfig)super.getNameComponent();
    }
    
    public TypeConfig getTypeConfig()
    {
    	return null;
    }
}
