package org.systemexception.ecommuter.api;

import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;
import org.systemexception.ecommuter.model.Territory;

import java.io.File;

/**
 * @author cappuccio
 * @date 07/07/16 14:21.
 */
public interface LoggerApi {

	void addTerritories(File file);

	void addedTerritory(Territory territory);

	void loadedTerritories(File file);

	void addPerson(Person person);

	void addPersonRelation(Person person, Address address, String relationType);

	void addedPerson(Person person);

	void updatePerson(Person person);

	void updatedPerson(Person person);

	void deletePerson(Person person);

	void deletedPerson(Person person);

	void findPersonsLivingIn(String postalCode);

	void findPersonsWorkingIn(String postalCode);

	void getNodeByPostalCode(String postalCode);

	void getNodeByPostalCodeNotExists(String postalCode);

	void getPersonsByPostalCodeRelation(String postaCode, String relation);

	void foundPersonByPostalCodeRelation(String postalCode, String relation);

	void readCsvTerritories(File file);

	void finishCsvTerritories(File file);

	void geoToAddress(double latitude, double longitude);

	void geoToAddressError(double latitude, double longitude);

	void geoToAddressNoResult(double latitude, double longitude);

	void addressToGeo(String address);

	void addressToGeoError(String address);

	void addressToGeoNoGeo(String address);

	void distanceBetween(Address addressA, Address addressB);

	void findNearbyPersons(Person person, Persons persons, double radius);

	void findNearbyPersonsRemoveSelf(Person person);

	void foundNearby(Person person, double distanceHome, double distanceWork);

	void excludedNearby(Person person, double distanceHome, double distanceWork);

	void saveFile(MultipartFile multipartFile);

	void createStorageFolder(File folder);

	void historiFyFile(File file, String historifiedFileName);

	void closeDatabase();
}
