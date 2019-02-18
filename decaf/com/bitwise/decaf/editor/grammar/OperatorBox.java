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
 
package com.bitwise.decaf.editor.grammar;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.hs.jfc.*;
import org.hs.generator.*;

import com.bitwise.decaf.editor.*;
import com.bitwise.decaf.editor.config.GrammarConfig;

public class OperatorBox extends JComboBox implements FormComponent, ItemListener
{
	public static final String LIAISON = "operator box liaison";
	static final OperatorCache operatorCache;
	static final Operator unknown = new Operator("??", GComparison.UNKNOWN);
	
	static OperatorComboBoxModel binaryModel;
	static OperatorComboBoxModel binaryAssignModel;
	static OperatorComboBoxModel comparisonModel;
	
	static
	{
		operatorCache = new OperatorCache();
		
		operatorCache.add(new Operator("=", GBinary.SIMPLE));
		operatorCache.add(new Operator("+", GBinary.ADD));
		operatorCache.add(new Operator("-", GBinary.SUBTRACT));
		operatorCache.add(new Operator("*", GBinary.MULTIPLY));
		operatorCache.add(new Operator("/", GBinary.DIVIDE));
		operatorCache.add(new Operator("%", GBinary.MODULO));
		operatorCache.add(new Operator("+=", GBinary.ADD, true));
		operatorCache.add(new Operator("-=", GBinary.SUBTRACT, true));
		operatorCache.add(new Operator("*=", GBinary.MULTIPLY, true));
		operatorCache.add(new Operator("/=", GBinary.DIVIDE, true));
		operatorCache.add(new Operator("%=", GBinary.MODULO, true));
		operatorCache.add(new Operator("<", GComparison.LT));
		operatorCache.add(new Operator("<=", GComparison.LE));
		operatorCache.add(new Operator(">", GComparison.GT));
		operatorCache.add(new Operator(">=", GComparison.GE));
		operatorCache.add(new Operator("==", GComparison.EQ));
		operatorCache.add(new Operator("!=", GComparison.NE));
		operatorCache.add(new Operator("and", GComparison.AND));
		operatorCache.add(new Operator("or", GComparison.OR));

		binaryModel = new OperatorComboBoxModel();
		binaryModel.addElement(operatorCache.get("="));
		binaryModel.addElement(operatorCache.get("+"));
		binaryModel.addElement(operatorCache.get("-"));
		binaryModel.addElement(operatorCache.get("*"));
		binaryModel.addElement(operatorCache.get("/"));
		binaryModel.addElement(operatorCache.get("%"));
		binaryModel.addElement(operatorCache.get("+="));
		binaryModel.addElement(operatorCache.get("-="));
		binaryModel.addElement(operatorCache.get("*="));
		binaryModel.addElement(operatorCache.get("/="));
		binaryModel.addElement(operatorCache.get("%="));
		
		binaryAssignModel = new OperatorComboBoxModel();
		binaryAssignModel.addElement(operatorCache.get("="));
		binaryAssignModel.addElement(operatorCache.get("+="));
		binaryAssignModel.addElement(operatorCache.get("-="));
		binaryAssignModel.addElement(operatorCache.get("*="));
		binaryAssignModel.addElement(operatorCache.get("/="));
		binaryAssignModel.addElement(operatorCache.get("%="));
		
		comparisonModel = new OperatorComboBoxModel();
		comparisonModel.addElement(operatorCache.get("<"));
		comparisonModel.addElement(operatorCache.get("<="));
		comparisonModel.addElement(operatorCache.get(">"));
		comparisonModel.addElement(operatorCache.get(">="));
		comparisonModel.addElement(operatorCache.get("=="));
		comparisonModel.addElement(operatorCache.get("!="));
		comparisonModel.addElement(operatorCache.get("and"));
		comparisonModel.addElement(operatorCache.get("or"));
	}
	
	protected JLabel label;
	
	protected GConstruct data;
	protected Grammar grammar;
	
