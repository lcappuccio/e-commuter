package org.systemexception.ecommuter.pojo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cappuccio
 * @date 07/07/16 14:16.
 */
public class LogService {

	private final Logger logger;

	private LogService(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Factory for logger
	 *
	 * @param clazz
	 * @return
	 */
	public static LogService getFor(Class clazz) {
		Logger logger = LoggerFactory.getLogger(clazz);
		return new LogService(logger);
	}

}
