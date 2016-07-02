package org.systemexception.ecommuter.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.exceptions.LocationException;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.services.LocationApiImpl;
import org.systemexception.ecommuter.services.PersonJsonParser;

import static junit.framework.TestCase.assertEquals;

/**
 * @author leo
 * @date 02/07/16 16:25
 */
public class PersonJsonParserTest {

	private final LocationApi locationService = new LocationApiImpl();
	private Person person;

	@Before
	public void setSut() throws LocationException {
		Address addressFromGeo = locationService.geoToAddress(45.4641776, 9.1899885);
		person = new Person("TEST_NAME", "TEST_SURNAME", addressFromGeo);
		person.setAddress(addressFromGeo);
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

	private String getPersonJson() {
		return "{\"name\":\"TEST_NAME\",\"surname\":\"TEST_SURNAME\",\"address\":{\"route\":\"Piazza del Duomo\"," +
				"\"locality\":\"Milano\",\"administrativeAreaLevel2\":\"Città Metropolitana di Milano\"," +
				"\"administrativeAreaLevel1\":\"Lombardia\",\"country\":\"Italy\",\"postalCode\":\"20122\"," +
				"\"formattedAddress\":\"Piazza del Duomo, 6, 20122 Milano, Italy\",\"latitude\":45.4635507," +
				"\"longitude\":9.1903881}}";
	}

	private JsonObject getPersonJsonObject() {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(getPersonJson());
		return jsonElement.getAsJsonObject();
	}

}