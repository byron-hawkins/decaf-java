package org.hs.util.file;

import java.io.*;
import java.util.*;
import gnu.regexp.*;

/**
 * A java.io.File with more features.  
 *
 * Note: methods will only abort on exception; if an operation expects to return
 * false, it will continue performing as many successful operations as it can.
 */
public class SuperFile extends File implements Serializable, OptionsConstants
{
	public static SuperFile[] getClasspath(String[] paths)
	{
		SuperFile[] classpath = new SuperFile[paths.length];
		for (int i = 0; i < paths.length; i++)
		{
			classpath[i] = new SuperFile(paths[i]);
		}
		
		return classpath;
	}
	
	private Vector listeners;
	protected RandomAccessFile access;
	protected Object writeLock = new Object();
	protected String baseName;
	protected String extension;
	protected String absolutePath;

	public SuperFile(String name)
	{
		super(name);
		init(name);
	}

	public SuperFile(File content)
	{
		super(content.getAbsolutePath());
		init(super.getName());
	}

	public SuperFile(File dir, String name)
	{
		super(dir, name);
		init(name);
	}
	
	private void init(String name)
	{
		this.listeners = new Vector();
		this.access = null;

		int dot = name.lastIndexOf(".");
		if (dot < 0)
		{
			this.baseName = name;
			this.extension = "";
		}
		else
		{
			this.baseName = name.substring(0, dot);
			this.extension = name.substring(dot+1, name.length());
		}
		
		this.absolutePath = super.getAbsolutePath();
		
		int dotSlash;
		while (true)
		{
			dotSlash = this.absolutePath.indexOf("." + File.separator);
			if (dotSlash < 0)
			{	
				break;
			}
			
			this.absolutePath = this.absolutePath.substring(0, dotSlash) + this.absolutePath.substring(dotSlash+2);
		}
		
		if (this.absolutePath.endsWith("."))
		{
			this.absolutePath = this.absolutePath.substring(0, this.absolutePath.length()-1);
		}
	}
	
	protected void initAccess()
		throws IOException
	{
		if (this.access == null)
		{
			this.access = new RandomAccessFile(this, "rw");
		}
	}
	
	public void unlock()
		throws IOException
	{
		synchronized (writeLock)
		{
			if (this.access != null)
			{
				this.access.close();
				this.access = null;
			}
		}
	}

	/**
	 * Add a listener for file/directory activity on this SuperFile
	 */
	public void addFileListener(FileListener listener)
	{
		this.listeners.addElement(listener);
	}

	/**
	 * Remove a file/directory listener.
	 */
	public void removeFileListener(FileListener listener)
	{
		this.listeners.removeElement(listener);
	}
	
	public String getBaseName()
	{
		return this.baseName;
	}
	
	public String getExtension()
	{
		return this.extension;
	}
	
	public String getAbsolutePath()
	{
		return this.absolutePath;
	}
	
	/**
	 * delete this directory
	 *
	 * @param options accepts Options.INCLUDE_SUBDIRS
	 * @return true iff the directory no longer exists
	 */
	public boolean deleteDirectory(int options)
	{
		return deleteDirectory(new Options(options));
	}
	public boolean deleteDirectory()
	{
		return deleteDirectory(new Options(Options.DEFAULT));
	}
	private boolean deleteDirectory(Options options)
	{
		if (!isDirectory())
		{
			return false;
		}

		boolean result = true;

		if (exists())
		{
			if (emptyDirectory(options))
			{
				result = delete();
			}
			result = false;
		}

		if (result)
		{
			notifyListeners(new FileEvent(FileEvent.DELETE, this, options));
		}
		return result;
	}

