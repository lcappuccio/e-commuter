package org.systemexception.ecommuter.model;

/**
 * @author leo
 * @date 01/07/16 23:53
 */
public class Person {

	private String id, name, lastname;
	private Address homeAddress, workAddress;

	public Person() {}

	public Person (final String personId, final String name, final String lastname, final Address homeAddress, final
	Address workAddress) {
		this.id = personId;
		this.name = name;
		this.lastname = lastname;
		this.homeAddress = homeAddress;
		this.workAddress = workAddress;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(final String lastname) {
		this.lastname = lastname;
	}

	public Address getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(final Address homeAddress) {
		this.homeAddress = homeAddress;
	}

	public Address getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(final Address workAddress) {
		this.workAddress = workAddress;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Person person = (Person) o;

		if (id != null ? !id.equals(person.id) : person.id != null) return false;
		if (name != null ? !name.equals(person.name) : person.name != null) return false;
		if (lastname != null ? !lastname.equals(person.lastname) : person.lastname != null) return false;
		if (homeAddress != null ? !homeAddress.equals(person.homeAddress) : person.homeAddress != null) return false;
		return workAddress != null ? workAddress.equals(person.workAddress) : person.workAddress == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
		result = 31 * result + (homeAddress != null ? homeAddress.hashCode() : 0);
		result = 31 * result + (workAddress != null ? workAddress.hashCode() : 0);
		return result;
	}
}
