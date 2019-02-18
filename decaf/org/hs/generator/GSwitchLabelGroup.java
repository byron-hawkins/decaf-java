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
 * A set of confluent case labels in a switch statement.
 */
public class GSwitchLabelGroup extends GConstruct 
{
	static final long serialVersionUID = 6758483130884036236L;

	public static final Object DEFAULT = new Object();
	
	/**
	 * The case labels, a collection of GLiteral.
	 */
	public Vector labels;	
	
	/**
	 * The block to execute under these case conditions.
	 */
	public GBlock block;
	
	public GSwitchLabelGroup()
	{
		super("???");
		init();
	}
	
	GSwitchLabelGroup(JSwitchGroup group)
	{
		super(group);
		init();
		group.accept(new Decoder());
	}
	
	private void init()
	{
		this.labels = new Vector();
		this.block = new GBlock();
	}

	public JSwitchGroup encode()
	{
		return (new JSwitchGroup(super.tokenReference, 
								 encodeLabels(), 
								 this.block.encodeStatements()));
	}
	
	protected JSwitchLabel[] encodeLabels()
	{
		JSwitchLabel[] encoded = new JSwitchLabel[this.labels.size()];
		Object nextLabel;
		for (int i = 0; i < this.labels.size(); i++)
		{
			nextLabel = labels.elementAt(i);
			if (nextLabel == DEFAULT)
			{
				encoded[i] = new JSwitchLabel(super.tokenReference, null);
			}
			else
			{
				encoded[i] = new JSwitchLabel(super.tokenReference, ((GLiteral)nextLabel).encodeExpression());
			}
		}
		
		return encoded;
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits an array length expression
		 */
		public void visitSwitchLabel(JSwitchLabel self,
									 JExpression expr)
		{
			if (expr == null)
			{
				GSwitchLabelGroup.this.labels.add(DEFAULT);
			}
			else
			{
				GSwitchLabelGroup.this.labels.add(new GLiteral(expr));
			}
		}
	
		/**
		 * visits an array length expression
		 */
		public void visitSwitchGroup(JSwitchGroup self,
									 JSwitchLabel[] labels,
									 JStatement[] stmts)
		{
			for (int i = 0; i < labels.length; i++)
			{
				labels[i].accept(this);
			}
			
			for (int i = 0; i < stmts.length; i++)
			{
				GSwitchLabelGroup.this.block.add(GDecoder.decode(stmts[i]));
			}
		}
	}

	public String toString()
	{
		return "labels(" + this.labels + ") block(" + this.block + ")";
	}
}

