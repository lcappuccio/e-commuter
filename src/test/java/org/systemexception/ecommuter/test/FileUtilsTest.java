package org.systemexception.ecommuter.test;

import org.junit.Test;
import org.systemexception.ecommuter.pojo.FileUtils;

import java.io.File;

import static junit.framework.TestCase.assertFalse;

/**
 * @author cappuccio
 * @date 07/07/16 10:38.
 */
public class FileUtilsTest {

	@Test
	public void removes_folder() {
		File toRemove = new File("TEST_FOLDER_TO_REMOVE");
		FileUtils.removeFolder(toRemove.getAbsolutePath());
		assertFalse(toRemove.exists());
	}

}