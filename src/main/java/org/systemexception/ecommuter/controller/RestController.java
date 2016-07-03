package org.systemexception.ecommuter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.enums.Endpoints;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;

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
	private DatabaseApi databaseApi;
	@Autowired
	private LocationApi locationService;

	@RequestMapping(value = Endpoints.ADD_TERRITORIES, method = RequestMethod.POST,
			produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<HttpStatus> save(@RequestParam(Endpoints.FILE_TO_UPLOAD) final MultipartFile dataFile)
			throws IOException, CsvParserException, TerritoriesException {

		File savedDataFile = new File(dataFile.getOriginalFilename());
		dataFile.transferTo(savedDataFile);
		databaseApi.addTerritories(savedDataFile);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
