package org.systemexception.ecommuter.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.enums.Constants;

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
		final File dataFile = new File(storageFolder + File.separator + convertTime(System.currentTimeMillis()) + "_" +
				receivedFile.getOriginalFilename());
		historifyFile(dataFile);
        boolean isFileCreated = dataFile.createNewFile();
        if (isFileCreated) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(dataFile)) {
                fileOutputStream.write(receivedFile.getBytes());
                LOGGER.info("saveFile {}{}", Constants.LOG_OBJECT_SEPARATOR, receivedFile.getName());
                return dataFile;
            }
        } else {
            LOGGER.error("error creating file {}", dataFile.getAbsolutePath());
            throw new IOException();
        }
	}

	public void removeFolder(final String folderPath) throws IOException {
		final File toRemove = new File(folderPath);
		if (toRemove.exists()) {
			final String[] files = toRemove.list();
			for (final String file : files != null ? files : new String[0]) {
                String fileNameToDelete = folderPath + File.separator + file;
                boolean deleted = new File(fileNameToDelete).delete();
                if (deleted) {
                    LOGGER.info("deletedFile {}", fileNameToDelete);
                }
            }
		}
		LOGGER.info("deleteFile {}", toRemove.getName());
		final boolean deleted = toRemove.delete();
		if (deleted) {
			LOGGER.info("removeFolderOk {}", folderPath);
		} else {
			LOGGER.error("removeFolderKo {}", folderPath);
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
		if (file.exists()) {
			final BasicFileAttributes attrs = Files.readAttributes(file.getAbsoluteFile().toPath(),
					BasicFileAttributes.class);
			final long fileTime = attrs.creationTime().toMillis();
			final String historifiedFilename = convertTime(fileTime) + "_" + file.getName();
            final File historifiedFilePath = new File(storageFolder + File.separator + historifiedFilename);
            boolean isFileRenamed = file.renameTo(historifiedFilePath);
            if (!isFileRenamed) {
                LOGGER.error("error renaming file {} to {}", file.getAbsolutePath(), historifiedFilePath.getAbsolutePath());
            }
            LOGGER.info("historiFyFile {} as {}", file.getName(), historifiedFilename);
		}
	}

	private String convertTime(final long time) {
		final Date date = new Date(time);
		final Format format = new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());
		return format.format(date);
	}
}
