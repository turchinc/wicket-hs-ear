package test.contact.api;

import java.io.Serializable;

import test.contact.model.Contact;

public class ContactTO implements Serializable {

	private static final long serialVersionUID = 2102939872731282292L;
	private Long id = -1L;
	private String name;
	private String email;

	public ContactTO() {
	}

	public ContactTO(Contact contact) {
		this.id = contact.getId();
		this.name = contact.getName();
		this.email = contact.getEmail();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
}