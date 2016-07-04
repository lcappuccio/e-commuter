package org.systemexception.ecommuter.enums;

/**
 * @author leo
 * @date 03/07/16 22:04
 */
public class Endpoints {

	public static final String CONTEXT = "/ecommuter";
	public static final String ADD_TERRITORIES = "/addterritories";
	// PERSON
	public static final String PERSON = "/person";
	public static final String PERSON_ADD = "/add";
	public static final String PERSON_DELETE = "/delete";
	public static final String PERSON_UPDATE = "/update";
	public static final String PERSON_NEARBY = "/nearby";
	// ADDRESS
	public static final String ADDRESS = "/address";
	public static final String GEO_TO_ADDRESS = "/fromgeo";
	public static final String ADDRESS_TO_GEO = "/togeo";
	// PARAMETERS
	public static final String FILE_TO_UPLOAD = "fileToUpload";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
}
