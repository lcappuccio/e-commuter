package org.systemexception.ecommuter.pojo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author cappuccio
 * @date 07/07/16 10:23.
 */
public class FileUtils {

	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

	public static void removeFolder(String folderToRemove) {
		File toRemove = new File(folderToRemove);
		if (toRemove.exists()) {
			String[] files = toRemove.list();
			for (String file : files) {
				new File(folderToRemove + File.separator + file).delete();
			}
		}
		boolean deleted = toRemove.delete();
		if (deleted) {
			logger.info("RemoveFolder: " + folderToRemove + " deleted");
		} else {
			logger.error("RemoveFolder: " + folderToRemove + " not deleted");
		}
	}
}
