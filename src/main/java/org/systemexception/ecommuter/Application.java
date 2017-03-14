package org.systemexception.ecommuter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.systemexception.ecommuter.services.*;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * @author leo
 * @date 01/07/16 23:18
 */
@SpringBootApplication
public class Application {

	public final static String API_KEY = System.getenv("API_KEY");
	private final static Logger LOGGER = Logger.getLogger(Application.class);

	@Value("${database.name}")
	private String databaseName;
	@Value("${storage.folder}")
	private String storageFolder;

	public static void main(String[] args) {
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

	@Bean
	public Docket restfulApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("restful-api").select().build().apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"e-commuter",
				"Save persons with geocoding and find who's close to who",
				null,
				null,
				new Contact("Leonardo Cappuccio", "https://github.com/lcappuccio/e-commuter/", null),
				"GPL v3",
				"https://github.com/lcappuccio/e-commuter/blob/master/LICENSE"
		);
	}

}
