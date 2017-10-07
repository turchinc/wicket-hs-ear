/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.contact.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import test.contact.model.Contact;
import test.contact.model.Contact.EntityField;

/**
 * A bean which manages Contact entities.
 */
@Stateless
public class ContactAPIImpl implements ContactAPI {

	@PersistenceContext
	private EntityManager em;

	@Override
	@SuppressWarnings("unchecked")
	public List<ContactTO> getContacts() {
		return (List<ContactTO>) em.createQuery("SELECT c FROM Contact c").getResultList().stream()
				.map(result -> new ContactTO((Contact) result)).collect(Collectors.toList());
	}

	@Override
	public List<ContactTO> searchContacts(List<EntityField> fields, String query, Boolean useFti) {
		return useFti ? searchContactsFti(fields, query) : searchContacts(fields, query);
	}


	/**
	 * @see ContactAPI#searchContacts(List, String, Boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<ContactTO> searchContactsFti(List<EntityField> fields, String query) {

		FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
		//em.getTransaction().begin();

		// http://hibernate.org/search/documentation/getting-started/
		QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Contact.class)
				//https://stackoverflow.com/questions/44028095/hibernate-search-to-find-partial-matches-of-a-phrase
			    .overridesForField(Contact.EntityField.name.toString(), Contact.CONTACT_ANALYZER )
			    .overridesForField(Contact.EntityField.email.toString(), Contact.CONTACT_ANALYZER)
				.get();
		List<String> searchFields = fields.stream().map( f -> f.toString()).collect(Collectors.toList());;
		org.apache.lucene.search.Query luceneQuery = qb.keyword().onFields(searchFields.toArray(new String[0]) ).matching(query)
				.createQuery();

		// wrap Lucene query in a javax.persistence.Query
		javax.persistence.Query q = fullTextEntityManager.createFullTextQuery(luceneQuery, Contact.class);

		// execute search
		List<Contact> results = q.getResultList();

//		em.getTransaction().commit();
//		em.close();
		return results.stream().map(result -> new ContactTO(result)).collect(Collectors.toList());
	}


	/**
	 * @see ContactAPI#searchContacts(List, String, Boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<ContactTO> searchContacts(List<Contact.EntityField> fields, String queryParam) {

		StringBuilder queryString = new StringBuilder();
		queryString.append(" from Contact c where ");
		int count = 0;
		for (EntityField f : fields) {
			if (count++ > 0)
				queryString.append(" OR ");
			queryString.append(f.toString());
			queryString.append(" LIKE :");
			queryString.append(f.toString());
		}

		Query q = em.createQuery(queryString.toString(), Contact.class);

		for (EntityField f : fields)
			q.setParameter(f.toString(), queryParam);
		System.out.println(queryString.toString());
		System.out.println(q.toString());

		List<Contact> results = q.getResultList();
		return results.stream().map(result -> new ContactTO(result)).collect(Collectors.toList());
	}

	/**
	 * Get Contact by ID.
	 */
	@Override
	public ContactTO getContact(Long id) {
		return new ContactTO(em.find(Contact.class, id));
	}

	/**
	 * Add a new Contact.
	 */
	@Override
	public void addContact(String name, String email) {
		em.merge(new Contact(null, name, email));
	}

	/**
	 * Add a new Contact.
	 */
	@Override
	public void addContact(ContactTO c) {
		em.merge(new Contact(null, c.getName(), c.getEmail()));
	}

	/**
	 * Save a new Contact.
	 */
	@Override
	public void saveContact(ContactTO c) {
		Contact managed = em.find(Contact.class, c.getId());
		managed.setEmail(c.getEmail());
		managed.setName(c.getName());
		em.merge(managed);
	}

	/**
	 * Remove a Contact.
	 */
	@Override
	public void remove(Long id) {
		Contact managed = em.find(Contact.class, id);
		em.remove(managed);
		em.flush();
	}

	@Override
	public void reindex() {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
		try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
