package org.systemexception.ecommuter.model;

/**
 * @author leo
 * @date 01/07/16 23:53
 */
public class Person {

	private String name, surname;
	private Address address;

	public Person (final String name, final String surname, final Address address) {
		this.name = name;
		this.surname = surname;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}
