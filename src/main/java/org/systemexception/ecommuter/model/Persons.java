package org.systemexception.ecommuter.model;

import java.util.HashSet;
import java.util.Set;

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

	public Set<Person> getPersons() {
		return persons;
	}
}
