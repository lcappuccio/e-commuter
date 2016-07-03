package org.systemexception.ecommuter.model;

import org.systemexception.ecommuter.exceptions.TerritoriesException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leo
 * @date 01/03/15 18:25
 */
public class Territories {

	private final List<Territory> territories;

	public Territories() {
		territories = new ArrayList();
	}

	public void addTerritory(Territory territory) throws TerritoriesException {
		for (Territory territory1 : territories) {
			if (territory.getPostalCode().equals(territory1.getPostalCode()) &&
					territory.getPlaceName().equals(territory1.getPlaceName())) {
				throw new TerritoriesException("Territory already exists");
			}
		}
		territories.add(territory);
	}

	public List<Territory> getTerritories() {
		return territories;
	}
}
