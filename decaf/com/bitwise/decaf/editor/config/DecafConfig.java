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

import java.io.InputStream;
import javax.xml.bind.Dispatcher;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.marshal.XMLScanner;

import org.hs.util.file.*;

public class DecafConfig extends EditorConfig
{
	protected SuperFile installRoot;

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
    	super.unmarshal(u);

		this.installRoot = new SuperFile(this.getInstallation().getInstallRoot());
		if (!this.installRoot.isDirectory())
		{
			this.installRoot = new SuperFile("..");
    		if (!this.installRoot.isDirectory())
    		{
	    		System.err.println("Can't establish directory '..'; aborting.");
	    		System.exit(1);
	    	}
    		System.err.println("Unable to open install root " + this.getInstallation().getInstallRoot() + ".  Guessing " + this.installRoot.getAbsolutePath());
    	}
    }

    public static EditorConfig unmarshal(InputStream in)
        throws UnmarshalException
    {
    	XMLScanner xs = XMLScanner.open(in);
    	Dispatcher d = newDispatcher();
    	
    	DecafConfig config = (DecafConfig) d.unmarshal(xs, (DecafConfig.class));
    	
        return config;
    }

    public static Dispatcher newDispatcher() 
    {
        return ClassConfig.newDispatcher();
    }
    
    public Installation getInstallation()
    {
    	return (Installation)super.getSetup();
    }
    
    public SuperFile getInstallRoot()
    {
    	return this.installRoot;
    }
    
    public ClassConfig getClassConfig()
    {
    	return (ClassConfig)super.getClassComponents();
    }
    
    public MethodConfig getMethodConfig()
    {
    	return (MethodConfig)super.getMethodComponents();
    }
    
    public ParameterConfig getParameterConfig()
    {
    	return (ParameterConfig)super.getParameterComponents();
    }
    
    public FieldConfig getFieldConfig()
    {
    	return (FieldConfig)super.getFieldComponents();
    }
    
    public LocalConfig getLocalConfig()
    {
    	return (LocalConfig)super.getLocalComponents();
    }
    
    public GrammarConfig getGrammarConfig()
    {
    	GrammarConfig grammarConfig = (GrammarConfig)super.getGrammars();
    	if (grammarConfig == null)
    	{
    		return (new GrammarConfig());
    	}
    	return grammarConfig;
    }
    
    public TypeChooserConfig getTypeChooserConfig()
    {
    	return (TypeChooserConfig)super.getTypeChooserContent();
    }
    
    public TypeCatalog getTypeCatalog()
    {
    	return (TypeCatalog)super.getTypeDefinitions();
    }
/*    
    public Hook getHook(jHandle handle)
    {
    	
    }
*/    
	public void initConstraints()
	{
    	ClassConfig classConfig = (ClassConfig)super.getClassComponents();
    	classConfig.initConstraints();

    	MethodConfig methodConfig = (MethodConfig)super.getMethodComponents();
    	methodConfig.initConstraints();

    	FieldConfig fieldConfig = (FieldConfig)super.getFieldComponents();
    	if (fieldConfig != null)
    	{
    		fieldConfig.initConstraints();
    	}

    	ParameterConfig parameterConfig = (ParameterConfig)super.getParameterComponents();
    	if (parameterConfig != null)
    	{
	    	parameterConfig.initConstraints();
    	}

    	LocalConfig localConfig = (LocalConfig)super.getLocalComponents();
    	if (localConfig != null)
    	{
	    	localConfig.initConstraints();
    	}
	}
}
