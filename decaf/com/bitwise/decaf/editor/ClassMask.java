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
import javax.swing.border.*;
import javax.swing.text.*;

import org.hs.jfc.*;
import org.hs.util.*;
import org.hs.generator.*;

import com.bitwise.decaf.editor.config.*;

/**
 * Content pane for the {@link ClassEditor}, this class has only the single standard component
 * {@link this.name} (as a class may be defined by nothing more than a name).  Additional components
 * are specified via xml and {@link DecafConfig}.  
 */
public class ClassMask extends Mask
{
	static ClassConfig config;

	protected DecafTextComponent name;
	protected DVariable thisReference;
	
	ClassMask()
	{
		super(config);

		try
		{
			NameConfig nameConfig = config.getNameConfig();
			Class nameClass = Class.forName(nameConfig.getClassname());
			this.name = (DecafTextComponent)nameClass.newInstance();
			this.name.setColumns(23);
			this.name.setRepresents(GConstruct.getField(GClass.class, "name"));
			this.name.setLabelText("Name");
			super.addMaskComponent(this.name, nameConfig.getFormConstraints());
			
			super.addFormComponent(new ThisReference(), new FormConstraints(10000, 0));
			
			this.thisReference = null;
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
	
	public String getName()
	{
		return this.name.getText();
	}

	public void display(GConstruct code)
	{
		this.thisReference = DVariable.getThis((Cup)code);
		super.display(code);
	}
	
	public void addNameListener(KeyListener nameListener)
	{
		this.name.addKeyListener(nameListener);
	}
	
	public Collection actions()
	{
		return this.actions;
	}

	class ThisReference extends JPanel implements FormComponent, HotComponent
	{
		protected Border plainBorder;
		protected Border hotBorder;

		public ThisReference()
		{
			super();
			
			super.setBorder(this.plainBorder = BorderFactory.createLineBorder(Color.black, 1));
			super.add(new JLabel("Reference to this"));
			super.setBackground(Color.white);

			//DecafEditor.hotStation.register((HotComponent)this);
		}
		
		public Component getLabel()
		{
			return null;
		}
		
		public Component getComponent()
		{
			return this;
		}
		
		public Component getComponentView()
		{
			return this;
		}
		
		public void register(HotComponent.Arbiter arbiter)
		{
			arbiter.offer(GExpression.class, COPY);
		}
		
		public boolean pick(boolean b)
		{
			if (b)
			{
				System.err.println("Applying hot border: " + this.hotBorder);
				super.setBorder(this.hotBorder);
				super.revalidate();
			}
			else
			{
				super.setBorder(this.plainBorder);
			}
			
			return true;
		}
		
		public Object give(Class type, int operation)
		{
			return ClassMask.this.thisReference.shallowCopy();
		}
		
		public void setHotColor(Color hot)
		{
			this.hotBorder = BorderFactory.createLineBorder(hot, 1);
			DecafEditor.log("Setting hot border: " + this.hotBorder);
		}

		public boolean isCompatible(Object hot)
		{
			return false;
		}
		
		public void take(Object hot)
		{
		}

	
		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			throw (new Utils.SerialException(getClass()));
		}
	}

/*
	class NewClass extends ActionBase
	{
		public NewClass()
		{
			super();
			
			super.putValue(Action.SHORT_DESCRIPTION, "Create a new class");
			super.putValue(Action.NAME, "New");
		}
		
		public void go(ActionEvent event)
		{
			System.err.println(getClass().getName() + ".actionPerformed()");
		}
	}
	
	class EditClass extends ActionBase
	{
		public EditClass()
		{
			super();
			
			super.putValue(Action.SHORT_DESCRIPTION, "Edit the selected class");
			super.putValue(Action.NAME, "Edit");
		}
		
		public void go(ActionEvent event)
		{
			System.err.println(getClass().getName() + ".actionPerformed()");
		}
	}
*/	
}