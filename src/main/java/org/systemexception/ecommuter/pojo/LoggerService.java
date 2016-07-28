package org.systemexception.ecommuter.pojo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.api.LoggerApi;
import org.systemexception.ecommuter.enums.Constants;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Territory;

import java.io.File;

/**
 * @author cappuccio
 * @date 07/07/16 14:16.
 */
public class LoggerService implements LoggerApi {

	private final Logger logger;

	private LoggerService(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Factory for logger
	 *
	 * @param clazz the class to obtain a logger or
	 */
	public static LoggerService getFor(Class clazz) {
		Logger logger = LoggerFactory.getLogger(clazz);
		return new LoggerService(logger);
	}

	@Override
	public void createdDatabase(final String databaseName) {
		logger.info("DatabaseImpl" + Constants.LOG_OBJECT_SEPARATOR + databaseName);
	}

	@Override
	public void addTerritories(String fileName) {
		logger.info("addTerritories" + Constants.LOG_OBJECT_SEPARATOR + fileName);
	}

	@Override
	public void addedTerritory(Territory territory) {
		logger.info("addedTerritory" + Constants.LOG_OBJECT_SEPARATOR + territory.getPostalCode() +
				Constants.LOG_ITEM_SEPARATOR + territory.getPlaceName());
	}

	@Override
	public void loadedTerritories(File file) {
		logger.info("loadedTerritories" + Constants.LOG_OBJECT_SEPARATOR + file.getName());
	}

	@Override
	public void addPerson(Person person) {
		logger.info("addPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname() + Constants.LOG_ITEM_SEPARATOR +
				"lives in " + person.getHomeAddress().getPostalCode() + Constants.LOG_ITEM_SEPARATOR +
				"works in " + person.getWorkAddress().getPostalCode());
	}

	@Override
	public void addPersonRelation(Person person, Address address, String relationType) {
		logger.info("addPersonRelation" + Constants.LOG_OBJECT_SEPARATOR + person.getName() +
				Constants.LOG_ITEM_SEPARATOR + person.getSurname() + Constants.LOG_ITEM_SEPARATOR + relationType +
				Constants.LOG_ITEM_SEPARATOR + address.getFormattedAddress());
	}

	@Override
	public void addedPerson(Person person) {
		logger.info("addedPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname() + Constants.LOG_ITEM_SEPARATOR +
				"lives in " + person.getHomeAddress().getFormattedAddress() + Constants.LOG_ITEM_SEPARATOR +
				"works in " + person.getWorkAddress().getFormattedAddress());
	}

	@Override
	public void addedNotPerson(Person person, final String reason) {
		logger.error("addedPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname() +
				Constants.LOG_ITEM_SEPARATOR + reason);
	}

	@Override
	public void updatePerson(Person person) {
		logger.info("updatePerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname());
	}

	@Override
	public void updatedPerson(Person person) {
		logger.info("updatedPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname());
	}

	@Override
	public void updatedPersonNotFound(Person person) {
		logger.info("updatedPersonNotFound" + Constants.LOG_OBJECT_SEPARATOR + person.getId() +
				Constants.LOG_ITEM_SEPARATOR + person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname());
	}

	@Override
	public void deletePerson(Person person) {
		logger.info("deletePerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname());
	}

	@Override
	public void deletedPerson(Person person) {
		logger.info("deletedPerson" + Constants.LOG_OBJECT_SEPARATOR + person.getId() + Constants.LOG_ITEM_SEPARATOR +
				person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname());
	}

	@Override
	public void findPersonsLivingIn(String postalCode) {
		logger.info("findPersonsLivingIn" + Constants.LOG_OBJECT_SEPARATOR + postalCode);
	}

	@Override
	public void findPersonsWorkingIn(String postalCode) {
		logger.info("findPersonsWorkingIn" + Constants.LOG_OBJECT_SEPARATOR + postalCode);
	}

	@Override
	public void getNodeByPostalCode(String postalCode) {
		logger.info("getNodeByPostalCode" + Constants.LOG_OBJECT_SEPARATOR + postalCode);
	}

	@Override
	public void getNodeByPostalCodeNotExists(String postalCode) {
		logger.info("getNodeByPostalCode" + Constants.LOG_OBJECT_SEPARATOR + postalCode + " does not exist");
	}

	@Override
	public void getPersonsByPostalCodeRelation(String postalCode, String relation) {
		logger.info("getPersonsByPostalCodeRelation" + Constants.LOG_OBJECT_SEPARATOR + relation +
				Constants.LOG_ITEM_SEPARATOR + postalCode);
	}

	@Override
	public void foundPersonByPostalCodeRelation(String postalCode, String relation) {
		logger.info("getPersonsByPostalCodeRelation" + Constants.LOG_OBJECT_SEPARATOR + relation +
				Constants.LOG_ITEM_SEPARATOR + postalCode);
	}

	@Override
	public void readCsvTerritories(File file) {
		logger.info("readCsvTerritories" + Constants.LOG_OBJECT_SEPARATOR + file.getName());
	}

	@Override
	public void finishCsvTerritories(File file) {
		logger.info("finishCsvTerritories" + Constants.LOG_OBJECT_SEPARATOR + file.getName());
	}

	@Override
	public void geoToAddress(double latitude, double longitude) {
		logger.info("geoToAddress" + Constants.LOG_OBJECT_SEPARATOR + latitude + Constants.LOG_ITEM_SEPARATOR +
				longitude);
	}

	@Override
	public void geoToAddressError(double latitude, double longitude) {
		logger.error("geoToAddressError" + Constants.LOG_OBJECT_SEPARATOR + latitude + Constants.LOG_ITEM_SEPARATOR
				+ longitude);
	}

	@Override
	public void geoToAddressNoResult(double latitude, double longitude) {
		logger.info("geoToAddressNoResult:" + Constants.LOG_OBJECT_SEPARATOR + latitude + Constants.LOG_ITEM_SEPARATOR
				+ longitude);
	}

	@Override
	public void addressToGeo(String address) {
		logger.info("addressToGeo" + Constants.LOG_OBJECT_SEPARATOR + address);
	}

	@Override
	public void addressToGeoError(String address) {
		logger.info("addressToGeoError" + Constants.LOG_OBJECT_SEPARATOR + address);
	}

	@Override
	public void addressToGeoNoGeo(String address) {
		logger.info("addressToGeoNoGeo" + Constants.LOG_OBJECT_SEPARATOR + address);
	}

	@Override
	public void distanceBetween(Address addressA, Address addressB) {
		logger.info("distanceBetween" + Constants.LOG_OBJECT_SEPARATOR +
				"(" + addressA.getLatitude() + Constants.LOG_ITEM_SEPARATOR + addressA.getLongitude() + ")" +
				Constants.LOG_ITEM_SEPARATOR +
				"(" + addressB.getLatitude() + Constants.LOG_ITEM_SEPARATOR + addressB.getLongitude() + ")");
	}

	@Override
	public void findNearbyPersons(Person person, double radius) {
		logger.info("findNearbyPersons" + Constants.LOG_OBJECT_SEPARATOR + person.getName() +
				Constants.LOG_ITEM_SEPARATOR + person.getSurname() + Constants.LOG_ITEM_SEPARATOR +
				person.getHomeAddress().getPostalCode() + Constants.LOG_ITEM_SEPARATOR +
				person.getWorkAddress().getPostalCode() + Constants.LOG_ITEM_SEPARATOR + "distance " + radius);
	}

	@Override
	public void findNearbyPersonsRemoveSelf(Person person) {
		logger.info("findNearbyPersonsRemoveSelf" + Constants.LOG_OBJECT_SEPARATOR + person.getName() +
				Constants.LOG_ITEM_SEPARATOR + person.getSurname());
	}

	@Override
	public void foundNearby(Person person, double distanceHome, double distanceWork) {
		logger.info("foundNearby" + Constants.LOG_OBJECT_SEPARATOR + person.getName() +
				Constants.LOG_ITEM_SEPARATOR + person.getSurname() + Constants.LOG_ITEM_SEPARATOR +
				"distance home " + distanceHome + Constants.LOG_ITEM_SEPARATOR + "distance work " + distanceWork);
	}

	@Override
	public void excludedNearby(Person person, double distanceHome, double distanceWork) {
		logger.info("excludedNearby" + Constants.LOG_OBJECT_SEPARATOR + person.getName() +
				Constants.LOG_ITEM_SEPARATOR + person.getSurname() + Constants.LOG_ITEM_SEPARATOR +
				"distance home " + distanceHome + Constants.LOG_ITEM_SEPARATOR +
				"distance work " + distanceWork);
	}

	@Override
	public void saveFile(MultipartFile multipartFile) {
		logger.info("saveFile" + Constants.LOG_OBJECT_SEPARATOR + multipartFile.getName());
	}

	@Override
	public void deleteFile(File file) {
		logger.info("deleteFile" + Constants.LOG_OBJECT_SEPARATOR + file.getName());
	}

	@Override
	public void createStorageFolder(File folder) {
		logger.info("createStorageFolder" + Constants.LOG_OBJECT_SEPARATOR + folder.getName());
	}

	@Override
	public void historiFyFile(File file, String historifiedFileName) {
		logger.info("historiFyFile" + Constants.LOG_OBJECT_SEPARATOR + file.getName() + Constants.LOG_ITEM_SEPARATOR +
				historifiedFileName);
	}

	@Override
	public void removeFolderOk(String folderName) {
		logger.info("removeFolderOk" + Constants.LOG_OBJECT_SEPARATOR + folderName);
	}

	@Override
	public void removeFolderKo(String folderName) {
		logger.error("removeFolderKo" + Constants.LOG_OBJECT_SEPARATOR + folderName);
	}

	@Override
	public void csvLoaded(File file) {
		logger.info("loadedCsv" + Constants.LOG_OBJECT_SEPARATOR + file.getName());
	}

	@Override
	public void csvLoadError(File file, String message) {
		logger.info("loadedCsv" + Constants.LOG_OBJECT_SEPARATOR + file.getName() + Constants.LOG_ITEM_SEPARATOR +
				message);
	}

	@Override
	public void closeDatabase() {
		logger.info("closeDatabase");
	}
}
