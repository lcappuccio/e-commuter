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
import java.util.Locale;

/**
 * @author leo
 * @date 03/07/16 23:02
 */
public class StorageImpl implements StorageApi {

	private final static Logger logger = LoggerFactory.getLogger(StorageImpl.class);
	private final String storageFolder;

	public StorageImpl(final String storageFolder) throws IOException {
		this.storageFolder = storageFolder;
		createStorageFolder();
	}

	@Override
	public File saveFile(MultipartFile receivedFile) throws IOException {
		File dataFile = new File(storageFolder + File.separator + convertTime(System.currentTimeMillis()) + "_" +
				receivedFile.getOriginalFilename());
		historifyFile(dataFile);
		dataFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(dataFile);
		fos.write(receivedFile.getBytes());
		fos.close();
		logger.info("saveFile" + Constants.LOG_OBJECT_SEPARATOR + receivedFile.getName());
		return dataFile;
	}

	public static void removeFolder(String folderToRemove) {
		File toRemove = new File(folderToRemove);
		if (toRemove.exists()) {
			String[] files = toRemove.list();
			for (String file : files) {
				new File(folderToRemove + File.separator + file).delete();
			}
		}
		logger.info("deleteFile" + Constants.LOG_OBJECT_SEPARATOR + toRemove.getName());
		boolean deleted = toRemove.delete();
		if (deleted) {
			logger.info("removeFolderOk" + Constants.LOG_OBJECT_SEPARATOR + folderToRemove);
		} else {
			logger.error("removeFolderKo" + Constants.LOG_OBJECT_SEPARATOR + folderToRemove);
		}
	}

	private void createStorageFolder() throws IOException {
		File storageFolderFile = new File(storageFolder);
		if (!storageFolderFile.exists()) {
			Files.createDirectory(storageFolderFile.toPath());
			logger.info("createStorageFolder" + Constants.LOG_OBJECT_SEPARATOR + storageFolderFile.getName());
		}
	}

	private void historifyFile(File file) throws IOException {
		if (file.exists()) {
			BasicFileAttributes attrs;
			attrs = Files.readAttributes(file.getAbsoluteFile().toPath(), BasicFileAttributes.class);
			long fileTime = attrs.creationTime().toMillis();
			String historifiedFilename = convertTime(fileTime) + "_" + file.getName();
			file.renameTo(new File(storageFolder + File.separator + historifiedFilename));
			logger.info("historiFyFile" + Constants.LOG_OBJECT_SEPARATOR + file.getName() +
					Constants.LOG_ITEM_SEPARATOR + historifiedFilename);
		}
	}

	private String convertTime(long time) {
		Date date = new Date(time);

		Format format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		return format.format(date);
	}
}
