/*
 * Copyright (C) 2003 HawkinsSoftware
 *
 * This Java code generator package is free 
 * software.  You can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software 
 * Foundation.  However, no compilation of this code or a derivative
 * of it may be used with or integrated into any commercial application,
 * except by the written permisson of HawkinsSoftware.  Future versions 
 * of this product will be sold commercially under a different license.  
 * HawkinsSoftware retains all rights to this product, including its
 * concepts, design and implementation.
 *
 * This package is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
 
package org.hs.generator;

import at.dms.kjc.*;

import java.util.*;

/**
 * A switch statement, using GSwitchLabelGroup for case labels.
 */
public class GSwitch extends GLoop implements GStatement
{
	static final long serialVersionUID = -1801270181297957427L;

	/**
	 * The expression to switch on.
	 */
	public GExpression on;
	
	/**
	 * A collection of GSwitchLabelGroup comprising the case labels
	 * for this switch statement.  The contents of the cases are included
	 * within GSwitchLabelGroup.
	 */
	public Vector labelGroups;	
	
	/**
	 * Convenience constructor.
	 */
	public GSwitch(GExpression on)
	{
		super("???");
		
		this.on = on;
		init();
	}
	
	GSwitch(JStatement statement)
	{
		this(statement, null);
	}
	
	GSwitch(JStatement statement, String label)
	{
		super(statement);
		init();
		super.label = label;
		statement.accept(new Decoder());
	}
	
	private void init()
	{
		this.labelGroups = new Vector();
	}

	public JStatement encodeStatement()
	{
		return super.encodeStatement(new JSwitchStatement(super.tokenReference,
								     					   this.on.encodeExpression(),
								     					   encodeLabels(),
								     					   super.encodeComments()));
	}

	protected JSwitchGroup[] encodeLabels()
	{
		JSwitchGroup[] encoded = new JSwitchGroup[this.labelGroups.size()];
		
		GSwitchLabelGroup group;
		for (int i = 0; i < this.labelGroups.size(); i++)
		{
			group = (GSwitchLabelGroup)this.labelGroups.elementAt(i);
			encoded[i] = group.encode();
		} 

		return encoded;
		
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a switch statement
		 */
		public void visitSwitchStatement(JSwitchStatement self,
										 JExpression expr,
										 JSwitchGroup[] body)
		{
			GSwitch.this.on = GDecoder.decode(expr);
			
			for (int i = 0; i < body.length; i++)
			{
				GSwitch.this.labelGroups.add(new GSwitchLabelGroup(body[i]));
			}
		}
	}

	public String toString()
	{
		return "switch on(" + this.on + ") performing(" + this.labelGroups + ")";
	}
}
