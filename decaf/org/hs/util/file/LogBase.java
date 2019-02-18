package org.hs.util.file;

import java.io.*;
import java.text.*;
import java.util.*;
import org.hs.util.*;

public class LogBase extends SuperFile
{
	protected static final BitSequence idFactory = new BitSequence();
	
	protected static final Bits CRITICAL = idFactory.next(BitSequence.BITWISE);
	protected static final Bits WARNING = idFactory.next(BitSequence.BITWISE);
	protected static final Bits MESSAGE = idFactory.next(BitSequence.BITWISE);
	protected static final Bits DEBUG = idFactory.next(BitSequence.BITWISE);
	protected static final Bits EXCEPTION = idFactory.next(BitSequence.BITWISE);
	protected static final Bits UNCAUGHT = idFactory.next(BitSequence.BITWISE);
	protected static final Bits SYSTEM_ERR = idFactory.next(BitSequence.BITWISE);
	
	public static final Bits C = CRITICAL;
	public static final Bits CE = CRITICAL.orEquals(EXCEPTION);
	public static final Bits W = WARNING;
	public static final Bits WE = WARNING.orEquals(EXCEPTION);
	public static final Bits M = MESSAGE;
	public static final Bits D = DEBUG;
	public static final Bits U = UNCAUGHT;
	public static final Bits OUT = SYSTEM_ERR;

	protected static BitSetMap baseLogs;	
	protected static SuperFile baseLogDir = new SuperFile("./log");
	protected static DateFormat defaultTimestamp = new SimpleDateFormat("yyyy.MM.dd '@' hh:mm:ss z': '");
	protected static LogThread logThread;
	
	protected static Collection logThese = Collections.synchronizedCollection(new Vector());

	static
	{
		logThread = new LogThread();
		
		baseLogs = createBaseLogs(baseLogDir);
	}
	
	protected static BitSetMap createBaseLogs(SuperFile logDir)
	{
		BitSetMap logs = new BitSetMap();
		
		
		if (!logDir.exists ())
		{
			logDir.mkdirs();
		}
		
		logs.put(CRITICAL, new LogBase(logDir, "critical.log"));
		logs.put(WARNING, new LogBase(logDir, "warning.log"));
		logs.put(MESSAGE, new LogBase(logDir, "message.log"));
		logs.put(EXCEPTION, new LogBase(logDir, "exception.log"));
		logs.put(DEBUG, new LogBase(logDir, "debug.log"));
		logs.put(UNCAUGHT, new LogBase(logDir, "uncaught.log"));
		
		return logs;
	}
	
	protected LogBase()
	{
	}
	
	protected LogBase(SuperFile logDir, String filename)
	{
		super(logDir, filename);
	}

	public static void init(String path)
	{
		baseLogDir = new SuperFile(path);
	}
	
	public static void log(String message, Bits logTo)
	{
		logThese.add(new LogMessage(message, logTo, baseLogs, defaultTimestamp));
	}	
	
	protected static void log(String message, Bits logTo, BitSetMap logs)
	{
		logThese.add(new LogMessage(message, logTo, logs, defaultTimestamp));
	}	
	
	protected static void log(String message, Bits logTo, BitSetMap logs, DateFormat dateFormat)
	{
		logThese.add(new LogMessage(message, logTo, logs, dateFormat));
	}	
	
	public static void systemExit(int code)
	{
		while (!logThese.isEmpty())
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
			}
		}
		System.exit(code);
	}
	
	private void log(String message)
		throws IOException
	{
		//System.err.println("Logging to " + super.getAbsolutePath());
		super.append(message);
	}

	static class LogMessage
	{
		String message;
		Bits logTo;
		BitSetMap logs;
		DateFormat dateFormat;
		boolean relayToSystemErr;
		
		public LogMessage(String message, Bits logTo, BitSetMap logs, DateFormat dateFormat)
		{
			this.message = message;
			this.logTo = logTo;
			this.logs = logs;
			this.dateFormat = dateFormat;
			this.relayToSystemErr = logTo.hasTheseOnBits(SYSTEM_ERR);
		}
		
		public boolean relayToSystemErr()
		{
			return this.relayToSystemErr;
		}
		
		public Iterator getOutputs()
		{
			return this.logs.get(this.logTo);
		}
		
		public String getMessage()
		{
			return this.dateFormat.format(new Date()) + this.message + "\n";
		}
	}
	
	static class LogThread extends Thread
	{
		public void run()
		{
			try
			{
				LogMessage logMessage;
				LogBase logBase;
				Iterator outputs;
				Iterator messages;
				TreeSet writeLockedFiles = new TreeSet();
				Iterator files;

				while (true)
				{
					sleep(2000);
					
					synchronized (logThese)
					{
						messages = logThese.iterator();
						while (messages.hasNext())
						{
							logMessage = (LogMessage)messages.next();
							
							if (logMessage.relayToSystemErr())
							{
								System.err.println(logMessage.getMessage());
							}
							
							outputs = logMessage.getOutputs();
							while (outputs.hasNext())
							{
								logBase = (LogBase)outputs.next();
								
								writeLockedFiles.add(logBase);
								
								try
								{
									logBase.log(logMessage.getMessage());
								}
								catch (IOException e)
								{
									System.err.println ("Error logging to " + logBase.getName () + ": " + e.getClass ().getName () + ": " + e.getMessage ());
								}
							}
						}
						
						logThese.clear();
					}
					
					files = writeLockedFiles.iterator();
					while (files.hasNext())
					{
						logBase = (LogBase)files.next();
						try
						{
							logBase.unlock();
						}
						catch (IOException e)
						{
							// never mind then!
						}
					}
					writeLockedFiles.clear(); 
				}
			}
			catch (InterruptedException e)
			{
				throw (new RuntimeException("LogBase.LogThread stopped!  " + e.getClass().getName() + ": " + e.getMessage()));
			}
		}

		// throw a timer in here to unhook write lock on files after a few seconds
	}
}