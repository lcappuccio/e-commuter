package org.systemexception.ecommuter.test.pojo;

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
import org.systemexception.ecommuter.services.LocationApi;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.pojo.PersonJsonParser;
import org.systemexception.ecommuter.services.LocationImpl;
import org.systemexception.ecommuter.test.End2End;

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
		Address addressFromGeo = locationService.geoToAddress(45.4641776, 9.1899885);
		personId = UUID.randomUUID().toString();
		person = new Person(personId, End2End.PERSON_NAME_A, End2End.PERSON_SURNAME_A, addressFromGeo ,addressFromGeo);
		person.setHomeAddress(addressFromGeo);
	}

	@Test
	public void generate_person_from_json() {
		JsonObject jsonObject = PersonJsonParser.fromPerson(person);

		assertEquals(getPersonJson(), jsonObject.toString());
	}


	@Test
	public void generate_json_from_person() {
		Person personFromJson = PersonJsonParser.fromJson(getPersonJsonObject());

		assertEquals(person, personFromJson);
	}

	@Test
	public void generate_person_from_string() {
		Person pesonFromString = PersonJsonParser.fromString(getPersonJson());

		assertEquals(person, pesonFromString);
	}

	public String getPersonJson() {
		return "{\"id\":\"" + personId + "\",\"name\":\"TEST_NAME_A\",\"lastname\":\"TEST_SURNAME_A\"," +
				"\"homeAddress\":{\"streetNumber\":\"110\",\"route\":\"Piazza del Duomo\"," +
				"\"formattedAddress\":\"Piazza del Duomo, 110, 20122 Milano MI, Italy\",\"latitude\":45.4636631," +
				"\"longitude\":9.1897884," +
				"\"territory\":{\"country\":\"IT\",\"postalCode\":\"20122\",\"placeName\":\"Milano\"}}," +
				"\"workAddress\":{\"streetNumber\":\"110\",\"route\":\"Piazza del Duomo\"," +
				"\"formattedAddress\":\"Piazza del Duomo, 110, 20122 Milano MI, Italy\",\"latitude\":45.4636631," +
				"\"longitude\":9.1897884," +
				"\"territory\":{\"country\":\"IT\",\"postalCode\":\"20122\",\"placeName\":\"Milano\"}}}";
	}

	private JsonObject getPersonJsonObject() {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(getPersonJson());
		return jsonElement.getAsJsonObject();
	}

}