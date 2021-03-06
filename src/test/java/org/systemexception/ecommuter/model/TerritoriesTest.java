package org.systemexception.ecommuter.model;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.systemexception.ecommuter.enums.Constants;

import java.security.InvalidParameterException;

import static org.junit.Assert.assertEquals;

/**
 * @author leo
 * @date 17/09/15 20:45
 */
public class TerritoriesTest {

	private Territories sut;
	private Territory territoryA, territoryB;

	@Before
	public void setUp() {
		territoryA = new Territory("IT", "123", "TEST");
		territoryB = new Territory("IT", "456", "TEST");
	}

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Test
	public void add_territories() {
		sut = new Territories();
		sut.addTerritory(territoryA);
		sut.addTerritory(territoryB);
		assertEquals(2, sut.getTerritories().size());
	}

	@Test
	public void add_duplicate_territory() {

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