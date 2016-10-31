package org.systemexception.ecommuter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.systemexception.ecommuter.services.DatabaseApi;
import org.systemexception.ecommuter.services.LocationApi;
import org.systemexception.ecommuter.services.StorageApi;
import org.systemexception.ecommuter.services.DatabaseImpl;
import org.systemexception.ecommuter.services.LocationImpl;
import org.systemexception.ecommuter.services.StorageImpl;
import springfox.documentation.service.ApiInfo;
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

	@Value("${database.name}")
	private String databaseName;

	@Value("${storage.folder}")
	private String storageFolder;

	private final static Logger logger = Logger.getLogger(Application.class);
	public final static String apiKey = System.getenv("API_KEY");

	public static void main(String[] args) {
		if (apiKey != null) {
			logger.info("Starting with API_KEY:" + apiKey);
			SpringApplication.run(Application.class, args);
		} else {
			String errorMessage = "API key is not set";
			logger.error(errorMessage);
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
				"leo@systemexception.org",
				"GPL v3",
				"https://github.com/lcappuccio/e-commuter/blob/master/LICENSE"
		);
	}

}