	/**
	 * Copy the contents of this into toDir
	 *
	 * @param options accepts Options.INCLUDE_SUBDIRS and Options.OVERWRITE
	 * @return true iff all the files that could have been copied, 
	 * per the options, were successfully copied (e.g., returns false
	 * if Options.OVERWRITE is not set and at least one of the files within 
	 * this dir exists in toDir).
	 */
	public boolean copyContentsInto(File toDir, int options)
		throws FileNotFoundException, IOException
	{
		return copyContentsInto(toDir, new Options(options));
	}
	public boolean copyContentsInto(File toDir)
		throws FileNotFoundException, IOException
	{
		return copyContentsInto(toDir, new Options(Options.DEFAULT));
	}
	private boolean copyContentsInto(File toDir, Options options)
		throws FileNotFoundException, IOException
	{
		if (!isDirectory())
		{
			return false;
		}

		String[] contents = list();
		SuperFile next;
		boolean copySuccess = true;

		for (int i = 0; i < contents.length; i++)
		{
			next = new SuperFile(this, contents[i]);
			if (next.isDirectory())
			{
				if (options.recurse())
				{
					//System.err.println("copyContentsInto(): copying dir: " + contents[i]);
					// what does File.getName() return when (File.isDirectory() == true)?
					SuperFile newDirectory = new SuperFile(toDir, next.getName());
					if (!newDirectory.exists())
					{
						newDirectory.mkdir();
					}
					copySuccess = copySuccess && next.copyContentsInto(newDirectory, options);
				}
				else
				{
					//System.err.println("copyContentsInto(): ignoring dir: " + contents[i]);
				}
			}
			else
			{
				copySuccess = copySuccess && next.copyInto(toDir, options);
				//System.err.println("copyContentsInto(): copied file: " + contents[i] + " ? " + copySuccess);
			}
		}

		return copySuccess;
	}

	/**
	 * Copy this to toDir
	 * 
	 * @param toDir must be a directory (or return false)
	 * @param options accepts Options.OVERWRITE iff this.isFile(); 
	 * accepts Options.INCLUDE_SUBDIRS iff this.isDirectory()
	 * @return true iff this is completely copied (per the options) to toDir
	 */
	public boolean copyInto(File toDir, int options)
		throws FileNotFoundException, IOException
	{
		return copyInto(toDir, new Options(options));
	}
	public boolean copyInto(File toDir)
		throws FileNotFoundException, IOException
	{
		return copyInto(toDir, new Options(Options.DEFAULT));
	}
	private boolean copyInto(File toDir, Options options)
		throws FileNotFoundException, IOException
	{
		if (!toDir.isDirectory())
		{
			return false;
		}

		if (isFile())
		{
			SuperFile newFile = new SuperFile(toDir, getName());
			if (newFile.exists() && !options.overwrite())
			{
				return false;
			}

			BufferedReader in = new BufferedReader(new FileReader(this));
			BufferedWriter out = new BufferedWriter(new FileWriter(newFile));

			String line = in.readLine();
			while (line != null)
			{
				out.write(line);
				out.newLine();
				line = in.readLine();
			}
			out.flush();
			out.close();
			in.close();
		}
		else
		{
			SuperFile newDir = new SuperFile(toDir, getName());
			if (!newDir.exists())
			{
				newDir.mkdir();
			}
			if (options.recurse())
			{
				return copyContentsInto(newDir, options);
			}
		}

		notifyListeners(new FileEvent(FileEvent.FILE_COPIED, this, toDir, options));
		if (toDir instanceof SuperFile)
		{
			((SuperFile)toDir).notifyListeners(new FileEvent(FileEvent.FILE_COPIED_INTO, ((SuperFile)toDir), this, options));
		}
		return true;
	}

	/**
	 * Delete the contents of this directory
	 * 
	 * @param options accepts Options.INCLUDE_SUBDIRS
	 * @return true iff this directory exists and is empty after 
	 * the operation completes (i.e., will return false if subdirectories 
	 * exist and the Options.INCLUDE_SUBDIRS option is not specified; also 
	 * will return false if this directory doesn't exist to begin with)
	 */
	public boolean emptyDirectory(int options)
	{
		return emptyDirectory(new Options(options));
	}
	public boolean emptyDirectory()
	{
		return emptyDirectory(new Options(Options.DEFAULT));
	}
	private boolean emptyDirectory(Options options)
	{
		if (!isDirectory())
		{
			return false;
		}

		if (!exists())
		{
			return false;
		}

		boolean wasEmptied = true;
		String[] contents = list();
		SuperFile next;

		for (int i = 0; i < contents.length; i++)
		{
			next = new SuperFile(this, contents[i]);
			if (next.isDirectory() && options.recurse())
			{
				wasEmptied = wasEmptied && next.emptyDirectory(options);
			}
			wasEmptied = wasEmptied && next.delete();
		}
		return wasEmptied;
	}

