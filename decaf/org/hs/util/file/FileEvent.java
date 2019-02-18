package org.hs.util.file;

import java.io.*;

public class FileEvent
{
	public static final int DELETE = 0;
	public static final int FILE_COPIED = 1;
	public static final int FILE_COPIED_INTO = 2;
	public static final int WRITE = 3;
	public static final int MKDIR = 4;
	public static final int MKDIRS = 5;
	public static final int RENAME = 6;

	private int event;
	private SuperFile source;
	private Options options;
	private File associated;
	private String data;

	public FileEvent(int event, SuperFile source)
	{
		this.event = event;
		this.source = source;
		this.options = null;
		this.associated = null;
		this.data = null;
	}

	public FileEvent(int event, SuperFile source,  Options options)
	{
		this.event = event;
		this.source = source;
		this.options = options;
		this.associated = null;
		this.data = null;
	}

	public FileEvent(int event, SuperFile source,  File associated, Options options)
	{
		this.event = event;
		this.source = source;
		this.options = options;
		this.associated = associated;
		this.data = null;
	}

	public FileEvent(int event, SuperFile source,  File associated)
	{
		this.event = event;
		this.source = source;
		this.associated = associated;
		this.options = null;
		this.data = null;
	}

	public FileEvent(int event, SuperFile source,  String data)
	{
		this.event = event;
		this.source = source;
		this.data = data;
		this.associated = null;
		this.options = null;
	}

	public int getEvent()
	{
		return this.event;
	}

	public SuperFile getSource()
	{
		return this.source;
	}

	public File getAssociatedFile()
	{
		return this.associated;
	}

	public Options getOptions()
	{
		return this.options;
	}

	public String describeEvent()
	{
		switch (this.event)
		{
			case DELETE:
				return "delete";
			case FILE_COPIED:
				return "file copied";
			case FILE_COPIED_INTO:
				return "file copied into";
			case WRITE:
				return "write";
			case MKDIR:
				return "mkdir";
			case MKDIRS:
				return "mkdirs";
			case RENAME:
				return "rename";
		}
		return "";
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getName());
		buffer.append(": ");
		buffer.append(this.describeEvent());
		buffer.append("\n\tevent source: ");
		buffer.append(this.source.getPath());
		if (this.associated != null)
		{
			buffer.append("\n\tassociated file: ");
			buffer.append(this.associated.getPath());
		}
		if (this.options != null)
		{
			buffer.append("\n\toptions: ");
			buffer.append(this.options.describe());
		}
		if (this.data != null)
		{
			buffer.append("\n\tdata: ");
			buffer.append(this.data);
		}
		return buffer.toString();
	}
}

