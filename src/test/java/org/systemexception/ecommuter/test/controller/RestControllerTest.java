package org.systemexception.ecommuter.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.api.DatabaseApi;
import org.systemexception.ecommuter.api.LocationApi;
import org.systemexception.ecommuter.api.StorageApi;
import org.systemexception.ecommuter.controller.RestController;
import org.systemexception.ecommuter.enums.Endpoints;
import org.systemexception.ecommuter.model.Person;
import org.systemexception.ecommuter.pojo.PersonJsonParser;

import java.io.File;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author leo
 * @date 03/07/16 22:46
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:application.properties")
public class RestControllerTest {

	private DatabaseApi databaseApi;
	private LocationApi locationApi;
	private StorageApi storageApi;
	@InjectMocks
	private RestController restController;
	private MockMvc sut;

	@Before
	public void setSut() {
		databaseApi = mock(DatabaseApi.class);
		locationApi = mock(LocationApi.class);
		storageApi = mock(StorageApi.class);
		restController = new RestController();
		MockitoAnnotations.initMocks(this);
		sut = MockMvcBuilders.standaloneSetup(restController).build();
	}

	@Test
	public void add_territories() throws Exception {
		MockMultipartFile dataFile = new MockMultipartFile(Endpoints.FILE_TO_UPLOAD,
				UUID.randomUUID().toString(), "text/plain", "some data".getBytes());
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

	private String getPerson() {
		return "{\"name\":\"TEST_NAME_A\",\"surname\":\"TEST_SURNAME_A\",\"homeAddress\":{\"streetNumber\":\"37\"," +
				"\"route\":\"Viale Dante Alighieri\",\"locality\":\"Luino\",\"administrativeAreaLevel2\":\"Provincia " +
				"di Varese\",\"administrativeAreaLevel1\":\"Lombardia\",\"country\":\"Italy\"," +
				"\"postalCode\":\"21016\",\"formattedAddress\":\"Viale Dante Alighieri, 37, 21016 Luino VA, Italy\"," +
				"\"latitude\":46.00051029999999,\"longitude\":8.7385149},\"workAddress\":{\"streetNumber\":\"9A\"," +
				"\"route\":\"Piazza Libertà\",\"locality\":\"Luino\",\"administrativeAreaLevel2\":\"Provincia di " +
				"Varese\",\"administrativeAreaLevel1\":\"Lombardia\",\"country\":\"Italy\",\"postalCode\":\"21016\"," +
				"\"formattedAddress\":\"Piazza Libertà, 9A, 21016 Luino VA, Italy\",\"latitude\":46.0035187," +
				"\"longitude\":8.7429054}}";
	}

}