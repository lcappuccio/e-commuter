package org.systemexception.ecommuter.test;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.systemexception.ecommuter.api.LoggerApi;
import org.systemexception.ecommuter.pojo.LogService;
import org.systemexception.ecommuter.services.DatabaseImpl;
import org.systemexception.ecommuter.services.LocationImpl;
import org.systemexception.ecommuter.services.StorageImpl;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author cappuccio
 * @date 07/07/16 15:47.
 */
public class LogServiceTest {

	private final LoggerApi sut = LogService.getFor(this.getClass());
	private final DatabaseImpl database = mock(DatabaseImpl.class);
	private final LocationImpl location = mock(LocationImpl.class);
	private final StorageImpl storage = mock(StorageImpl.class);

	@Test
	public void log_save_file() throws IOException {
		String dbName = "TEST";
		MockMultipartFile mockMultipartFile = new MockMultipartFile("FILE", "FILE".getBytes());
		File file = storage.saveFile(mockMultipartFile);
		verify(sut).saveFile(mockMultipartFile);
	}
}