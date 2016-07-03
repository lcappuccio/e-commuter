package org.systemexception.ecommuter.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
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
import org.systemexception.ecommuter.controller.RestController;
import org.systemexception.ecommuter.enums.Endpoints;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
	@InjectMocks
	private RestController restController;
	private MockMvc sut;

	@Before
	public void setSut() {
		databaseApi = mock(DatabaseApi.class);
		locationApi = mock(LocationApi.class);
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
		File receivedFile = new File(dataFile.getOriginalFilename());
		receivedFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(receivedFile);
		fos.write(dataFile.getBytes());
		fos.close();
		verify(databaseApi).addTerritories(receivedFile);
	}

}