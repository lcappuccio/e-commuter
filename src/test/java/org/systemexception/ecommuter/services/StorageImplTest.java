package org.systemexception.ecommuter.services;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.controller.RestControllerTest;
import org.systemexception.ecommuter.enums.Endpoints;
import org.systemexception.ecommuter.End2End;

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

	private final static String STORAGE_FOLDER = End2End.TARGET_FOLDER + File.separator + "test_storage";
	private StorageApi sut;
	private MultipartFile multipartFile;
	private static StorageApi storageApi;

	@BeforeClass
	public static void setSut() throws IOException {
		storageApi = new StorageImpl(STORAGE_FOLDER);

		storageApi.removeFolder(STORAGE_FOLDER);

		assertFalse(new File(STORAGE_FOLDER).exists());
	}

	@AfterClass
	public static void tearDownSut() {
		storageApi.removeFolder(STORAGE_FOLDER);

		assertFalse(new File(STORAGE_FOLDER).exists());
	}

	@Before
	public void setUp() throws IOException, URISyntaxException {
		sut = new StorageImpl(STORAGE_FOLDER);
		multipartFile = new MockMultipartFile(Endpoints.FILE_TO_UPLOAD, UUID.randomUUID().toString(),
				RestControllerTest.TEXT_PLAIN_FILE, "some data" .getBytes());
	}

	@Test
	public void outputFolderExists() {
		assertTrue(new File(STORAGE_FOLDER).exists());
	}

	@Test
	public void saveDataExists() throws IOException {
		final File savedFile = sut.saveFile(multipartFile);
		final File testDataFile = new File(STORAGE_FOLDER + File.separator + savedFile.getName());

		assertTrue(testDataFile.exists());
	}

	@Test
	public void historify() throws IOException {
		final File savedFile = sut.saveFile(multipartFile);
		final File testDataFile = new File(STORAGE_FOLDER + File.separator + savedFile.getName());
		final BasicFileAttributes attrs = Files.readAttributes(testDataFile.toPath(), BasicFileAttributes.class);
		sut.saveFile(multipartFile);
		assertTrue(new File(STORAGE_FOLDER + File.separator + convertTime(attrs.creationTime().toMillis())
				+ "_" + testDataFile.getName()).exists());
	}

	private String convertTime(long time) {
		final Date date = new Date(time);
		final Format format = new SimpleDateFormat(StorageImpl.DATETIME_FORMAT);
		return format.format(date);
	}
}