package org.systemexception.ecommuter.model;

import org.systemexception.ecommuter.exceptions.PersonsException;

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

	public void addPerson(Person person) throws PersonsException {
		for (Person innerPerson : persons) {
			if (person.equals(innerPerson)) {
				throw new PersonsException("Person already exists");
			}
		}
		persons.add(person);
	}

	public List<Person> getPersons() {
		return persons;
	}
}
