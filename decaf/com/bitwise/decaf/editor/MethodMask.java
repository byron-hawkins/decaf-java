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
import org.hs.util.*;
import org.hs.generator.*;

import com.bitwise.decaf.editor.config.*;
import com.bitwise.decaf.editor.grammar.*;

public class MethodMask extends Mask
{
	static MethodConfig config;
	
	protected DecafTextComponent name;
	protected MaskComponent returnType;
	
	protected DMethod data;
	
	protected ParagraphBox body;
	
	MethodMask(DMethod data)
	{
		super(config);
		
		this.body = null;
		
		try
		{
			NameConfig nameConfig = config.getNameConfig();
			Class nameClass = Class.forName(nameConfig.getClassname());
			this.name = (DecafTextComponent)nameClass.newInstance();
			this.name.setColumns(23);
			this.name.setLabelText("Name");
			this.name.setRepresents(GConstruct.getField(GMethod.class, "name"));
			super.addMaskComponent(this.name, nameConfig.getFormConstraints());
			
			ReturnConfig returnConfig = config.getReturnConfig();
			Class returnClass = Class.forName(returnConfig.getClassname());
			this.returnType = (MaskComponent)returnClass.newInstance();
			
			if (returnType instanceof DecafTypeComponent)
			{
				DecafTypeComponent typeComponent = (DecafTypeComponent)this.returnType;
				typeComponent.setLabelText("Return Type");
				typeComponent.setRepresents(GConstruct.getField(GMethod.class, "returnType"));
				typeComponent.setType(Utils.voidType);
			}
			super.addMaskComponent(this.returnType, returnConfig.getFormConstraints());
		}
		catch (Exception e)
		{
			DecafEditor.log(e);
		}
		
		this.data = data;
	}
	
	public void assemble()
	{
		super.addMaskComponents();

		this.body = new ParagraphBox("Body", ((DMethod)this.data).bodyGrammar);

		super.addFormComponent(body, new FormConstraints(1000, 0, FormLayout.LABEL_ON_TOP));
		
		super.display(this.data);
	}

	public void register(HotComponent.Arbiter arbiter)
	{
		super.register(arbiter);
		arbiter.offer(String.class, COPY);
	}
	
	public Object give(Class type, int operation)
	{
		if (type == String.class)
		{
			return this.name.getText();
		}
		/*
		if (type == GMethod.class)
		{
			DMethod method = new DMethod(null);
			super.apply(method);
			return method;
		}
		*/
		return null;
	}

	public void addNameListener(KeyListener nameListener)
	{
		this.name.addKeyListener(nameListener);
	}
	
	public void apply(DMethod data)
	{
		super.apply(data);
		data.update();
		this.body.terminate();
	}
}