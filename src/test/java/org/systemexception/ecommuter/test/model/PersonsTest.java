package org.systemexception.ecommuter.test.model;

import org.junit.Before;
import org.junit.Test;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;

import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 03/07/16 12:38
 */
public class PersonsTest {

	private Persons sut;
	private Person personA, personB;

	@Before
	public void setSut() {
		sut = new Persons();
		Address homeAddress = new Address();
		homeAddress.setPostalCode("123");
		Address workAddress = new Address();
		workAddress.setPostalCode("456");
		personA = new Person("NAME", "SURNAME", homeAddress, workAddress);
		personB = new Person("NAME2", "SURNAME2", homeAddress, workAddress);
		sut.addPerson(personA);
		sut.addPerson(personB);
	}

	@Test
	public void add_persons() {
		assertTrue(sut.getPersons().size() == 2);
	}

}