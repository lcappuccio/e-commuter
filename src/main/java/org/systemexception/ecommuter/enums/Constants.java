package org.systemexception.ecommuter.enums;

/**
 * @author leo
 * @date 03/07/16 15:41
 */
public enum Constants {

	LOG_SEPARATOR(",");

	private final String aString;

	Constants(String aString) {
		this.aString = aString;
	}

	@Override
	public String toString() {
		return aString;
	}
}