package com.hotcode.logger;

import java.io.IOException;
import java.io.OutputStream;

public class LoggingOutputStream  extends OutputStream
{
	/**
	 * 
	 */
	protected static final String LINE_SEPERATOR = System.getProperty("line.separator");

	/**
	 * 
	 */
	protected boolean hasBeenClosed = false;

	/**
	 * 
	 */
	protected byte[] buf;

	/**
	 * 
	 */
	protected int count;

	/**
	 * 
	 */
	private int bufLength;
	
	/**
	 * 
	 */
	private Logger output = null;

	/**
	 * 
	 */
	public static final int DEFAULT_BUFFER_LENGTH = 2048;
	
	/**
	 * 
	 * @param logger
	 */
	public LoggingOutputStream(Logger output)
	{
		this.output = output;
		this.bufLength = DEFAULT_BUFFER_LENGTH;
	    this.buf = new byte[DEFAULT_BUFFER_LENGTH];
	    this.count = 0;
	}
	
	/**
	 * 
	 */
	public void close() 
	{
		_flush(true);
		hasBeenClosed = true;
	}

	/**
	 * 
	 */
	public void flush()
	{
		_flush(false);
	}

	
	/**
	 * 
	 */
	private void reset() 
	{
		//not resetting the buffer -- assuming that if it grew that it
		//   will likely grow similarly again
		count = 0;
	}

	@Override
	public void write(int b) throws IOException 
	{	
		if (hasBeenClosed)
		{
			throw new IOException("The stream has been closed.");
		}

		// don't log nulls
		if (b == 0) return;

		// would this be writing past the buffer?
		if (count == bufLength)
		{
			// grow the buffer
			final int newBufLength = bufLength+DEFAULT_BUFFER_LENGTH;
			final byte[] newBuf = new byte[newBufLength];
			System.arraycopy(buf, 0, newBuf, 0, bufLength);
			buf = newBuf;
			bufLength = newBufLength;
		}

		buf[count] = (byte)b;
		count++;

	}

	/**
	 * 
	 */
	private void _flush(boolean writeToFile) 
	{
		if (count == 0)
		{
			if (writeToFile) doWriteLog();
			return ;
		}

		//don't print out blank lines; flushing from PrintStream puts out these
		if (count == LINE_SEPERATOR.length()) 
		{
		     if ( ((char)buf[0]) == LINE_SEPERATOR.charAt(0)  &&
		           ( ( count == 1 ) ||  // <- Unix & Mac, -> Windows
		             ( (count == 2) && ((char)buf[1]) == LINE_SEPERATOR.charAt(1) ) ) ) 
		     {
		        reset();
		        return;
		     }
		}

		final byte[] theBytes = new byte[count];
		System.arraycopy(buf, 0, theBytes, 0, count);
		this.output.info(new String(theBytes));
		
		if (writeToFile) doWriteLog();
		
		reset();
	}
	
	/**
	 * 
	 */
	private void doWriteLog()
	{
		try {this.output.doWriteLog(); } catch (Exception ex){ /* Do nothing */};
	}
}
