package org.systemexception.ecommuter.test.model;

import org.junit.Before;
import org.junit.Test;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.Territories;
import org.systemexception.ecommuter.model.Territory;

import static org.junit.Assert.assertEquals;
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
		territoryA = new Territory("IT", "123", "TEST", "ADMINLEVEL1", "ADMINLEVEL2");
		territoryB = new Territory("IT", "456", "TEST", "ADMINLEVEL1", "ADMINLEVEL2");
	}

	@Test
	public void add_territories() throws TerritoriesException {
		sut = new Territories();
		sut.addTerritory(territoryA);
		sut.addTerritory(territoryB);
		assertTrue(sut.getTerritories().size() == 2);
	}

	@Test
	public void add_duplicate_territory() throws TerritoriesException {
		sut = new Territories();
		sut.addTerritory(territoryA);
		Territory badTerritory = new Territory("IT", "123", "TEST", "ADMINLEVEL1", "ADMINLEVEL2");
		sut.addTerritory(badTerritory);

		assertEquals(1, sut.getTerritories().size());
	}
}