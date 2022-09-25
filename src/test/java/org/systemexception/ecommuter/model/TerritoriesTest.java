package org.systemexception.ecommuter.model;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.systemexception.ecommuter.enums.Constants;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author leo
 * @date 17/09/15 20:45
 */
class TerritoriesTest {

	private Territories sut;
	private Territory territoryA, territoryB;

	@BeforeEach
	void setUp() {
		territoryA = new Territory("IT", "123", "TEST");
		territoryB = new Territory("IT", "456", "TEST");
	}

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Test
	void add_territories() {
		sut = new Territories();
		sut.addTerritory(territoryA);
		sut.addTerritory(territoryB);
		assertEquals(2, sut.getTerritories().size());
	}

	@Test
	void add_duplicate_territory() {

		expectedException.expect(InvalidParameterException.class);
		expectedException.expectMessage("isValidTerritory"  + Constants.LOG_OBJECT_SEPARATOR + "IT" +
				Constants.LOG_ITEM_SEPARATOR + "123");

		sut = new Territories();
		sut.addTerritory(territoryA);
		final Territory badTerritory = new Territory("IT", "123", "TEST");
		sut.addTerritory(badTerritory);

		assertEquals(1, sut.getTerritories().size());
	}
}