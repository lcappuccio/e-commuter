package org.systemexception.ecommuter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author leo
 * @date 03/07/16 12:38
 */
class PersonsTest {

	private Persons sut;

	@BeforeEach
	void setSut() {
		sut = new Persons();

		final Address homeAddress = new Address();
		final Territory territoryA = new Territory();
		territoryA.setPostalCode("123");
		homeAddress.setTerritory(territoryA);

		final Address workAddress = new Address();
		final Territory territoryB = new Territory();
		territoryB.setPostalCode("456");
		workAddress.setTerritory(territoryB);

		final Person personA = new Person(UUID.randomUUID().toString(), "NAME", "SURNAME", homeAddress, workAddress);
		final Person personB = new Person(UUID.randomUUID().toString(), "NAME2", "SURNAME2", homeAddress, workAddress);
		sut.addPerson(personA);
		sut.addPerson(personB);
	}

	@Test
	void add_persons() {
		assertEquals(2, sut.getPersonList().size());
	}

}