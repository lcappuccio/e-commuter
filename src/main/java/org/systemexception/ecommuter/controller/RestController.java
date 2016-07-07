package org.systemexception.ecommuter.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.api.StorageApi;
import org.systemexception.ecommuter.enums.Constants;
import org.systemexception.ecommuter.enums.Endpoints;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.LocationException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

/**
 * @author leo
 * @date 03/07/16 22:06
 */
@Controller
@RequestMapping(value = Endpoints.CONTEXT)
@EnableSwagger2
@Api(basePath = Endpoints.CONTEXT, description = "e-commuter REST API")
public class RestController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DatabaseApi databaseService;
	@Autowired
	private LocationApi locationService;
	@Autowired
	private StorageApi storageService;

	@RequestMapping(value = Endpoints.ADD_TERRITORIES, method = RequestMethod.POST,
			produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<HttpStatus> addTerritories(
			@RequestParam(Endpoints.FILE_TO_UPLOAD) final MultipartFile dataFile)
			throws IOException, CsvParserException, TerritoriesException {

		logger.info("AddTerritories: " + dataFile.getName());
		File territoriesFile = storageService.saveFile(dataFile);
		databaseService.addTerritories(territoriesFile);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = Endpoints.PERSON + Endpoints.PERSON_ADD, method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> addPerson(@RequestBody @Valid final Person person) {

		logger.info("AddPerson: " + person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname());
		Person personSaved = databaseService.addPerson(person);
		ResponseEntity<Person> personResponseEntity = new ResponseEntity<>(personSaved, HttpStatus.CREATED);
		return personResponseEntity;
	}

	@RequestMapping(value = Endpoints.PERSON + Endpoints.PERSON_DELETE, method = RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> deletePerson(@RequestBody @Valid final Person person) {

		logger.info("DeletePerson: " + person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname());
		databaseService.deletePerson(person);
		ResponseEntity<Person> personResponseEntity = new ResponseEntity<>(person, HttpStatus.OK);
		return personResponseEntity;
	}

	@RequestMapping(value = Endpoints.ADDRESS + Endpoints.GEO_TO_ADDRESS, method = RequestMethod.PUT,
			params = {Endpoints.LATITUDE, Endpoints.LONGITUDE}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Address> geoToAddress(
			@RequestParam(value = Endpoints.LATITUDE) final double latitude,
			@RequestParam(value = Endpoints.LONGITUDE) final double longitude) throws LocationException {

		// TODO LC this logger is duplicated, org.systemexception.ecommuter.services.LocationImpl.geoToAddress()
		logger.info("GeoToAddress: (" + latitude + Constants.LOG_ITEM_SEPARATOR + longitude + ")");
		Address address = locationService.geoToAddress(latitude, longitude);
		ResponseEntity<Address> addressResponseEntity = new ResponseEntity<>(address, HttpStatus.OK);
		return addressResponseEntity;
	}

	@RequestMapping(value = Endpoints.ADDRESS + Endpoints.ADDRESS_TO_GEO, method = RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Address> addressToGeo(@RequestBody @Valid final Address address) throws LocationException {

		// TODO LC this logger is duplicated, org.systemexception.ecommuter.services.LocationImpl.addressToGeo()
		logger.info("AddressToGeo: " + address.getFormattedAddress());
		Address responseAddress = locationService.addressToGeo(address.getFormattedAddress());
		ResponseEntity<Address> addressResponseEntity = new ResponseEntity<>(responseAddress, HttpStatus.OK);
		return addressResponseEntity;
	}

	@RequestMapping(value = Endpoints.PERSON + Endpoints.PERSON_NEARBY, method = RequestMethod.PUT,
			params = {Endpoints.DISTANCE}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Persons> nearbyPersons(
			@RequestBody @Valid final Person person,
			@RequestParam(value = Endpoints.DISTANCE) final double distance) {

		// TODO LC this logger is duplicated, org.systemexception.ecommuter.services.LocationImpl.findNearbyPersons()
		logger.info("FindNearbyPersons: " + person.getName() + Constants.LOG_ITEM_SEPARATOR + person.getSurname() +
				Constants.LOG_ITEM_SEPARATOR + person.getHomeAddress().getPostalCode() + Constants.LOG_ITEM_SEPARATOR +
				person.getWorkAddress().getPostalCode());
		Persons personsLivesIn = databaseService.findPersonsLivesIn(person.getHomeAddress().getPostalCode());
		Persons personsWorksIn = databaseService.findPersonsWorksIn(person.getWorkAddress().getPostalCode());
		Persons fullPersonList = new Persons();
		for (Person personLiving : personsLivesIn.getPersons()) {
			fullPersonList.addPerson(personLiving);
		}
		for (Person personWorking : personsWorksIn.getPersons()) {
			if (!fullPersonList.getPersons().contains(person)) {
				fullPersonList.addPerson(personWorking);
			}
		}
		Persons nearbyPersons = locationService.findNearbyPersons(person, fullPersonList, distance);
		ResponseEntity<Persons> personsResponseEntity = new ResponseEntity<>(nearbyPersons, HttpStatus.OK);
		return personsResponseEntity;
	}

}
