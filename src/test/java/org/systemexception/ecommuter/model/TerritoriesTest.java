package org.systemexception.ecommuter.model;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.systemexception.ecommuter.enums.Constants;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.Territories;
import org.systemexception.ecommuter.model.Territory;

import java.security.InvalidParameterException;

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
		territoryA = new Territory("IT", "123", "TEST");
		territoryB = new Territory("IT", "456", "TEST");
	}

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Test
	public void add_territories() throws TerritoriesException, CsvParserException {
		sut = new Territories();
		sut.addTerritory(territoryA);
		sut.addTerritory(territoryB);
		assertTrue(sut.getTerritories().size() == 2);
	}

	@Test
	public void add_duplicate_territory() throws TerritoriesException, CsvParserException {

		expectedException.expect(InvalidParameterException.class);
		expectedException.expectMessage("isValidTerritory"  + Constants.LOG_OBJECT_SEPARATOR + "IT" +
				Constants.LOG_ITEM_SEPARATOR + "123");

		sut = new Territories();
		sut.addTerritory(territoryA);
		Territory badTerritory = new Territory("IT", "123", "TEST");
		sut.addTerritory(badTerritory);

		assertEquals(1, sut.getTerritories().size());
	}
}