package org.systemexception.ecommuter.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.End2EndTest;
import org.systemexception.ecommuter.controller.RestControllerTest;
import org.systemexception.ecommuter.enums.Endpoints;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author leo
 * @date 08/12/15 22:09
 */
public class StorageImplTest {

	private final static String STORAGE_FOLDER = End2EndTest.TARGET_FOLDER + File.separator + "test_storage";
	private StorageApi sut;
	private MultipartFile multipartFile;

	@BeforeAll
	public static void setSut() {
		End2EndTest.removeFolder(STORAGE_FOLDER);
    }

	@AfterAll
	public static void tearDownSut() {
        End2EndTest.removeFolder(STORAGE_FOLDER);
    }

	@BeforeEach
	public void setUp() throws IOException {
		sut = new StorageImpl(STORAGE_FOLDER);
		multipartFile = new MockMultipartFile(Endpoints.FILE_TO_UPLOAD, UUID.randomUUID().toString(),
				RestControllerTest.TEXT_PLAIN_FILE, "some data" .getBytes());
	}

	@Test
	void outputFolderExists() {
		assertTrue(new File(STORAGE_FOLDER).exists());
	}

	@Test
	void saveDataExists() throws IOException {
		final File savedFile = sut.saveFile(multipartFile);
		final File testDataFile = new File(STORAGE_FOLDER + File.separator + savedFile.getName());

		assertTrue(testDataFile.exists());
	}

	@Test
	void historify() throws IOException {
		final File savedFile = sut.saveFile(multipartFile);
		final File testDataFile = new File(STORAGE_FOLDER + File.separator + savedFile.getName());
		final BasicFileAttributes attrs = Files.readAttributes(testDataFile.toPath(), BasicFileAttributes.class);
		sut.saveFile(multipartFile);
        String expectedPathName = STORAGE_FOLDER + File.separator + convertTime(attrs.creationTime().toMillis())
                + "_" + testDataFile.getName();
        assertTrue(new File(expectedPathName).exists());
	}

	private String convertTime(long time) {
		final Date date = new Date(time);
		final Format format = new SimpleDateFormat(StorageImpl.DATETIME_FORMAT);
		return format.format(date);
	}
}