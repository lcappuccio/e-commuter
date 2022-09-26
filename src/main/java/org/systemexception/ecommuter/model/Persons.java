package org.systemexception.ecommuter.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author leo
 * @date 03/07/16 12:35
 */
public class Persons {

	private final HashSet<Person> personList;

	public Persons() {
		personList = new HashSet<>();
	}

	public void addPerson(final Person person) {
		personList.add(person);
	}

	public Set<Person> getPersonList() {
		return personList;
	}
}
