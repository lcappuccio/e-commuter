package org.systemexception.ecommuter.pojo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.End2EndTest;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.services.LocationApi;
import org.systemexception.ecommuter.services.LocationImpl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author leo
 * @date 02/07/16 16:25
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
class PersonJsonParserTest {

	private final LocationApi locationService = new LocationImpl();
	private Person person;
	private String personId;

	@BeforeEach
	void setSut() throws Exception {
		final Address addressFromGeo = locationService.geoToAddress(45.4641835, 9.1896379);
		personId = UUID.randomUUID().toString();
		person = new Person(personId, End2EndTest.PERSON_NAME_A, End2EndTest.PERSON_SURNAME_A, addressFromGeo ,addressFromGeo);
		person.setHomeAddress(addressFromGeo);
	}

	@Test
	void generate_person_from_json() {
		final JsonObject jsonObject = PersonJsonParser.fromPerson(person);

        assertEquals(jsonObject.get("name").getAsString(), person.getName());
        assertEquals(jsonObject.get("lastname").getAsString(), person.getLastname());
        assertEquals(
                jsonObject.getAsJsonObject("homeAddress").get("formattedAddress").getAsString(),
                person.getHomeAddress().getFormattedAddress()
        );
    }


	@Test
	void generate_json_from_person() {
		final Person personFromJson = PersonJsonParser.fromJson(getPersonJsonObject());

		assertEquals(person, personFromJson);
	}

	@Test
	void generate_person_from_string() {
		final Person pesonFromString = PersonJsonParser.fromString(getPersonJson());

		assertEquals(person, pesonFromString);
	}

	private String getPersonJson() {
        return "{\"id\":\"" + personId + "\",\"name\":\"TEST_NAME_A\",\"lastname\":\"TEST_SURNAME_A\"," +
				"\"lastname\":\"TEST_SURNAME_A\",\"homeAddress\":{\"route\":\"P.za del Duomo\"," +
                "\"streetNumber\":\"2\"," +
				"\"formattedAddress\":\"P.za del Duomo, 2, 20123 Milano MI, Italy\",\"latitude\":45.4642918," +
				"\"longitude\":9.189398599999999,\"territory\":{\"country\":\"IT\",\"postalCode\":\"20123\"," +
				"\"placeName\":\"Milano\"}},\"workAddress\":{\"route\":\"P.za del Duomo\"," +
                "\"streetNumber\":\"2\"," +
				"\"formattedAddress\":\"P.za del Duomo, 2, 20123 Milano MI, Italy\",\"latitude\":45.4642918," +
				"\"longitude\":9.189398599999999,\"territory\":{\"country\":\"IT\",\"postalCode\":\"20123\"," +
				"\"placeName\":\"Milano\"}}}";
	}

	private JsonObject getPersonJsonObject() {
		final JsonElement jsonElement = JsonParser.parseString(getPersonJson());
		return jsonElement.getAsJsonObject();
	}

}