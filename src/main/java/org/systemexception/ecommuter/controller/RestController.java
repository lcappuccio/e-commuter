package org.systemexception.ecommuter.controller;

import io.swagger.annotations.Api;
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
import org.systemexception.ecommuter.services.DatabaseApi;
import org.systemexception.ecommuter.services.LocationApi;
import org.systemexception.ecommuter.services.StorageApi;
import org.systemexception.ecommuter.enums.Endpoints;
import org.systemexception.ecommuter.exceptions.CsvParserException;
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

	private final DatabaseApi databaseService;
	private final LocationApi locationService;
	private final StorageApi storageService;

	@Autowired
	public RestController(DatabaseApi databaseService, LocationApi locationService, StorageApi storageService) {
		this.databaseService = databaseService;
		this.locationService = locationService;
		this.storageService = storageService;
	}

	@RequestMapping(value = Endpoints.ADD_TERRITORIES, method = RequestMethod.POST,
			produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<HttpStatus> addTerritories(
			@RequestParam(Endpoints.FILE_TO_UPLOAD) final MultipartFile dataFile)
			throws IOException, CsvParserException, TerritoriesException {

		File territoriesFile = storageService.saveFile(dataFile);
		databaseService.addTerritories(territoriesFile);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = Endpoints.PERSON + Endpoints.PERSON_ADD, method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> addPerson(@RequestBody @Valid final Person person) throws TerritoriesException {

		Person personSaved = databaseService.addPerson(person);
		return new ResponseEntity<>(personSaved, HttpStatus.CREATED);
	}

	@RequestMapping(value = Endpoints.PERSON + Endpoints.PERSON_BY_LASTNAME, method = RequestMethod.GET,
			params = {Endpoints.LAST_NAME}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Persons> findPersonByLastname(
			@RequestParam(value = Endpoints.LAST_NAME) final String lastname) throws TerritoriesException {

		Persons personsByLastname = databaseService.findPersonsByLastname(lastname);
		return new ResponseEntity<>(personsByLastname, HttpStatus.OK);
	}

	@RequestMapping(value = Endpoints.PERSON + Endpoints.PERSON_DELETE, method = RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> deletePerson(@RequestBody @Valid final Person person) {

		databaseService.deletePerson(person);
		return new ResponseEntity<>(person, HttpStatus.OK);
	}

	@RequestMapping(value = Endpoints.PERSON + Endpoints.PERSON_UPDATE, method = RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> updatePerson(@RequestBody @Valid final Person person) throws TerritoriesException {

		Person personUpdated = databaseService.updatePerson(person);
		return new ResponseEntity<>(personUpdated, HttpStatus.OK);
	}

	@RequestMapping(value = Endpoints.ADDRESS + Endpoints.GEO_TO_ADDRESS, method = RequestMethod.PUT,
			params = {Endpoints.LATITUDE, Endpoints.LONGITUDE}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Address> geoToAddress(
			@RequestParam(value = Endpoints.LATITUDE) final double latitude,
			@RequestParam(value = Endpoints.LONGITUDE) final double longitude) throws Exception {

		Address address = locationService.geoToAddress(latitude, longitude);
		return new ResponseEntity<>(address, HttpStatus.OK);
	}

	@RequestMapping(value = Endpoints.ADDRESS + Endpoints.ADDRESS_TO_GEO, method = RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Address> addressToGeo(@RequestBody @Valid final Address address) throws Exception {

		Address responseAddress = locationService.addressToGeo(address.getFormattedAddress());
		return new ResponseEntity<>(responseAddress, HttpStatus.OK);
	}

	@RequestMapping(value = Endpoints.PERSON + Endpoints.PERSON_NEARBY, method = RequestMethod.PUT,
			params = {Endpoints.DISTANCE}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Persons> nearbyPersons(
			@RequestBody @Valid final Person person,
			@RequestParam(value = Endpoints.DISTANCE) final double distance) {

		Persons personsLivesIn = databaseService.findPersonsLivesIn(
				person.getHomeAddress().getTerritory().getCountry(),
				person.getHomeAddress().getTerritory().getPostalCode());
		Persons personsWorksIn = databaseService.findPersonsWorksIn(
				person.getWorkAddress().getTerritory().getCountry(),
				person.getWorkAddress().getTerritory().getPostalCode());
		Persons fullPersonList = new Persons();

		for (Person personLiving : personsLivesIn.getPersons()) {
			fullPersonList.addPerson(personLiving);
		}
		for (Person personWorking : personsWorksIn.getPersons()) {
			fullPersonList.addPerson(personWorking);
		}

		Persons nearbyPersons = locationService.findNearbyPersons(person, fullPersonList, distance);
		return new ResponseEntity<>(nearbyPersons, HttpStatus.OK);
	}

}
