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
import at.dms.compiler.*;

import java.util.*;

/**
 * A class initializer block, which may be static.  
 */
public class GClassInit extends GConstruct implements GStatement
{
	static final long serialVersionUID = -2709470837019556726L;

	/**
	 * Determines whether the initializer is static.
	 */
	public boolean isStatic;
	
	/**
	 * The statements of the initializer.
	 */
	public Vector body;
	
	/**
	 * Default no-arg constructor, assumes the block is static.
	 */
	public GClassInit()
	{
		this(true);
	}
	
	/**
	 * Convenience constructor.
	 */
	public GClassInit(boolean isStatic)
	{
		super("???");
		init();
		this.isStatic = isStatic;
	}
	
	GClassInit(JClassBlock declaration)
	{
		super(declaration);
		init();
		
		this.isStatic = declaration.isStaticInitializer();
		declaration.accept(new Decoder());
	}
	
	private void init()
	{
		this.body = new Vector();
	}
	
	public JStatement encodeStatement()
	{
		return (new JClassBlock(super.tokenReference,
								this.isStatic,
								encodeBody()));
	}

	protected JStatement[] encodeBody()
	{
		JStatement[] encoded = new JStatement[this.body.size()];
		for (int i = 0; i < this.body.size(); i++)
		{
			encoded[i] = ((GStatement)this.body.elementAt(i)).encodeStatement();
		}
		return encoded;
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
				GClassInit.this.body.add(GDecoder.decode(body[i]));
			}
		}
	}

	public String toString()
	{
		return "body(" + this.body + ") static?(" + this.isStatic + ")";
	}
}

