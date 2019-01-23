package org.systemexception.ecommuter.enums;

/**
 * @author leo
 * @date 03/07/16 15:41
 */
public enum Constants {

	LOG_OBJECT_SEPARATOR("|"),
	LOG_ITEM_SEPARATOR(",");

	private final String aString;

	Constants(final String aString) {
		this.aString = aString;
	}

	@Override
	public String toString() {
		return aString;
	}
}
