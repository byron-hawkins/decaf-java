package org.hs.util.file;

import java.util.*;

public class LineTokenizer 
{
	private Vector lines;
	private String tokensWithinLine;
	
	private transient Enumeration index;
	private transient StringTokenizer nextLine;

	public LineTokenizer(String content)
	{
		this.tokensWithinLine = null;
		init(content);
	}

	public LineTokenizer(String content, String tokensWithinLine)
	{
		this.tokensWithinLine = tokensWithinLine;
		init(content);
	}

	private void init(String content)
	{
		this.lines = new Vector();
		StringTokenizer lineTokens = new StringTokenizer(content, "\n");
		
		while (lineTokens.hasMoreTokens())
		{
			lines.addElement(lineTokens.nextToken());
		}
		rewind();
	}

	public boolean nextLine()
	{
		if (!this.index.hasMoreElements())
		{
			return false;
		}
		this.nextLine = getTokenizer((String)this.index.nextElement());
		return true;
	}

	public String nextToken()
	{
		if (this.nextLine.hasMoreTokens())
		{
			return this.nextLine.nextToken();
		}
		return null;
	}

	public void rewind()
	{
		this.index = this.lines.elements();
	}

	private StringTokenizer getTokenizer(String content)
	{
		if (this.tokensWithinLine == null)
		{
			return (new StringTokenizer(content));
		}
		else
		{
			return (new StringTokenizer(content, this.tokensWithinLine));
		}
	}
}
