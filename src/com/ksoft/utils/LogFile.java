package com.ksoft.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogFile {
	public static Logger getLoggerFile(){
		FileHandler handler;
		Logger logger = Logger.getLogger("com.ksoft");
		try {
			handler = new FileHandler("log/logs.log", true);
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
	}
}
