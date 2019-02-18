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

import at.dms.compiler.*;
import at.dms.kjc.*;

import java.io.*;
import java.util.*;

/**
 * A GBlock represents {a block of code in braces}, separate from those blocks
 * that are intrinsically attached to a loop or switch construct.
 */
public class GBlock extends GConstruct implements GStatement
{
	static final long serialVersionUID = -5181084520145267877L;

	/**
	 *	Contains a collection of GStatement, and is initialized
	 * to an empty java.util.Vector.
	 */
	public Vector statements;	
	
	/**
	 *	If this GBlock is the body of a GMethod, then this field represents an 
	 * initial constructor call statement, i.e., this(...) or super(...).
	 */
	public GMethodCall constructorCall;
	
	/**
	 * Vanilla constructor, initializes <code>statements</code> to an empty java.util.Vector.
	 */
	public GBlock()
	{
		super("???");
		init();
	}
	
	GBlock(JStatement statement)
	{
		super(statement);
		init();
		statement.accept(new Decoder());
	}
	
	private void init()
	{
		this.statements = new Vector();
		this.constructorCall = null;
	}
	
	public boolean isEmpty()
	{
		return this.statements.isEmpty();
	}
	
	public void add(GStatement statement)
	{
		if (statement instanceof GVariable)
		{
			((GVariable)statement).isLocal = true;
		}
		this.statements.add(statement);
	}
	
	public void add(int index, GStatement statement)
	{
		if (statement instanceof GVariable)
		{
			((GVariable)statement).isLocal = true;
		}
		this.statements.add(index, statement);
	}
	
	public void append(GBlock block)
	{
		this.statements.addAll(block.statements);
	}

	public void printBody(Writer writer)
	{
  		TabbedPrintWriter tabbed = new TabbedPrintWriter(new BufferedWriter(writer));
		KjcPrettyPrinter printer = new KjcPrettyPrinter(tabbed, GSourceFile.s_typeFactory);
		
		JStatement[] body = encodeStatements();
		for (int i = 0; i < body.length; i++)
		{
			body[i].accept(printer);
		}
	}

	public JStatement encodeStatement()
	{
		return (new JBlock(super.tokenReference,
						   encodeStatements(),
						   super.encodeComments()));
	}
	
	// also used as a hack for JConstructoryBody (which is a hack for this(...))
	public JStatement[] encodeStatements()
	{
		JStatement[] encoded = new JStatement[this.statements.size()];
		for (int i = 0; i < this.statements.size(); i++)
		{
			encoded[i] = ((GStatement)this.statements.elementAt(i)).encodeStatement();
		}
		return encoded;
	}
	
	public JBlock encodeBlock()
	{
		return (JBlock)encodeStatement();
	}
	
	public JConstructorCall encodeConstructorCall()
	{
		return (JConstructorCall)((this.constructorCall == null)?null:this.constructorCall.encodeExpression());
	}
	
	class Decoder extends GConstructDecoder
	{
		/**
		 * visits an expression statement
		 */
		public void visitBlockStatement(JBlock self,
										JStatement[] body,
										JavaStyleComment[] comments)
		{
			for (int i = 0; i < body.length; i++)
			{
				GBlock.this.statements.add(GDecoder.decode(body[i]));
			}
		}

		/**
		 * visits a constructor body
		 */
		public void visitConstructorBlockStatement(JBlock self,
												   JExpression constructorCall,
												   JStatement[] body,
												   JavaStyleComment[] comments)
		{
			for (int i = 0; i < body.length; i++)
			{
				GBlock.this.statements.add(GDecoder.decode(body[i]));
			}
			
			if (constructorCall == null)
			{
				GBlock.this.constructorCall = null;
			}
			else
			{
				GBlock.this.constructorCall = new GMethodCall(constructorCall);
			}
		}
	}

	public String toString()
	{
		return "statements(" + this.statements + ")";
	}
}
