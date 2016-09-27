package org.systemexception.ecommuter.model;

import java.util.HashSet;

/**
 * @author leo
 * @date 01/03/15 18:25
 */
public class Territories {

	private final HashSet<Territory> territories;

	public Territories() {
		territories = new HashSet<>();
	}

	public void addTerritory(Territory territory) {
		territories.add(territory);
	}

	public HashSet<Territory> getTerritories() {
		return territories;
	}
}
