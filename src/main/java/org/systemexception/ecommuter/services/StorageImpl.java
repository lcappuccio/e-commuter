package org.systemexception.ecommuter.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.api.StorageApi;
import org.systemexception.ecommuter.enums.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author leo
 * @date 03/07/16 23:02
 */
public class StorageImpl implements StorageApi {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String storageFolder;

	public StorageImpl(final String storageFolder) throws IOException {
		this.storageFolder = storageFolder;
		createStorageFolder();
	}

	@Override
	public File saveFile(MultipartFile receivedFile) throws IOException {
		File dataFile = new File(storageFolder + File.separator + receivedFile.getName());
		historifyFile(dataFile);
		dataFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(dataFile);
		fos.write(receivedFile.getBytes());
		fos.close();
		logger.info("SaveFile: " + receivedFile.getName());
		return dataFile;
	}

	private void createStorageFolder() throws IOException {
		File storageFolderFile = new File(storageFolder);
		if (!storageFolderFile.exists()) {
			Files.createDirectory(storageFolderFile.toPath());
			logger.info("CreateStorageFolder: " + storageFolder);
		}
	}

	private void historifyFile(File file) throws IOException {
		if (file.exists()) {
			BasicFileAttributes attrs;
			attrs = Files.readAttributes(file.getAbsoluteFile().toPath(), BasicFileAttributes.class);
			long fileTime = attrs.creationTime().toMillis();
			String historifiedFilename = File.separator + convertTime(fileTime) + "_" + file.getName();
			file.renameTo(new File(storageFolder + File.separator + historifiedFilename));
			logger.info("HistorifyFile: " + file.getName() + Constants.LOG_SEPARATOR + historifiedFilename);
		}
	}

	private String convertTime(long time) {
		Date date = new Date(time);
		Format format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(date);
	}
}
