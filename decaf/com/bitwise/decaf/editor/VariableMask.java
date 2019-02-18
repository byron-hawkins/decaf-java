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
 
package com.bitwise.decaf.editor;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import org.hs.jfc.*;
import org.hs.generator.*;

import com.bitwise.decaf.editor.config.*;

public class VariableMask extends Mask
{
	protected static TreeMap configs;
	
	static void add(FieldConfig config)
	{
		addConfig(Fields.class.getName(), config);
	}
	static void add(ParameterConfig config)
	{
		addConfig(Parameters.class.getName(), config);
	}
	static void add(LocalConfig config)
	{
		addConfig(Locals.class.getName(), config);
	}
	
	protected static void addConfig(String dataClassname, Object config)
	{
		if (config != null)
		{
			configs.put(dataClassname, config);
		}
	}

	protected DecafTextComponent name;
	protected DecafTypeComponent type;
	
	static
	{
		configs = new TreeMap();
	}

	public VariableMask(String destination)
	{
		super((MaskConfig)configs.get(destination));

		try
		{
			NameConfig nameConfig = super.config.getNameConfig();
			Class nameClass = Class.forName(nameConfig.getClassname());
			this.name = (DecafTextComponent)nameClass.newInstance();
			this.name.setColumns(23);
			this.name.setLabelText("Name");
			this.name.setRepresents(GConstruct.getField(GVariable.class, "name"));
			super.addMaskComponent(this.name, nameConfig.getFormConstraints());
			
			TypeConfig typeConfig = super.config.getTypeConfig();
			Class typeClass = Class.forName(typeConfig.getClassname());
			this.type = (DecafTypeComponent)typeClass.newInstance();
			this.type.setLabelText("Type");
			this.type.setRepresents(GConstruct.getField(GVariable.class, "type"));
			super.addMaskComponent(this.type, typeConfig.getFormConstraints());
		}
		catch (Exception e)
		{
			DecafEditor.log(e);
		}
	}
	
	public void assemble()
	{
		super.addMaskComponents();
	}
/*	
	public Object give(Class type)
	{
		if (type == GVariable.class)
		{
			DVariable variable = new DVariable(false);	// boolean isLocal
			super.apply(variable);
			return variable;
		}

		return null;
	}
*/	
	public void apply(DVariable data)
	{
		super.apply(data);
		data.update();
	}
}