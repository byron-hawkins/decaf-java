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
 
package com.bitwise.decaf.editor;

import java.io.*;
import java.util.*;

import org.hs.util.file.*;
import org.hs.generator.*;

public class CupFile extends SuperFile
{
	protected GSourceFile sourceFile;
	protected Object typeRoot;
	protected Object bluesReview;
	protected Object percolatorBrew;
	protected boolean valid;
	
	public CupFile(String filename)
	{
		super(filename);
		
		this.sourceFile = null;
		this.typeRoot = null;
	}
	
	public CupFile(String filename, GSourceFile sourceFile, Object bluesReview, Object percolatorBrew)
	{
		super(filename);
		
		this.sourceFile = sourceFile;
		this.typeRoot = typeRoot;
		this.bluesReview = bluesReview;
		this.percolatorBrew = percolatorBrew;
		
		this.valid = true;
	}

	public GSourceFile getSourceFile()
	{
		return this.sourceFile;
	}
	
	public boolean isValid()
	{
		return this.valid;
	}
	
	public Object getTypeRoot()
	{
		return this.typeRoot;
	}
	
	public Object getBluesReview()
	{
		return this.bluesReview;
	}
	
	public Object getPercolatorBrew()
	{
		return this.percolatorBrew;
	}
	
	public void open()
		throws IOException, FileNotFoundException, DirtyCupException
	{
		try
		{
			ObjectInputStream in = new ObjectInputStream(super.getInputStream());
			this.sourceFile = (GSourceFile)in.readObject();
			//this.typeRoot = in.readObject();
			this.bluesReview = in.readObject();
			this.percolatorBrew = in.readObject();
			in.close();
		}
		catch (ClassNotFoundException notPossible)
		{
		}
		catch (InvalidClassException e)
		{
			DecafEditor.log(e);
			new DecafEditor.Alert("File can't be opened", "The file " + super.getName() + " cannot be opened because it was written by a version of Decaf that is incompatible with the current version.  This problem will be fixed in a future release of Decaf, but for now I must simply apologize for your inconvenience.");
			this.valid = false;
			throw (new DirtyCupException());
		}
		
		this.sourceFile.applyDefaultDirectory();
	}
	
	public void save()
		throws IOException, FileNotFoundException
	{
		ObjectOutputStream out = new ObjectOutputStream(super.getOutputStream());
		out.writeObject(this.sourceFile);
		//out.writeObject(this.typeRoot);
		out.writeObject(this.bluesReview);
		out.writeObject(this.percolatorBrew);
		out.flush();
		out.close();
	}
	
	public static class DirtyCupException extends Exception
	{
	}
}
