package org.systemexception.ecommuter.test.model;

import org.junit.Before;
import org.junit.Test;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.Territories;
import org.systemexception.ecommuter.model.Territory;

import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 17/09/15 20:45
 */
public class TerritoriesTest {

	private Territories sut;
	private Territory territoryA, territoryB;

	@Before
	public void setUp() {
		territoryA = new Territory("123","TEST");
		territoryB = new Territory("456","TEST");
	}

	@Test
	public void add_territories() throws TerritoriesException {
		sut = new Territories();
		sut.addTerritory(territoryA);
		sut.addTerritory(territoryB);
		assertTrue(sut.getTerritories().size() == 2);
	}

	@Test(expected = TerritoriesException.class)
	public void add_duplicate_territory() throws TerritoriesException {
		sut = new Territories();
		sut.addTerritory(territoryA);
		Territory badTerritory = new Territory("123","TEST");
		sut.addTerritory(badTerritory);
	}
}