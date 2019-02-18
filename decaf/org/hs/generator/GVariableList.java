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
 * A list of GVariables all declared in a single statement.  They must
 * all be of the same type, though this is not enforced by the construct
 * chosen by KJC (passing the buck again!).  I don't like this concept to 
 * begin with.  Why even use it?  I don't.
 */
public class GVariableList extends GConstruct implements GStatement
{
	static final long serialVersionUID = 7535202275050272963L;

	/**
	 * A collection of GVariable to declare in this single statement.
	 */
	public Vector variables;	// GVariable 
	
	public GVariableList()
	{
		super("???");
		init();
	}
	
	GVariableList(JStatement statement)
	{
		super(statement);
		init();
		statement.accept(new Decoder());
	}
	
	public JStatement encodeStatement()
	{
		return (new JVariableDeclarationStatement(super.tokenReference,
												  encodeVariables(),
												  super.encodeComments()));
	}
  
  	protected JVariableDefinition[] encodeVariables()
	{
		JVariableDefinition[] encoded = new JVariableDefinition[this.variables.size()];
		for (int i = 0; i < this.variables.size(); i++)
		{
			encoded[i] = ((GVariable)this.variables.elementAt(i)).encodeVariable();
		}
		return encoded;
	}
	
	private void init()
	{
		this.variables = new Vector();
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a variable declaration statement
		 */
		public void visitVariableDeclarationStatement(JVariableDeclarationStatement self,
													  JVariableDefinition[] vars)
		{
			for (int i = 0; i < vars.length; i++)
			{
				GVariableList.this.variables.add(new GVariable(vars[i]));
			}
		}
	}
	
	public String toString()
	{
		return "variables(" + this.variables + ")";
	}
}
