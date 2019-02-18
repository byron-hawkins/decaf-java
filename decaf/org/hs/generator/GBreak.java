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
 *	This class represents a <code>break</code> statement in the context of
 * a conditional loop or switch statement.  It may bear a label, which can refer
 * (via java.lang.String.equals()) to the labelled loop or switch construct
 * it intends to terminate.  
 */
public class GBreak extends GConstruct implements GStatement
{
	static final long serialVersionUID = 2791864818207917946L;

	protected String label;

	/**
	 *	A break statement with no label.
	 */	
	public GBreak()
	{
		this((GLoop)null);
	}
	
	/**
	 * A break statement referring to the loop or switch construct
	 * with label matching (via java.lang.String.equals()) <code>target</code>.
	 */
	public GBreak(GLoop target)
	{
		super("???");
		
		if (target == null)
		{
			this.label = null;
		}
		else
		{
			this.label = target.label;
		}
	}
	
	GBreak(JStatement statement)
	{
		super(statement);
		statement.accept(new Decoder());
	}
	
	public JStatement encodeStatement()
	{
		return (new JBreakStatement(super.tokenReference,
									this.label,
									super.encodeComments()));
	}
	
	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a break statement
		 */
		public void visitBreakStatement(JBreakStatement self,
										String label)
		{
			GBreak.this.label = label;
		}
	}

	public String toString()
	{
		return "label(" + this.label + ")";
	}
}
