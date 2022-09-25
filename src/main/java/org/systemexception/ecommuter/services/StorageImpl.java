package org.systemexception.ecommuter.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.enums.Constants;
import org.systemexception.ecommuter.pojo.UserDataSantizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author leo
 * @date 03/07/16 23:02
 */
public class StorageImpl implements StorageApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(StorageImpl.class);
	private final String storageFolder;

	static final String DATETIME_FORMAT = "yyyyMMddHHmmss";

	public StorageImpl(final String storageFolder) throws IOException {
		this.storageFolder = storageFolder;
		createStorageFolder();
	}

	@Override
	public File saveFile(final MultipartFile receivedFile) throws IOException {
        String originalFilename = receivedFile.getOriginalFilename();
        if (StringUtils.isEmpty(originalFilename)) {
            throw new IllegalArgumentException();
        }
        final String santizedFileName = UserDataSantizer.returnAsSafe(originalFilename);
		final File dataFile = new File(storageFolder + File.separator + convertTime(System.currentTimeMillis()) + "_" +
                santizedFileName);
		historifyFile(dataFile);
        boolean isFileCreated = dataFile.createNewFile();
        if (isFileCreated) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(dataFile)) {
                fileOutputStream.write(receivedFile.getBytes());
                LOGGER.info("saveFile {}{}", Constants.LOG_OBJECT_SEPARATOR, santizedFileName);
                return dataFile;
            }
        } else {
            LOGGER.error("error creating file {}", dataFile.getAbsolutePath());
            throw new IOException();
        }
	}

	private void createStorageFolder() throws IOException {
		final File storageFolderFile = new File(storageFolder);
		if (!storageFolderFile.exists()) {
			Files.createDirectory(storageFolderFile.toPath());
			LOGGER.info("createStorageFolder {}{}", Constants.LOG_OBJECT_SEPARATOR, storageFolderFile.getName());
		}
	}

	private void historifyFile(final File file) throws IOException {
        final String sanitiziedFilePath = UserDataSantizer.returnAsSafe(file.getAbsolutePath());
        final File sanitizedFile = new File(sanitiziedFilePath);
		if (sanitizedFile.exists()) {
			final BasicFileAttributes attrs = Files.readAttributes(sanitizedFile.getAbsoluteFile().toPath(),
					BasicFileAttributes.class);
			final long fileTime = attrs.creationTime().toMillis();
			final String historifiedFilename = convertTime(fileTime) + "_" + sanitizedFile.getName();
            final File historifiedFilePath = new File(storageFolder + File.separator + historifiedFilename);
            boolean isFileRenamed = sanitizedFile.renameTo(historifiedFilePath);
            if (!isFileRenamed) {
                LOGGER.error("error renaming file {} to {}", sanitizedFile.getAbsolutePath(), historifiedFilePath.getAbsolutePath());
            }
            LOGGER.info("historiFyFile {} as {}", sanitizedFile.getName(), historifiedFilename);
		}
	}

	private String convertTime(final long time) {
		final Date date = new Date(time);
		final Format format = new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());
		return format.format(date);
	}
}
