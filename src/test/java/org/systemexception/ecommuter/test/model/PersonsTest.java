package org.systemexception.ecommuter.test.model;

import org.junit.Before;
import org.junit.Test;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;
import org.systemexception.ecommuter.model.Territory;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 03/07/16 12:38
 */
public class PersonsTest {

	private Persons sut;

	@Before
	public void setSut() {
		sut = new Persons();

		Address homeAddress = new Address();
		Territory territoryA = new Territory();
		territoryA.setPostalCode("123");
		homeAddress.setTerritory(territoryA);

		Address workAddress = new Address();
		Territory territoryB = new Territory();
		territoryB.setPostalCode("456");
		workAddress.setTerritory(territoryB);

		Person personA = new Person(UUID.randomUUID().toString(), "NAME", "SURNAME", homeAddress, workAddress);
		Person personB = new Person(UUID.randomUUID().toString(), "NAME2", "SURNAME2", homeAddress, workAddress);
		sut.addPerson(personA);
		sut.addPerson(personB);
	}

	@Test
	public void add_persons() {
		assertTrue(sut.getPersons().size() == 2);
	}

}