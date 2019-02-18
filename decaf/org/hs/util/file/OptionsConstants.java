package org.hs.util.file;

public interface OptionsConstants
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
}
