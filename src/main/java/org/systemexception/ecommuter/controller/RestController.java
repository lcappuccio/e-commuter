package org.systemexception.ecommuter.controller;

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
import org.systemexception.ecommuter.enums.Endpoints;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.Person;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

/**
 * @author leo
 * @date 03/07/16 22:06
 */
@Controller
@RequestMapping(value = Endpoints.CONTEXT)
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
	public ResponseEntity<HttpStatus> save(@RequestParam(Endpoints.FILE_TO_UPLOAD) final MultipartFile dataFile)
			throws IOException, CsvParserException, TerritoriesException {

		File territoriesFile = storageService.saveFile(dataFile);
		databaseService.addTerritories(territoriesFile);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = Endpoints.PERSON + Endpoints.PERSON_ADD, method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> addPerson(@RequestBody @Valid final Person person) {

		Person personSaved = databaseService.addPerson(person);

		ResponseEntity<Person> personResponseEntity =
				new ResponseEntity<>(personSaved, HttpStatus.CREATED);

		return personResponseEntity;
	}

}
