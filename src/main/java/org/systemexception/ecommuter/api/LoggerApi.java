package org.systemexception.ecommuter.api;

import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Territory;

import java.io.File;

/**
 * @author cappuccio
 * @date 07/07/16 14:21.
 */
public interface LoggerApi {

	// Territories
	void addTerritories(String fileName);

	void addedTerritory(Territory territory);

	void loadedTerritories(File file);

	void readCsvTerritories(File file);

	void finishCsvTerritories(File file);

	// Person
	void addPerson(Person person);

	void addPersonRelation(Person person, Address address, String relationType);

	void addedPerson(Person person);

	void addedNotPerson(Person person);

	void updatePerson(Person person);

	void updatedPerson(Person person);

	void deletePerson(Person person);

	void deletedPerson(Person person);

	void findPersonsLivingIn(String postalCode);

	void findPersonsWorkingIn(String postalCode);

	void getPersonsByPostalCodeRelation(String postaCode, String relation);

	void foundPersonByPostalCodeRelation(String postalCode, String relation);

	void findNearbyPersons(Person person, double radius);

	void findNearbyPersonsRemoveSelf(Person person);

	void foundNearby(Person person, double distanceHome, double distanceWork);

	void excludedNearby(Person person, double distanceHome, double distanceWork);

	// Nodes
	void getNodeByPostalCode(String postalCode);

	void getNodeByPostalCodeNotExists(String postalCode);

	// Address and Geocodes
	void geoToAddress(double latitude, double longitude);

	void geoToAddressError(double latitude, double longitude);

	void geoToAddressNoResult(double latitude, double longitude);

	void addressToGeo(String address);

	void addressToGeoError(String address);

	void addressToGeoNoGeo(String address);

	void distanceBetween(Address addressA, Address addressB);

	// Utilities
	void saveFile(MultipartFile multipartFile);

	void createStorageFolder(File folder);

	void historiFyFile(File file, String historifiedFileName);

	void deleteFile(File file);

	void removeFolderOk(String folderName);

	void removeFolderKo(String folderName);

	void csvLoaded(File file);

	void csvLoadError(File file, String message);

	void closeDatabase();
}
