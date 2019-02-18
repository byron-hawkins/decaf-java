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
 * A try/catch/finally statement.
 */
public class GTry extends GConstruct implements GStatement
{
	static final long serialVersionUID = -5765318712904846146L;

	/**
	 * What to try.
	 */
	public GBlock tryBody;
	
	/**
	 * A collection of GCatch.
	 */
	public Vector catches;
	
	/**
	 * A block to execute unconditionally upon exit of this try/catch/finally.
	 * (er, the finally block).  
	 */
	public GBlock finallyBody;
	
	public GTry()
	{
		super("???");
		
		init();
	}
	
	GTry(JStatement statement)
	{
		super(statement);
		init();
		statement.accept(new Decoder());
	}
	
	private void init()
	{
		this.catches = new Vector();
		this.tryBody = new GBlock();
		this.finallyBody = new GBlock();
	}		

	public JStatement encodeStatement()
	{
		JStatement encoded = null;
		
		if (!catches.isEmpty())
		{
			encoded = new JTryCatchStatement(super.tokenReference,
											 this.tryBody.encodeBlock(),
											 encodeCatches(),
											 super.encodeComments());
		}
		
		if (!finallyBody.isEmpty())
		{
			JBlock tryFinallyBody;
			
			if (catches.isEmpty())
			{
				tryFinallyBody = this.tryBody.encodeBlock();
			}
			else
			{
				tryFinallyBody = new JBlock(super.tokenReference,
											new JStatement[] { encoded },
											super.encodeComments());
			}
			
			encoded = new JTryFinallyStatement(super.tokenReference,
											   tryFinallyBody,
											   this.finallyBody.encodeBlock(),
											   super.encodeComments());
		}
		
		return encoded;
	}
	
	protected JCatchClause[] encodeCatches()
	{
		JCatchClause[] encoded = new JCatchClause[this.catches.size()];
		for (int i = 0; i < this.catches.size(); i++)
		{
			encoded[i] = ((GCatch)catches.elementAt(i)).encode();
		}
		
		return encoded;
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a try-catch statement
		 */
		public void visitTryCatchStatement(JTryCatchStatement self,
										   JBlock tryClause,
										   JCatchClause[] catchClauses)
		{
			GTry.this.tryBody = new GBlock(tryClause);
			for (int i = 0; i < catchClauses.length; i++)
			{
				GTry.this.catches.add(new GCatch(catchClauses[i]));
			}
		}

		/**
		 * visits a try-finally statement
		 */
		public void visitTryFinallyStatement(JTryFinallyStatement self,
											 JBlock tryClause,
											 JBlock finallyClause)
		{
			GTry.this.tryBody = new GBlock(tryClause);
			GTry.this.finallyBody = new GBlock(finallyClause);
		}
	}	
	
	public String toString()
	{
		return "body(" + this.tryBody + ") catching(" + this.catches + ") finally(" + this.finallyBody + ")";
	}
}
