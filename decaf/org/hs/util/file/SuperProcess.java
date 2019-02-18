package org.hs.util.file;

import java.io.*;
import java.util.*;

public class SuperProcess 
{
	private Process containedProcess;

	private transient String inputStreamContent;
	private transient String errorStreamContent;

	public SuperProcess(Process inProcess)
	{
		this.containedProcess = inProcess;
		inputStreamContent = null;
		errorStreamContent = null;
	}

	public LineTokenizer getTokenizedInput()
		throws IOException, InterruptedException
	{
		return (new LineTokenizer(getInput()));
	}

	public StringTokenizer getInputTokens()
		throws IOException, InterruptedException
	{
		return (new StringTokenizer(getInput()));
	}

	public StringTokenizer getInputTokens(String tokens)
		throws IOException, InterruptedException
	{
		return (new StringTokenizer(getInput(), tokens));
	}

	public StringTokenizer getInputTokens(String tokens, boolean someDamnedThing)
		throws IOException, InterruptedException
	{
		return (new StringTokenizer(getInput(), tokens, someDamnedThing));
	}

	public Process getProcess()
	{
		return this.containedProcess;
	}

	public int exitValue()
		throws InterruptedException
	{
		this.containedProcess.waitFor();
		return this.containedProcess.exitValue();
	}

	public String getErrorInput()
		throws IOException, InterruptedException
	{
		if (errorStreamContent != null)
		{
			return errorStreamContent;
		}

		if (this.containedProcess == null)
		{
			return (new String());
		}

		this.containedProcess.waitFor();
		
		InputStream in = this.containedProcess.getErrorStream();
		errorStreamContent = readInput(in);

		return errorStreamContent;
	}

	public String getInput()
		throws IOException, InterruptedException
	{
		if (inputStreamContent != null)
		{
			return inputStreamContent;
		}

		if (this.containedProcess == null)
		{
			return (new String());
		}

		this.containedProcess.waitFor();
		
		InputStream in = this.containedProcess.getInputStream();
		inputStreamContent = readInput(in);

		return inputStreamContent;
	}

	private String readInput(InputStream in)
		throws IOException
	{
		byte[] data;
		int available;
		while (true)
		{
			available = in.available();
			if (available == 0)
			{
				break;
			}
			data = new byte[available];
			in.read(data);
			return (new String(data));
		}
		return (new String());
	}
}
