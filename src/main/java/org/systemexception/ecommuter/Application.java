package org.systemexception.ecommuter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.systemexception.ecommuter.services.*;

import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * @author leo
 * @date 01/07/16 23:18
 */
@SpringBootApplication
public class Application {

	public static final String API_KEY = System.getenv("API_KEY");
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	@Value("${database.name}")
	private String databaseName;
	@Value("${storage.folder}")
	private String storageFolder;

	public static void main(final String[] args) {
		if (API_KEY != null) {
			LOGGER.info("Starting with API_KEY:" + API_KEY);
			SpringApplication.run(Application.class, args);
		} else {
			String errorMessage = "API key is not set";
			LOGGER.error(errorMessage);
			throw new InvalidParameterException(errorMessage);
		}
	}

	@Bean
	public DatabaseApi databaseApi() {
		return new DatabaseImpl(databaseName);
	}

	@Bean
	public LocationApi locationApi() {
		return new LocationImpl();
	}

	@Bean
	public StorageApi storageApi() throws IOException {
		return new StorageImpl(storageFolder);
	}
}
