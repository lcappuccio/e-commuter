package org.systemexception.ecommuter.pojo;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.systemexception.ecommuter.model.Person;

/**
 * @author leo
 * @date 02/07/16 16:22
 */
public class PersonJsonParser {

    private PersonJsonParser() {}

	static Person fromJson(final JsonObject jsonObject) {
		Gson gson = new Gson();
		Person person = new Person();
		if (jsonObject.isJsonObject()) {
			person = gson.fromJson(jsonObject, Person.class);
		}
		return person;
	}

	public static Person fromString(final String jsonString) {
		JsonObject parse = JsonParser.parseString(jsonString).getAsJsonObject();
		return fromJson(parse);
	}

	public static JsonObject fromPerson(final Person person) {
		Gson gson = new Gson();
		String personJson = gson.toJson(person);
		JsonElement jsonElement = JsonParser.parseString(personJson);
		return jsonElement.getAsJsonObject();
	}
}
