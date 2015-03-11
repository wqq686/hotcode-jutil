package com.hotcode.logger;

import java.util.Collection;

/**
 * 
 * @author wuqq
 *
 */
public class LogFlushWorker implements Runnable {
	
	@Override
	public void run() {
		for(;!Thread.interrupted();) 
		{
			try 
			{
				flush();
				Thread.sleep(100) ;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				if (e instanceof InterruptedException)
				{
					flush(); 
					break ;
				}
				
			}
		}
	}
	
	
	/**
	 * 
	 */
	public static void flush() {
		Collection<Logger> coll = LoggerFactory.getLoggerMap().values();
		for (Logger logger : coll) {
			try
			{
				logger.doWriteLog();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}