	// not setting handle.data properly for Comparison
	public OperatorBox(OperatorComboBoxModel model, String label, Grammar grammar)
	{
		super(model.copy());
		
		super.addItemListener(this);
		
		this.grammar = grammar;
		this.data = grammar.getSource();
		this.label = new JLabel(label);
		
		((OperatorComboBoxModel)this.getModel()).setSelectedItem(data);
		
		apply();
	}
	
	public Component getLabel()
	{
		return this.label;
	}
	
	public Component getComponent()
	{
		return this;
	}
	
	public Component getComponentView()
	{
		return this;
	}

	public void apply()
	{
		((Operator)super.getSelectedItem()).apply(this.data);
		this.grammar.notifyNewData();
	}
	
	public Collection actions()
	{
		return (new Vector());
	}
	
	public void itemStateChanged(ItemEvent event)
	{
		if (event.getStateChange() == ItemEvent.SELECTED)
		{
			apply();
		}
	}

	static class Operator implements Comparable
	{
		protected String display;
		protected int value;
		protected boolean assign;

		public Operator(GBinary data)
		{
			this("", data.operator, data.compoundAssignment);
		}
		
		public Operator(GComparison data)
		{
			this("", data.operator, false);
		}
		
		public Operator(String display, int value)
		{
			this(display, value, false);
		}
		
		public Operator(String display, int value, boolean assign)
		{
			this.display = display;
			this.value = value;
			this.assign = assign;
		}
		
		public void apply(GConstruct data)
		{
			if (data instanceof GBinary)
			{
				GBinary binary = (GBinary)data;
				binary.operator = this.value;
				binary.compoundAssignment = this.assign;
			}
			else if (data instanceof GComparison)
			{
				GComparison comparison = (GComparison)data;
				comparison.operator = this.value;
			}
		}
		
		public int compareTo(Object o)
		{
			if (o instanceof Operator)
			{
				Operator other = (Operator)o;
				if (this.assign)
				{
					if (!other.assign)
					{
						return -1;
					}
				}
				else
				{
					if (other.assign)
					{
						return 1;
					}
				}
				return (this.value - other.value);
			}
			throw (new IllegalArgumentException("Can't compare a " + getClass().getName() + " to a " + o.getClass().getName()));
		}			
		
		public boolean equals(Object o)
		{
			if (o instanceof Operator)
			{
				Operator other = (Operator)o;
				return ((this.value == other.value) && (this.assign == other.assign));
			}
			return false;
		}
		
		public String toString()
		{
			return this.display;
		}
	}
	
	static class OperatorCache 
	{
		protected TreeMap byDisplay;
		protected TreeMap byValues;
		
		public OperatorCache()
		{
			this.byDisplay = new TreeMap();
			this.byValues = new TreeMap();
		}
		
		public void add(Operator operator)
		{
			this.byDisplay.put(operator.display, operator);
			this.byValues.put(operator, operator);
		}
		
		public Operator get(String display)
		{
			return (Operator)this.byDisplay.get(display);
		}
		
		public Operator get(GBinary data)
		{
			return (Operator)this.byValues.get(new Operator(data));
		}
		
		public Operator get(GComparison data)
		{
			if (data.operator == GComparison.UNKNOWN)
			{
				return unknown;
			}
			return (Operator)this.byValues.get(new Operator(data));
		}
	}
	
	static class OperatorComboBoxModel extends DefaultComboBoxModel implements Cloneable
	{
		public void setSelectedItem(GConstruct o)
		{
			if (o instanceof GBinary)
			{
				GBinary data = (GBinary)o;
				if (data.operator == GBinary.UNKNOWN)
				{
					return;
				}
				
				super.setSelectedItem(operatorCache.get(data));
			}
			if (o instanceof GComparison)
			{
				GComparison data = (GComparison)o;
				if (data.operator == GComparison.UNKNOWN)
				{
					return;
				}

				super.setSelectedItem(operatorCache.get(data));
			}
		}
		
		public OperatorComboBoxModel copy()
		{
			try
			{
				return (OperatorComboBoxModel)this.clone();
			}
			catch (CloneNotSupportedException notByTheHairsOfMyChinnyChinChin)
			{
			}
			return null;
		}
	}
	
	public Object writeReplace()
		throws java.io.ObjectStreamException
	{
		throw (new Utils.SerialException(getClass()));
	}
}