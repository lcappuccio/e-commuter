package org.systemexception.ecommuter.pojo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.End2End;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.services.LocationApi;
import org.systemexception.ecommuter.services.LocationImpl;

import java.util.UUID;

import static junit.framework.TestCase.assertEquals;

/**
 * @author leo
 * @date 02/07/16 16:25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class PersonJsonParserTest {

	private final LocationApi locationService = new LocationImpl();
	private Person person;
	private String personId;

	@Before
	public void setSut() throws Exception {
		final Address addressFromGeo = locationService.geoToAddress(45.4641835, 9.1896379);
		personId = UUID.randomUUID().toString();
		person = new Person(personId, End2End.PERSON_NAME_A, End2End.PERSON_SURNAME_A, addressFromGeo ,addressFromGeo);
		person.setHomeAddress(addressFromGeo);
	}

	@Test
	public void generate_person_from_json() {
		final JsonObject jsonObject = PersonJsonParser.fromPerson(person);

		assertEquals(getPersonJson(), jsonObject.toString());
	}


	@Test
	public void generate_json_from_person() {
		final Person personFromJson = PersonJsonParser.fromJson(getPersonJsonObject());

		assertEquals(person, personFromJson);
	}

	@Test
	public void generate_person_from_string() {
		final Person pesonFromString = PersonJsonParser.fromString(getPersonJson());

		assertEquals(person, pesonFromString);
	}

	private String getPersonJson() {
		return "{\"id\":\"" + personId + "\",\"name\":\"TEST_NAME_A\",\"lastname\":\"TEST_SURNAME_A\"," +
				"\"homeAddress\":{\"route\":\"Piazza del Duomo\"," +
				"\"formattedAddress\":\"Duomo, Piazza del Duomo, 20122 Milano MI, Italy\",\"latitude\":45.4641835," +
				"\"longitude\":9.1896379," +
				"\"territory\":{\"country\":\"IT\",\"postalCode\":\"20122\",\"placeName\":\"Milano\"}}," +
				"\"workAddress\":{\"route\":\"Piazza del Duomo\"," +
				"\"formattedAddress\":\"Duomo, Piazza del Duomo, 20122 Milano MI, Italy\",\"latitude\":45.4641835," +
				"\"longitude\":9.1896379," +
				"\"territory\":{\"country\":\"IT\",\"postalCode\":\"20122\",\"placeName\":\"Milano\"}}}";
	}

	private JsonObject getPersonJsonObject() {
		final JsonParser jsonParser = new JsonParser();
		final JsonElement jsonElement = jsonParser.parse(getPersonJson());
		return jsonElement.getAsJsonObject();
	}

}