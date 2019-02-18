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
 * This class represents a <code>for</code> loop such as:
 * <p><code>
 * for (int i = 0; i < something.size(); i++)<br>
 * {<br>
 *     something.do(i);<br>
 * }<br>
 * </code><p>
 * It contains the expected <code>init</code> statement ('int i = 0' in the example), 
 * <code>condition</code> expression ('i < something.size()'), <code>increment</code> 
 * expressions ('i++') and <code>body</code> ('{ something.do(i); }'), each of which may 
 * be null (init and conditon) or empty (increment and body).  The <code>init</code> may
 * be compound, but I'm not just sure how at the moment, since I haven't tried it.
 */
public class GFor extends GLoop implements GStatement
{
	static final long serialVersionUID = -456752993805303054L;

	/**
	 * The for loop initializer, such as 'int i = 0' in the example above.  
	 * May be null.  There must be some way to make this compound, but I'm 
	 * not sure what that would be since I haven't tried it.
	 */
	public GStatement init;
	
	/**
	 * The for loop condition, such as 'i < something.size()' in the example above.
	 * May be null; if not, it must evaluate to a primitive boolean.
	 */
	public GExpression condition;
	
	/**
	 * A collection of GExpression, all appearing in the role of the line 
	 * 'i++' in the example above.  May be empty.
	 */
	public Vector increment;
	
	/**
	 * The body of the for loop.
	 */
	public GBlock body;

	/**
	 * Convenience constructor.
	 */	
	public GFor(GStatement init, GExpression condition, GExpression increment)
	{
		super("???");
		init();
		
		this.init = init;
		this.condition = condition;
		if (increment != null)
		{
			this.increment.add(increment);
		}
		
		if (this.init instanceof GVariable)
		{
			((GVariable)this.init).isLocal = true;
		}
	}
	
	GFor(JStatement statement)
	{
		this(statement, null);
	}
	
	GFor(JStatement statement, String label)
	{
		super(statement);
		init();
		super.label = label;
		statement.accept(new Decoder());
	}
	
	private void init()
	{
		this.increment = new Vector();
		this.body = new GBlock();
	}
	
	public JStatement encodeStatement()
	{
		return super.encodeStatement(new JForStatement(super.tokenReference,
								  					   this.init.encodeStatement(),
								  					   this.condition.encodeExpression(),
								  					   encodeIncrement(),
								  					   this.body.encodeBlock(),
								  					   super.encodeComments()));
	}
	
	protected JExpressionListStatement encodeIncrement()
	{
		if (this.increment.isEmpty())
		{
			return null;
		}
		
		JExpression[] encoded = new JExpression[this.increment.size()];
		for (int i = 0; i < this.increment.size(); i++)
		{
			encoded[i] = ((GExpression)this.increment.elementAt(i)).encodeExpression();
		}
		return (new JExpressionListStatement(super.tokenReference,
											 encoded,
											 new at.dms.compiler.JavaStyleComment[0]));
	}
	
	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a for statement
		 */
		public void visitForStatement(JForStatement self,
									  JStatement init,
									  JExpression cond,
									  JStatement incr,
									  JStatement body)
		{
			GFor.this.init = GDecoder.decode(init);
			GFor.this.condition = GDecoder.decode(cond);
			incr.accept(this);
			GFor.this.body = new GBlock(body);
		}
		
		/**
		 * visits an expression list statement
		 */
		public void visitExpressionListStatement(JExpressionListStatement self,
												 JExpression[] expr)
		{
			for (int i = 0; i < expr.length; i++)
			{
				GFor.this.increment.add(GDecoder.decode(expr[i]));
			}
		}
	}

	public String toString()
	{
		return "init(" + this.init + ") condition(" + this.condition + ") increment(" + this.increment + ") body(" + this.body + ") " + super.toString();
	}
}
