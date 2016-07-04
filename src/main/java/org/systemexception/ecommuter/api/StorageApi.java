package org.systemexception.ecommuter.api;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author leo
 * @date 08/12/15 22:15
 */
@Service
public interface StorageApi {

	/**
	 * Saves file object to disk
	 *
	 * @param file
	 */
	File saveFile(MultipartFile file) throws IOException;
}
