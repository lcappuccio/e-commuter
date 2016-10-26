package org.systemexception.ecommuter.test.controller;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.api.StorageApi;
import org.systemexception.ecommuter.controller.RestController;
import org.systemexception.ecommuter.enums.Endpoints;
import org.systemexception.ecommuter.model.Address;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.model.Persons;
import org.systemexception.ecommuter.model.Territory;
import org.systemexception.ecommuter.pojo.PersonJsonParser;
import org.systemexception.ecommuter.test.End2End;

import java.io.File;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author leo
 * @date 03/07/16 22:46
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class RestControllerTest {

	private DatabaseApi databaseApi;
	private LocationApi locationApi;
	private StorageApi storageApi;
	private RestController restController;
	private MockMvc sut;
	public final static String TEXT_PLAIN_FILE = "text/plain";

	@Before
	public void setSut() {
		databaseApi = mock(DatabaseApi.class);
		locationApi = mock(LocationApi.class);
		storageApi = mock(StorageApi.class);
		restController = new RestController(databaseApi, locationApi, storageApi);
		sut = MockMvcBuilders.standaloneSetup(restController).build();
	}

	@Test
	public void add_territories() throws Exception {
		MockMultipartFile dataFile = new MockMultipartFile(Endpoints.FILE_TO_UPLOAD,
				UUID.randomUUID().toString(), TEXT_PLAIN_FILE, "some data".getBytes());
		sut.perform(MockMvcRequestBuilders.fileUpload(Endpoints.CONTEXT + Endpoints.ADD_TERRITORIES).file(dataFile))
				.andExpect(status().is(HttpStatus.OK.value()));
		File receivedFile = storageApi.saveFile(dataFile);

		verify(databaseApi).addTerritories(receivedFile);
	}

	@Test
	public void add_person() throws Exception {
		Person person = PersonJsonParser.fromString(getPerson());
		when(databaseApi.addPerson(person)).thenReturn(person);
		sut.perform(MockMvcRequestBuilders.post(Endpoints.CONTEXT + Endpoints.PERSON + Endpoints.PERSON_ADD)
				.contentType(MediaType.APPLICATION_JSON).content(getPerson().getBytes()))
				.andExpect(status().is(HttpStatus.CREATED.value()));

		verify(databaseApi).addPerson(person);
	}

	@Test
	public void update_person() throws Exception {
		Person person = PersonJsonParser.fromString(getPerson());
		Person updatedPerson = new Person(person.getId(), "UPDATED_NAME", "UPDATED_SURNAME", person.getHomeAddress(),
				person.getWorkAddress());

		when(databaseApi.updatePerson(person)).thenReturn(updatedPerson);
		sut.perform(MockMvcRequestBuilders.put(Endpoints.CONTEXT + Endpoints.PERSON +
				Endpoints.PERSON_UPDATE).contentType(MediaType.APPLICATION_JSON).content(getPerson().getBytes()))
				.andExpect(status().is(HttpStatus.OK.value()));

		verify(databaseApi).updatePerson(person);
	}

	@Test
	public void delete_person() throws Exception {
		Person person = PersonJsonParser.fromString(getPerson());
		sut.perform(MockMvcRequestBuilders.delete(Endpoints.CONTEXT + Endpoints.PERSON + Endpoints.PERSON_DELETE)
				.contentType(MediaType.APPLICATION_JSON).content(getPerson().getBytes()))
				.andExpect(status().is(HttpStatus.OK.value()));

		verify(databaseApi).deletePerson(person);
	}

	@Test
	public void geo_to_address() throws Exception {
		String latitude = "123.4";
		String longitude = "456.7";
		sut.perform(MockMvcRequestBuilders.put(Endpoints.CONTEXT + Endpoints.ADDRESS + Endpoints.GEO_TO_ADDRESS)
				.param(Endpoints.LATITUDE, latitude).param(Endpoints.LONGITUDE, longitude))
				.andExpect(status().is(HttpStatus.OK.value()));

		verify(locationApi).geoToAddress(Double.valueOf(latitude), Double.valueOf(longitude));
	}

	@Test
	public void address_to_geo() throws Exception {
		Gson gson = new Gson();
		JsonParser jsonParser = new JsonParser();
		Address address = gson.fromJson(jsonParser.parse(getAddress()).getAsJsonObject(), Address.class);
		sut.perform(MockMvcRequestBuilders.put(Endpoints.CONTEXT + Endpoints.ADDRESS + Endpoints.ADDRESS_TO_GEO)
				.contentType(MediaType.APPLICATION_JSON).content(getAddress().getBytes()))
				.andExpect(status().is(HttpStatus.OK.value()));

		verify(locationApi).addressToGeo(address.getFormattedAddress());
	}

	@Test
	public void nearby_persons() throws Exception {
		double distance = 0.5;
		Person person = mock(Person.class);

		Persons personsLiving = mock(Persons.class);
		Address addressLiving = mock(Address.class);
		Territory territoryLiving = mock(Territory.class);

		Persons personsWorking = mock(Persons.class);
		Address addressWorking = mock(Address.class);
		Territory territoryWorking = mock(Territory.class);

		when(person.getHomeAddress()).thenReturn(addressLiving);
		when(person.getHomeAddress().getTerritory()).thenReturn(territoryLiving);
		when(territoryLiving.getCountry()).thenReturn(End2End.LOCATION_ITALY);
		when(territoryLiving.getPostalCode()).thenReturn(End2End.LOCATION_LUINO_POSTCODE);

		when(person.getWorkAddress()).thenReturn(addressWorking);
		when(person.getWorkAddress().getTerritory()).thenReturn(territoryWorking);
		when(territoryWorking.getCountry()).thenReturn(End2End.LOCATION_ITALY);
		when(territoryWorking.getPostalCode()).thenReturn(End2End.LOCATION_LUINO_POSTCODE);

		when(databaseApi.findPersonsLivesIn(territoryLiving.getCountry(), territoryLiving.getPostalCode()))
				.thenReturn(personsLiving);
		when(databaseApi.findPersonsWorksIn(territoryWorking.getCountry(), territoryWorking.getPostalCode()))
				.thenReturn(personsWorking);

		sut.perform(MockMvcRequestBuilders.put(Endpoints.CONTEXT + Endpoints.PERSON + Endpoints.PERSON_NEARBY)
				.param(Endpoints.DISTANCE, String.valueOf(distance))
				.contentType(MediaType.APPLICATION_JSON).content(getPerson().getBytes()))
				.andExpect(status().is(HttpStatus.OK.value()));

		verify(databaseApi).findPersonsLivesIn(person.getHomeAddress().getTerritory().getCountry(),
				person.getHomeAddress().getTerritory().getPostalCode());
		verify(databaseApi).findPersonsWorksIn(person.getWorkAddress().getTerritory().getCountry(),
				person.getWorkAddress().getTerritory().getPostalCode());
		// Testing only interaction
		verify(locationApi).findNearbyPersons(any(), any(), anyDouble());
	}

	private String getPerson() {
		return "{" +
				"\"name\": \"TEST_NAME_A\"," +
				"\"surname\": \"TEST_SURNAME_A\"," +
				"\"homeAddress\": {" +
				"\"streetNumber\": \"37\"," +
				"\"route\": \"Viale Dante Alighieri\"," +
				"\"administrativeAreaLevel2\": \"Provincia di Varese\"," +
				"\"administrativeAreaLevel1\": \"Lombardia\"," +
				"\"formattedAddress \": \"Viale Dante Alighieri, 37, 21016 Luino VA, Italy\"," +
				"\"latitude\": 46.00051029999999," +
				"\"longitude\": 8.7385149," +
				"\"territory\": {" +
				"\"country\": \"IT\"," +
				"\"postalCode\": \"21016\"," +
				"\"placeName\": \"Luino\"" +
				"}}," +
				"\"workAddress\": {" +
				"\"streetNumber\": \"9A\"," +
				"\"route\": \"Piazza Libertà\"," +
				"\"administrativeAreaLevel2\": \"Provincia di Varese\"," +
				"\"administrativeAreaLevel1\": \"Lombardia\"," +
				"\"formattedAddress\": \"Piazza Libertà, 9A, 21016 Luino VA, Italy \"," +
				"\"latitude\": 46.0035187," +
				"\"longitude\": 8.7429054," +
				"\"territory\": {" +
				"\"country\": \"IT\"," +
				"\"postalCode\": \"21016\"," +
				"\"placeName\": \"Luino\"}}}";
	}

	private String getAddress() {
		return "{\"streetNumber\":\"9A\",\"route\":\"Piazza Libertà\",\"locality\":\"Luino\"," +
				"\"administrativeAreaLevel2\":\"Provincia di Varese\",\"administrativeAreaLevel1\":\"Lombardia\"," +
				"\"country\":\"IT\",\"postalCode\":\"21016\",\"formattedAddress\":\"Piazza Libertà, 9A, 21016 " +
				"Luino" +
				" VA, Italy\",\"latitude\":46.0035187,\"longitude\":8.7429054}";
	}

}