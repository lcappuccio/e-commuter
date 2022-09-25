package org.systemexception.ecommuter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.enums.Endpoints;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;
import org.systemexception.ecommuter.pojo.UserDataSantizer;
import org.systemexception.ecommuter.services.DatabaseApi;
import org.systemexception.ecommuter.services.LocationApi;
import org.systemexception.ecommuter.services.StorageApi;

import java.io.File;
import java.io.IOException;

/**
 * @author leo
 * @date 03/07/16 22:06
 */
@Controller
@RequestMapping(value = Endpoints.CONTEXT)
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

	@PostMapping(value = Endpoints.ADD_TERRITORIES, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<HttpStatus> addTerritories(
			@RequestParam(Endpoints.FILE_TO_UPLOAD) final MultipartFile dataFile)
			throws IOException, CsvParserException {

		final File territoriesFile = storageService.saveFile(dataFile);
		databaseService.addTerritories(territoriesFile);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = Endpoints.PERSON + Endpoints.PERSON_ADD, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> addPerson(@RequestBody @Validated final Person person) throws TerritoriesException {

		final Person personSaved = databaseService.addPerson(person);
		return new ResponseEntity<>(personSaved, HttpStatus.CREATED);
	}

	@GetMapping(value = Endpoints.PERSON + Endpoints.PERSON_BY_LASTNAME, params = {Endpoints.LAST_NAME},
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Persons> findPersonByLastname(
			@RequestParam(value = Endpoints.LAST_NAME) final String lastname) {

		final Persons personsByLastname = databaseService.findPersonsByLastname(lastname);
		return new ResponseEntity<>(personsByLastname, HttpStatus.OK);
	}

	@DeleteMapping(value = Endpoints.PERSON + Endpoints.PERSON_DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> deletePerson(@RequestBody @Validated final Person person) {

		databaseService.deletePerson(person);
		return new ResponseEntity<>(person, HttpStatus.OK);
	}

	@PutMapping(value = Endpoints.PERSON + Endpoints.PERSON_UPDATE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> updatePerson(@RequestBody @Validated final Person person) {

		final Person personUpdated = databaseService.updatePerson(person);
		return new ResponseEntity<>(personUpdated, HttpStatus.OK);
	}

	@PutMapping(value = Endpoints.ADDRESS + Endpoints.GEO_TO_ADDRESS, params = {Endpoints.LATITUDE,
            Endpoints.LONGITUDE}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Address> geoToAddress(
			@RequestParam(value = Endpoints.LATITUDE) final double latitude,
			@RequestParam(value = Endpoints.LONGITUDE) final double longitude) throws Exception {

		final Address address = locationService.geoToAddress(latitude, longitude);
		return new ResponseEntity<>(address, HttpStatus.OK);
	}

	@PutMapping(value = Endpoints.ADDRESS + Endpoints.ADDRESS_TO_GEO, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Address> addressToGeo(@RequestBody @Validated final Address address) throws Exception {

        final String safeFormattedAddress = UserDataSantizer.returnAsSafe(address.getFormattedAddress());
        final Address responseAddress = locationService.addressToGeo(safeFormattedAddress);
		return new ResponseEntity<>(responseAddress, HttpStatus.OK);
	}

	@PutMapping(value = Endpoints.PERSON + Endpoints.PERSON_NEARBY, params = {Endpoints.DISTANCE},
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Persons> nearbyPersons(
			@RequestBody @Validated final Person person,
			@RequestParam(value = Endpoints.DISTANCE) final double distance) {

		final Persons personsLivesIn = databaseService.findPersonsLivesIn(
				person.getHomeAddress().getTerritory().getCountry(),
				person.getHomeAddress().getTerritory().getPostalCode());
		final Persons personsWorksIn = databaseService.findPersonsWorksIn(
				person.getWorkAddress().getTerritory().getCountry(),
				person.getWorkAddress().getTerritory().getPostalCode());
		final Persons fullPersonList = new Persons();

		for (Person personLiving : personsLivesIn.getPersons()) {
			fullPersonList.addPerson(personLiving);
		}
		for (Person personWorking : personsWorksIn.getPersons()) {
			fullPersonList.addPerson(personWorking);
		}
		final Persons nearbyPersons = locationService.findNearbyPersons(person, fullPersonList, distance);
		return new ResponseEntity<>(nearbyPersons, HttpStatus.OK);
	}

}
