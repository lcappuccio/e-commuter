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

	public static Person fromJson(JsonObject jsonObject) {
		Gson gson = new Gson();
		Person person = new Person();
		if (jsonObject.isJsonObject()) {
			person = gson.fromJson(jsonObject, Person.class);
		}
		return person;
	}

	public static JsonObject fromPerson(Person person) {
		Gson gson = new Gson();
		String personJson = gson.toJson(person);
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(personJson);
		return jsonElement.getAsJsonObject();
	}
}
