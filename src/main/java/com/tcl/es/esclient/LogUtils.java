package com.tcl.es.esclient;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.alibaba.fastjson.JSON;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public class LogUtils {
	
	static
	{
		JSON.parse("{}");
		LoggerFactory.getLogger("main");
	}

	public static void resetConfigByPath(String path) 
	{
		 // assume SLF4J is bo und to logback in the current environment
	    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
	    
	    try {
	      JoranConfigurator configurator = new JoranConfigurator();
	      configurator.setContext(context);
	      // Call context.reset() to clear any previous configuration, e.g. default 
	      // configuration. For multi-step configuration, omit calling context.reset().
	      context.reset(); 
	      configurator.doConfigure(path);
	    } catch (JoranException je) {
	      // StatusPrinter will handle this
	    	je.printStackTrace();
	    //	ch.qos.logback.core.pattern.parser.Compiler
	    }
	    StatusPrinter.printInCaseOfErrorsOrWarnings(context);
	}
	
	
	public static void resetConfigByData(String data) 
	{
//		File file = new File("logback_" + System.currentTimeMillis() + ".xml");
	    
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
	    
	    try {
//	    	FileUtils.writeStringToFile(file, data);
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			context.reset(); 
//			configurator.doConfigure(file.getAbsolutePath());
			//变更logback 时  不落地，以字节流方式传入logback 
			ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());
			configurator.doConfigure(bis);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("#############delete file: " + file + "#################");
//			file.delete();
	    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    StatusPrinter.printInCaseOfErrorsOrWarnings(context);
	}
	
	public static String logStackTrace(Throwable e) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		sb.append(e);
		sb.append("\n");
		StackTraceElement[] trace = e.getStackTrace();
		for (StackTraceElement traceElement : trace) {
			sb.append("\tat " + traceElement);
			sb.append("\n");
		}

		Throwable ourCause = e.getCause();
		if (ourCause != null) {
			sb.append("\n");
			sb.append(ourCause);
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public static void openTag(String tag)
	{
		if(tag != null)
			MDC.put("tag", tag);
	}
	
	public static void openTag(String tag, String esbid)
	{
		if(tag != null)
			MDC.put("tag", tag);
		
		if(esbid != null)
			MDC.put("esid", esbid);
	}
	
	public static void removeTag()
	{
		MDC.remove("tag");
	}
	
	public static Logger logger()
	{
		String threadName = Thread.currentThread().getName();
		
		Logger logger = LoggerFactory.getLogger(threadName);  
		
//		return new To8toLogger(logger);
		
		return logger;
	}
}