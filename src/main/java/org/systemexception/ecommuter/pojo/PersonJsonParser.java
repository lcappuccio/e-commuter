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

	public static Person fromJson(final JsonObject jsonObject) {
		Gson gson = new Gson();
		Person person = new Person();
		if (jsonObject.isJsonObject()) {
			person = gson.fromJson(jsonObject, Person.class);
		}
		return person;
	}

	public static Person fromString(final String jsonString) {
		JsonParser jsonParser = new JsonParser();
		JsonObject parse = jsonParser.parse(jsonString).getAsJsonObject();
		return fromJson(parse);
	}

	public static JsonObject fromPerson(final Person person) {
		Gson gson = new Gson();
		String personJson = gson.toJson(person);
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(personJson);
		return jsonElement.getAsJsonObject();
	}
}
