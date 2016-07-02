package org.systemexception.ecommuter.model;

/**
 * @author leo
 * @date 01/07/16 23:53
 */
public class Person {

	private String name, surname;
	private Address address;

	public Person() {}

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Person person = (Person) o;

		if (name != null ? !name.equals(person.name) : person.name != null) return false;
		if (surname != null ? !surname.equals(person.surname) : person.surname != null) return false;
		return address != null ? address.equals(person.address) : person.address == null;

	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (surname != null ? surname.hashCode() : 0);
		result = 31 * result + (address != null ? address.hashCode() : 0);
		return result;
	}
}
