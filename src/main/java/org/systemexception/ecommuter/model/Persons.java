package org.systemexception.ecommuter.model;

import java.util.HashSet;

/**
 * @author leo
 * @date 03/07/16 12:35
 */
public class Persons {

	private final HashSet<Person> persons;

	public Persons() {
		persons = new HashSet<>();
	}

	public void addPerson(final Person person) {
		persons.add(person);
	}

	public HashSet<Person> getPersons() {
		return persons;
	}
}
