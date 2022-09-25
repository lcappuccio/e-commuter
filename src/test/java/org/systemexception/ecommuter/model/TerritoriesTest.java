package org.systemexception.ecommuter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


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

	@Test
	void add_territories() {
		sut = new Territories();
		sut.addTerritory(territoryA);
		sut.addTerritory(territoryB);
		assertEquals(2, sut.getTerritories().size());
	}

	@Test
	void add_duplicate_territory() {

        sut = new Territories();
        sut.addTerritory(territoryA);
        final Territory badTerritory = new Territory("IT", "123", "TEST");
        assertThrows(InvalidParameterException.class, () -> {
            sut.addTerritory(badTerritory);
        });
        assertEquals(1, sut.getTerritories().size());
	}
}