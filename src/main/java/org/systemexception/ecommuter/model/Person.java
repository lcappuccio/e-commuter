package org.systemexception.ecommuter.model;

/**
 * @author leo
 * @date 01/07/16 23:53
 */
public class Person {

	private String id, name, surname;
	private Address homeAddress, workAddress;

	public Person() {}

	public Person (final String personId, final String name, final String surname, final Address homeAddress, final
	Address workAddress) {
		this.id = personId;
		this.name = name;
		this.surname = surname;
		this.homeAddress = homeAddress;
		this.workAddress = workAddress;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Address getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(Address homeAddress) {
		this.homeAddress = homeAddress;
	}

	public Address getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(Address workAddress) {
		this.workAddress = workAddress;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Person person = (Person) o;

		if (id != null ? !id.equals(person.id) : person.id != null) return false;
		if (name != null ? !name.equals(person.name) : person.name != null) return false;
		if (surname != null ? !surname.equals(person.surname) : person.surname != null) return false;
		if (homeAddress != null ? !homeAddress.equals(person.homeAddress) : person.homeAddress != null) return false;
		return workAddress != null ? workAddress.equals(person.workAddress) : person.workAddress == null;

	}
}
