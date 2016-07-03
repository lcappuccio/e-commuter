package org.systemexception.ecommuter.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leo
 * @date 03/07/16 12:35
 */
public class Persons {

	private final List<Person> persons;

	public Persons() {
		persons = new ArrayList();
	}

	public void addPerson(Person person) {
		persons.add(person);
	}

	public List<Person> getPersons() {
		return persons;
	}
}
