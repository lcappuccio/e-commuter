package org.systemexception.ecommuter;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author leo
 * @date 01/07/16 23:18
 */
@SpringBootApplication
public class Application {

	private final static Logger logger = Logger.getLogger(Application.class);
	public final static String apiKey = System.getenv("API_KEY");

	public static void main(String[] args) {
		if (apiKey != null) {
			logger.info("Starting with API_KEY:" + apiKey);
			SpringApplication.run(Application.class, args);
		} else {
			System.out.println("API key is not set");
		}
	}
}