	/**
	 * Write (param) contents to the file, replacing 
	 * existing contents if the file exists.
	 */
	public void write(String contents)
		throws IOException
	{
		synchronized (writeLock)
		{
			initAccess();
			this.access.setLength(0);
			this.access.seek(0);
			this.access.writeBytes(contents);
		}
		
		notifyListeners(new FileEvent(FileEvent.WRITE, this, contents));
	}

	/**
	 * Write (param) contents to the file, followed by a newline, replacing 
	 * existing contents if the file exists.
	 */
	public void writeln(String contents)
		throws IOException
	{
		synchronized (writeLock)
		{
			initAccess();
			this.access.setLength(0);
			this.access.seek(0);
			this.access.writeBytes(contents);
			this.access.writeBytes("\n");
		}
		
		notifyListeners(new FileEvent(FileEvent.WRITE, this, contents));
	}

	/**
	 * Prepend the file with (param) contents.
	 */
	public void prepend(String contents)
		throws IOException
	{
		synchronized (writeLock)
		{
			initAccess();
			this.access.seek(0);
			this.access.writeBytes(contents);
		}
	}

	/**
	 * Append the file with (param) contents.
	 */
	public void append(String contents)
		throws IOException
	{
		synchronized (writeLock)
		{
			initAccess();
			this.access.seek(this.access.length());
			this.access.writeBytes(contents);
		}
	}
	
	/**
	 * Append the file with (param) contents, followed by a newline.
	 */
	public void appendln(String contents)
		throws IOException
	{
		synchronized (writeLock)
		{
			initAccess();
			this.access.seek(this.access.length());
			this.access.writeBytes(contents);
			this.access.writeBytes("\n");
		}
	}
	
	public void entry(String contents)
	{
		try
		{
			append(contents + "\n");
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
		}
	}

	public void entry(Throwable t)
	{
		try
		{
			PrintStream out = getPrintStream();
			out.println(t.getMessage());
			t.printStackTrace(out);
		}
		catch (FileNotFoundException itsTooMuch)
		{
			itsTooMuch.printStackTrace(System.err);
		}
	}

	/**
	 * Extend this directory with the (param) fragment, which can 
	 * specify either a file or a directory.  
	 */
	public SuperFile extend(String fragmentName)
	{
		return extend(new File(fragmentName));
	}
	
	/**
	 * Extend this directory with the (param) fragment, which can 
	 * specify either a file or a directory.  
	 */
	public SuperFile extend(File fragment)
	{
		if (isFile())
		{
			return null;
		}
		
		if (fragment.getName().equals(".") || fragment.getName().equals("./") || fragment.getName().equals(".\\"))
		{
			return this;
		}

		if (this.getName().equals(".") || this.getName().equals("./") || this.getName().equals(".\\"))
		{
			return (new SuperFile(fragment.getPath()));
		}

		SuperFile extension;
		if (fragment.isDirectory())
		{
			extension = new SuperFile(getPath() + "/" + fragment.getPath());
		}
		else
		{
			if (fragment.getPath().length() > (fragment.getName().length() + 1))
			{
				// the fragment is a local path and filename
				extension = new SuperFile(new File(getPath() + "/" + fragment.getParent()), fragment.getName());
			}
			else
			{
				// the fragment is just a filename
				extension = new SuperFile(this, fragment.getName());
			}
		}
		
		return extension;
	}

	/** 
	 * If (param) root is at some level a parent of this SuperFile,
	 * return a SuperFile that does not specify the root.
	 */
	public SuperFile localTo(File root)
	{
		if (!root.isDirectory())
		{
			return null;
		}

		if (getDirectory() == null)
		{
			return null;
		}

		if (getDirectory().getPath().indexOf(root.getPath()) != 0)
		{
			return null;
		}

		return (new SuperFile(getPath().substring(root.getPath().length() + 1, getPath().length())));
	}

	/**
	 * Get the contents of the file.
	 */
	public String read()
		throws IOException
	{
		FileInputStream in = new FileInputStream(this);
		int available = in.available();
		byte[] contents = new byte[available];
		in.read(contents);
		in.close();
		return (new String(contents));
	}

