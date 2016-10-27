package org.systemexception.ecommuter.test.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;
import org.systemexception.ecommuter.model.Territory;
import org.systemexception.ecommuter.services.LocationImpl;
import org.systemexception.ecommuter.test.End2End;

import static org.junit.Assert.*;

/**
 * @author leo
 * @date 02/07/16 01:24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class LocationImplTest {

	private final LocationApi sut = new LocationImpl();
	private final String locationItaly = End2End.LOCATION_ITALY;
	private final String locationMilano = "Milano";
	private final String locationPiazzaDuomo = "Piazza del Duomo";

	@Test
	public void address_to_geo() throws Exception {
		String stringAddress = "Piazza del Duomo Milano";
		Address geoFromAddress = sut.addressToGeo(stringAddress);

		assertNotNull(geoFromAddress);
		assertNotEquals(geoFromAddress, stringAddress);
		assertEquals("Piazza del Duomo, Milano, Italy", geoFromAddress.getFormattedAddress());
		assertEquals(45.4641776, geoFromAddress.getLatitude(), 0);
		assertEquals(9.1899885, geoFromAddress.getLongitude(), 0);
		assertEquals(locationItaly, geoFromAddress.getTerritory().getCountry());
		assertEquals(locationMilano, geoFromAddress.getTerritory().getPlaceName());
		assertEquals(locationItaly, geoFromAddress.getTerritory().getCountry());
		assertEquals(locationPiazzaDuomo, geoFromAddress.getRoute());
	}

	@Test
	public void address_to_geo_empty() throws Exception {
		String stringAddress = "A place that really does not exist";
		Address geoFromAddress = sut.addressToGeo(stringAddress);

		assertNull(geoFromAddress.getStreetNumber());
		assertNull(geoFromAddress.getTerritory());
		assertTrue(geoFromAddress.getLatitude() == 0d);
		assertTrue(geoFromAddress.getLongitude() == 0d);
	}

	@Test
	public void geo_to_address() throws Exception {
		Address addressFromGeo = sut.geoToAddress(45.4641776, 9.1899885);

		assertNotNull(addressFromGeo);
		assertEquals("Piazza del Duomo, 6, 20122 Milano, Italy", addressFromGeo.getFormattedAddress());
		assertEquals(45.4635507, addressFromGeo.getLatitude(), 0);
		assertEquals(9.1903881, addressFromGeo.getLongitude(), 0);
		assertEquals(locationItaly, addressFromGeo.getTerritory().getCountry());
		assertEquals(locationMilano, addressFromGeo.getTerritory().getPlaceName());
		assertEquals(locationItaly, addressFromGeo.getTerritory().getCountry());
		assertEquals(locationPiazzaDuomo, addressFromGeo.getRoute());
		assertEquals("20122", addressFromGeo.getTerritory().getPostalCode());
	}

	@Test
	public void geo_to_address_empty() throws Exception {
		Address addressFromGeo = sut.geoToAddress(0, 0);

		assertNull(addressFromGeo.getStreetNumber());
		assertNull(addressFromGeo.getTerritory());
		assertTrue(addressFromGeo.getLatitude() == 0d);
		assertTrue(addressFromGeo.getLongitude() == 0d);
	}

	@Test
	public void calculate_distance_for_addresses() throws Exception {
		Address addressLuino = sut.addressToGeo("Piazza Garibaldi, Luino, VA");
		Address addressVarese = sut.addressToGeo("Piazza Giovanni XXIII, Varese, VA");
		double distanceBetween = sut.distanceBetween(addressLuino, addressVarese);

		assertEquals(19.7, distanceBetween, 0);
	}

	@Test
	public void calculate_distance_for_coordinates() throws Exception {
		Address addressLuino = sut.geoToAddress(46.0021, 8.7507);
		Address addressBarcelona = sut.geoToAddress(41.38879, 2.15899);
		double distanceBetween = sut.distanceBetween(addressBarcelona, addressLuino);

		assertEquals(737.9, distanceBetween, 0);
	}

	@Test
	public void find_nearby_persons() {

		Person personA = new Person();
		Address addressWorkA = getAddress(End2End.LOCATION_LUINO_POSTCODE, 46.003509, 8.742917);
		Address addressHomeA = getAddress(End2End.LOCATION_LUINO_POSTCODE, 46.000490, 8.738347);
		personA.setId("ID_A");
		personA.setName(End2End.PERSON_NAME_A);
		personA.setLastname(End2End.PERSON_SURNAME_A);
		personA.setHomeAddress(addressHomeA);
		personA.setWorkAddress(addressWorkA);

		Person personB = new Person();
		Address addressWorkB = getAddress(End2End.LOCATION_LUINO_POSTCODE, 46.002834, 8.742499);
		Address addressHomeB = getAddress(End2End.LOCATION_LUINO_POSTCODE, 45.999950, 8.740594);
		personB.setId("ID_B");
		personB.setName(End2End.PERSON_NAME_B);
		personB.setLastname(End2End.PERSON_SURNAME_B);
		personB.setHomeAddress(addressHomeB);
		personB.setWorkAddress(addressWorkB);

		Person personC = new Person();
		Address addressWorkC = getAddress(End2End.LOCATION_LUINO_POSTCODE, 45.996015, 8.732703);
		Address addressHomeC = getAddress(End2End.LOCATION_LUINO_POSTCODE, 45.999659, 8.737842);
		personC.setId("ID_C");
		personC.setName(End2End.PERSON_NAME_C);
		personC.setLastname(End2End.PERSON_SURNAME_C);
		personC.setHomeAddress(addressHomeC);
		personC.setWorkAddress(addressWorkC);

		Persons persons = new Persons();
		persons.addPerson(personA);
		persons.addPerson(personB);
		persons.addPerson(personC);

		Persons nearbyPersons = sut.findNearbyPersons(personA, persons, 0.5);

		assertTrue(nearbyPersons.getPersons().size() == 1);

	}

	private Address getAddress(String postalCode, double latitude, double longitude) {
		Address address = new Address();
		Territory territory = new Territory("Country", postalCode, "PlaceName");
		address.setTerritory(territory);
		address.setLatitude(latitude);
		address.setLongitude(longitude);
		return address;
	}

}