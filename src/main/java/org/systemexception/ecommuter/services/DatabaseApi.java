package org.systemexception.ecommuter.services;

import org.springframework.stereotype.Service;
import org.systemexception.ecommuter.exceptions.CsvParserException;
import org.systemexception.ecommuter.exceptions.TerritoriesException;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;

import java.io.File;

/**
 * @author leo
 * @date 02/07/16 23:07
 */
@Service
public interface DatabaseApi {

	/**
	 * Reads all lines from a csv file and creates all nodes
	 *
	 * @param territoriesFile the csv file containing the structure
	 */
	void addTerritories(File territoriesFile) throws CsvParserException;

	/**
	 * Adds a person to the database
	 *
	 * @param person
	 */
	Person addPerson(Person person) throws TerritoriesException;

	/**
	 * Updates a person
	 *
	 * @param person
	 */
	Person updatePerson(Person person);

	/**
	 * Deletes a person from the database
	 *
	 * @param person
	 */
	void deletePerson(Person person);

	/**
	 * Finds all persons living in your postal code
	 */
	Persons findPersonsLivesIn(String country, String postalCode);

	/**
	 * Finds all persons working in your postal code
	 */
	Persons findPersonsWorksIn(String country, String postalCode);

	/**
	 * Finds all persons with exactly the given last name
	 *
	 * @param lastname
	 * @return
	 */
	Persons findPersonsByLastname(String lastname);

}
