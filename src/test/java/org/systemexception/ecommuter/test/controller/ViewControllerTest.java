package org.systemexception.ecommuter.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.enums.Endpoints;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class ViewControllerTest {

	private MockMvc sut;

	@Before
	public void setSut() {
		ViewController viewController = new ViewController();
		sut = MockMvcBuilders.standaloneSetup(viewController).build();
	}

	@Test
	public void get_main_view() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(Endpoints.CONTEXT)).andExpect(status().is(HttpStatus.OK.value()));
	}
}