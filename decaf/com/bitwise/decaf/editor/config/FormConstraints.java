/*
 * Copyright (C) 2003 HawkinsSoftware
 *
 * This prototype of the Decaf Java development environment is free 
 * software.  You can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software 
 * Foundation.  However, no compilation of this code or a derivative
 * of it may be used with or integrated into any commercial application,
 * except by the written permisson of HawkinsSoftware.  Future versions 
 * of this product will be sold commercially under a different license.  
 * HawkinsSoftware retains all rights to this product, including its
 * concepts, design and implementation.
 *
 * This prototype is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
 
package com.bitwise.decaf.editor.config;

import javax.xml.bind.Dispatcher;
import javax.xml.bind.UnmarshalException;
import javax.xml.marshal.XMLScanner;

import org.hs.jfc.*;

public class FormConstraints extends FormLocation
{
	public int row;
	public int column;
	public int mode;
	public double fillRightPct;
	
	public FormConstraints()
	{
		super();
	}
	
	public FormConstraints(int row, int column)
	{
		this(row, column, FormLayout.DEFAULT);
	}
	
	public FormConstraints(int row, int column, int mode)
	{
		super();

		this.row = row;
		this.column = column;
		this.mode = mode;
	}

    public static FormLocation unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
    	FormConstraints constraints = (FormConstraints) d.unmarshal(xs, (FormConstraints.class));
    	constraints.init();
        return constraints;
    }

    public static Dispatcher newDispatcher() 
    {
        return ClassConfig.newDispatcher();
    }
    
    public void init()
    {
        this.row = Integer.parseInt(super.getRow());
        this.column = Integer.parseInt(super.getColumn());
        
        String _mode = super.getMode();
        
        if (_mode == null)
        {
        	this.mode = FormLayout.DEFAULT;
        }
        else
        {
        	if (_mode.equals("DEFAULT"))
        	{
        		this.mode = FormLayout.DEFAULT;
        	}
        	else if (_mode.equals("LABEL_ON_TOP"))
        	{
        		this.mode = FormLayout.LABEL_ON_TOP;
        	}
        	else if (_mode.equals("FREE_FIELD"))
        	{
        		this.mode = FormLayout.FREE_FIELD;
        	}
			else if (_mode.equals("FREE_LABEL"))
        	{
        		this.mode = FormLayout.FREE_LABEL;
        	}
        }
        
        String _fillRightPct = super.getFillRightPct();
        if (_fillRightPct == null)
        {
        	this.fillRightPct = 0.0;
        }
        else
        {
        	this.fillRightPct = Double.parseDouble(_fillRightPct);
        }
    }
}
