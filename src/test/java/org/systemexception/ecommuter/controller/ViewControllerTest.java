package org.systemexception.ecommuter.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.systemexception.ecommuter.Application;
import org.systemexception.ecommuter.enums.Endpoints;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
class ViewControllerTest {

	private MockMvc sut;
	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setSut() {
		sut = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void get_main_view() throws Exception {
		sut.perform(MockMvcRequestBuilders.get(Endpoints.CONTEXT)).andExpect(status().is(HttpStatus.OK.value()))
				.andExpect(content().string(containsString("e-commuter")));
	}
}