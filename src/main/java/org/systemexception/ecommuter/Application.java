package org.systemexception.ecommuter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author leo
 * @date 01/07/16 23:18
 */
@SpringBootApplication
public class Application {

	@Value("${google.maps.api.key}")
	private String googleMapsApiKey;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
