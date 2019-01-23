package org.systemexception.ecommuter.model;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

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
		assertEquals(2, sut.getPersons().size());
	}

}