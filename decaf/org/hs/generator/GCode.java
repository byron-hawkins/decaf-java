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

import java.util.*;

/**
 * This class is kind of a cluge; it allows a block of text to be 
 * included in the GConstruct tree as a statement of Java code.  It 
 * is only designed for use in generating source code, and is 
 * compiled into void.  
 */
public class GCode extends GConstruct implements GStatement
{
	static final long serialVersionUID = -2709470837019556726L;

	protected String code;
	
	public GCode(String code)
	{
		super("???");
		
		this.code = code;
	}
	
	GCode(JStatement statement)
	{
		super(statement);
		statement.accept(new Decoder());
	}
	
	public JStatement encodeStatement()
	{
		return (new JCode(super.tokenReference,
						  this.code,
						  super.encodeComments()));
	}
	
	public String getCode()
	{
		return this.code;
	}

	public String toString()
	{
		return "code(" + this.code+ ")";
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a code statement
		 */
		public void visitCodeStatement(JCode self, 
									   String code,
									   JavaStyleComment[] comments)
		{
			GCode.this.code = code;
		}
	}
}
