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
 * JLS 6.5.2 Reclassification of Contextually Ambiguous Names
 */
public class GReference extends GConstruct implements GExpression
{
	static final long serialVersionUID = 6519657230548353207L;

	protected String name;
	protected GExpression prefix;
	
	public GReference(String name)
	{
		super("???");

		this.name = name.replace('.','/');
		this.prefix = null;
	}

	GReference(JExpression expression)
	{
		super(expression);
		this.name = "";
		expression.accept(new Decoder());
	}

	public JExpression encodeExpression()
	{
		if (this.prefix == null)
		{
			if (this.name.indexOf("/") > 0)
			{
				return JNameExpression.build(super.tokenReference, this.name);
			}
			else
			{
				return (new JNameExpression(super.tokenReference, this.name));
			}
		}
		else
		{
			return (new JNameExpression(super.tokenReference, 
										this.prefix.encodeExpression(), 
										this.name));	
		}
	}
	
	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a name expression
		 */
		public void visitNameExpression(JNameExpression self,
										JExpression prefix,	// a chain of JNameExpression
										String ident)
		{
			if (GReference.this.name.length() == 0)
			{
				GReference.this.name = ident;
			}
			else
			{
				GReference.this.name = ident + "." + GReference.this.name;
			}
			
			if (prefix != null)
			{
				GReference.this.prefix = GDecoder.decode(prefix);
			}
		}

		/**
		 * visits a local variable expression
		 */
		public void visitLocalVariableExpression(JLocalVariableExpression self,
												 String ident)
		{
			GReference.this.name = ident;
		}
	}
	
	public String toString()
	{
		return "prefix(" + prefix + ") name(" + this.name + ")";
	}
}
