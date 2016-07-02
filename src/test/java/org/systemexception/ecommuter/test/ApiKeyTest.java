package org.systemexception.ecommuter.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.systemexception.ecommuter.Application;

import static junit.framework.TestCase.assertNotNull;

/**
 * @author leo
 * @date 02/07/16 01:14
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class ApiKeyTest {

	@Test
	public void api_key_is_not_null() {
		assertNotNull(Application.apiKey);
	}
}
