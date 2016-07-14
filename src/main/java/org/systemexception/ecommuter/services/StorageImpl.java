package org.systemexception.ecommuter.services;

import org.springframework.web.multipart.MultipartFile;
import org.systemexception.ecommuter.api.LoggerApi;
import org.systemexception.ecommuter.api.StorageApi;
import org.systemexception.ecommuter.pojo.LoggerService;

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

	private final static LoggerApi logger = LoggerService.getFor(StorageImpl.class);
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
		logger.saveFile(receivedFile);
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
		logger.deleteFile(toRemove);
		boolean deleted = toRemove.delete();
		if (deleted) {
			logger.removeFolderOk(folderToRemove);
		} else {
			logger.removeFolderKo(folderToRemove);
		}
	}

	private void createStorageFolder() throws IOException {
		File storageFolderFile = new File(storageFolder);
		if (!storageFolderFile.exists()) {
			Files.createDirectory(storageFolderFile.toPath());
			logger.createStorageFolder(storageFolderFile);
		}
	}

	private void historifyFile(File file) throws IOException {
		if (file.exists()) {
			BasicFileAttributes attrs;
			attrs = Files.readAttributes(file.getAbsoluteFile().toPath(), BasicFileAttributes.class);
			long fileTime = attrs.creationTime().toMillis();
			String historifiedFilename = convertTime(fileTime) + "_" + file.getName();
			file.renameTo(new File(storageFolder + File.separator + historifiedFilename));
			logger.historiFyFile(file, historifiedFilename);
		}
	}

	private String convertTime(long time) {
		Date date = new Date(time);

		Format format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		return format.format(date);
	}
}
