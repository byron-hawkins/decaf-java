package org.hs.util.file;

public class Options
{
	/**
	 * option parameter: do not overwrite files, and exclude subdirectories 
	 */
	public static final int DEFAULT = 0;
	/**
	 * option parameter: include subdirectories in this operation.  See method detail for applicability.
	 */
	public static final int INCLUDE_SUBDIRS = 1;
	/**
	 * option parameter: if a file is being moved or copied, and the destination already exists, overwrite it.
	 * See method detail for applicability.
	 */
	public static final int OVERWRITE = 2;

	private int mOptions;

	public Options(int options)
	{
		mOptions = options;
	}

	public boolean recurse()
	{
		return ((mOptions & INCLUDE_SUBDIRS) != 0);
	}

	public boolean overwrite()
	{
		return ((mOptions & OVERWRITE) != 0);
	}

	public String describe()
	{
		StringBuffer buffer = new StringBuffer();
		if (recurse())
		{
			buffer.append("recursive");
		}
		if (overwrite())
		{
			if (recurse())
			{
				buffer.append(" and ");
			}
			buffer.append("overwrite");
		}
		return buffer.toString();
	}
}
