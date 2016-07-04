package org.systemexception.ecommuter.test.services;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.api.StorageApi;
import org.systemexception.ecommuter.enums.Endpoints;
import org.systemexception.ecommuter.services.StorageImpl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author leo
 * @date 08/12/15 22:09
 */
public class StorageImplTest {

	private final static String STORAGE_FOLDER = "target" + File.separator + "test_output";
	private StorageApi sut;
	private static final String testFile = "it_data_SMALL.csv";
	private MultipartFile multipartFile;

	@BeforeClass
	public static void setSut() {
		File toRemove = new File(STORAGE_FOLDER);
		if (toRemove.exists()) {
			String[] files = toRemove.list();
			for (String file : files) {
				new File(STORAGE_FOLDER + File.separator + file).delete();
			}
		}
		toRemove.delete();

		assertFalse(toRemove.exists());
	}

	@AfterClass
	public static void tearDownSut() {
		File toRemove = new File(STORAGE_FOLDER);
		if (toRemove.exists()) {
			String[] files = toRemove.list();
			for (String file : files) {
				new File(STORAGE_FOLDER + File.separator + file).delete();
			}
		}
		toRemove.delete();

		assertFalse(toRemove.exists());
	}

	@Before
	public void setUp() throws IOException, URISyntaxException {
		sut = new StorageImpl(STORAGE_FOLDER);
		multipartFile = new MockMultipartFile(Endpoints.FILE_TO_UPLOAD, UUID.randomUUID().toString(),
				"text/plain", "some data".getBytes());
	}

	@Test
	public void outputFolderExists() {
		assertTrue(new File(STORAGE_FOLDER).exists());
	}

	@Test
	public void saveDataExists() throws IOException {
		File savedFile = sut.saveFile(multipartFile);
		File testDataFile = new File(STORAGE_FOLDER + File.separator + savedFile.getName());

		assertTrue(testDataFile.exists());
	}

	@Test
	public void historify() throws IOException {
		File savedFile = sut.saveFile(multipartFile);
		File testDataFile = new File(STORAGE_FOLDER + File.separator + savedFile.getName());
		BasicFileAttributes attrs = Files.readAttributes(testDataFile.toPath(), BasicFileAttributes.class);
		sut.saveFile(multipartFile);
		assertTrue(new File(STORAGE_FOLDER + File.separator + convertTime(attrs.creationTime().toMillis()) + "_" +
				testDataFile.getName()).exists());
	}

	private String convertTime(long time) {
		Date date = new Date(time);
		Format format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(date);
	}
}