	/**
	 * Similar to File.list(), but accepts a gnu regular expression, 
	 * as defined in gnu.regexp.RESyntax.RE_SYNTAX_PERL5, and
	 * returns an array of SuperFile.
	 */
	public SuperFile[] list(String filter)
		throws IOException
	{
		String[] list = super.list(new WildcardFilter(filter));
		if (list == null)
		{
			return null;
		}
		SuperFile[] files = new SuperFile[list.length];
		for (int i = 0; i < list.length; i++)
		{
			files[i] = new SuperFile(this, list[i]);
		}
		return files;
	}

	/**
	 * Same as list(String), but includes all directories,
	 * regardless of their agreement with the filter
	 */
	public SuperFile[] listWithDirs(String fileFilter)
		throws IOException
	{
		SuperFile[] files = list(fileFilter);
		SuperFile[] dirs = list(".*");

		if ((files == null) || (dirs == null))
		{
			return null;
		}

		Vector results = new Vector();
		for (int i = 0; i < files.length; i++)
		{
			results.addElement(files[i]);
		}

		for (int i = 0; i < dirs.length; i++)
		{
			if (dirs[i].isDirectory())
			{
				results.addElement(dirs[i]);
			}
		}

		SuperFile[] ret = new SuperFile[results.size()];
		results.copyInto(ret);
		return ret;
	}

	public SuperFile getDirectory()
	{
		String path = getPath();

		if (!isDirectory())
		{
			path = getParent();
		}
		return new SuperFile(path);
	}
	
	public boolean isEmpty()
	{
		String[] list = list();
		return ((list == null) || (list.length == 0));
	}

	public boolean mkdir()
	{
		boolean result = super.mkdir();
		if (result)
		{
			notifyListeners(new FileEvent(FileEvent.MKDIR, this));
		}
		return result;
	}

	public boolean mkdirs()
	{
		boolean result = super.mkdirs();
		if (result)
		{
			notifyListeners(new FileEvent(FileEvent.MKDIRS, this));
		}
		return result;
	}

	public boolean delete()
	{
		boolean result = super.delete();
		if (result)
		{
			notifyListeners(new FileEvent(FileEvent.DELETE, this));
		}
		return result;
	}

	public boolean renameTo(File dest)
	{
		boolean result = super.renameTo(dest);
		if (result)
		{
			notifyListeners(new FileEvent(FileEvent.RENAME, this, dest));
		}
		return result;
	}

	private void notifyListeners(FileEvent e)
	{
		for (int i = 0; i < this.listeners.size(); i++)
		{
			((FileListener)this.listeners.elementAt(i)).fileStateChanged(e);
		}
	}

	class WildcardFilter implements FilenameFilter
	{
		private RE filter;

		public WildcardFilter(String filter)
			throws IOException
		{
			try
			{
 				this.filter = new RE(filter);
			}
			catch (REException e)
			{
				StringWriter sw = new StringWriter();
				PrintWriter stackWriter = new PrintWriter(sw);
				e.printStackTrace(stackWriter);
			
				throw (new IOException("Illegal regular expression: " + filter + "\n" + sw.toString()));
			}
		}

		public boolean accept(File dir, String name)
		{
			return this.filter.isMatch(name);
		}
	}
	
	public OutputStream getOutputStream()
		throws FileNotFoundException
	{
		return (new FileOutputStream(this));
	}
	
	public InputStream getInputStream()
		throws FileNotFoundException
	{
		return (new FileInputStream(this));
	}
	
	public PrintStream getPrintStream()
		throws FileNotFoundException
	{
		return (new PrintStream(getOutputStream()));
	}
	
	public PrintWriter getPrintWriter()
		throws FileNotFoundException
	{
		return (new PrintWriter(getOutputStream()));
	}
	
	protected static class Serialization implements Serializable
	{
		protected String absolutePath;
		
		public Serialization(String absolutePath)
		{
			this.absolutePath = absolutePath;
		}
		
		protected Object readResolve()
			throws java.io.ObjectStreamException
		{
			SuperFile read = new SuperFile(this.absolutePath);
			return read;
		}
	}

	protected SuperFile()
	{
		super("");
	}
		
	protected Object writeReplace()
		throws java.io.ObjectStreamException
	{
		return (new Serialization(super.getAbsolutePath()));
	}
}
