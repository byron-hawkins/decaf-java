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
 *	A catch clause of a try/catch/finally statement, consisting of a variable
 * to catch and a block to execute under that condition.  
 */
public class GCatch extends GConstruct
{
	static final long serialVersionUID = -6631631546751562370L;

	/**
	 * The variable to catch, which, like a parameter, is expected to have
	 * prefix and initializer set to null, isLocal set to true, and type
	 * set to some subclass of java.lang.Throwable.  The variable's modifiers
	 * will be ignored.
	 */
	public GVariable catchWhat;
	
	/**
	 * The block to execute under the condition of the catch clause.  The
	 * variable <code>catchWhat</code> will be available under the same terms
	 * as a parameter to a method.
	 */
	public GBlock body;
	
	/**
	 * Convenience constructor
	 */
	public GCatch(GVariable catchWhat)
	{
		super("???");
		
		this.catchWhat = catchWhat;
		this.body = new GBlock();
	}
	
	GCatch(JCatchClause clause)
	{
		super(clause);
		clause.accept(new Decoder());
	}
	
	public JCatchClause encode()
	{
		return (new JCatchClause(super.tokenReference,
								 this.catchWhat.encodeParameter(),
								 this.body.encodeBlock()));
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits an array length expression
		 */
		public void visitCatchClause(JCatchClause self,
									 JFormalParameter exception,
									 JBlock body)
		{
			GCatch.this.catchWhat = new GVariable(exception);
			GCatch.this.body = new GBlock(body);
		}
	}
	
	public String toString()
	{
		return "catching(" + this.catchWhat + ") and doing(" + this.body + ")";
	}
}
