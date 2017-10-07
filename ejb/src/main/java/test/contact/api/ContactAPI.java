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

import javax.ejb.Local;

import test.contact.model.Contact;

/**
 *
 * @author Filippo Diotalevi
 */
@Local
public interface ContactAPI {

	public void reindex();
    /**
     * Returns the currently available contacts
     *
     * @return every contact in the database
     */
    public List<ContactTO> getContacts();

    /**
     * search for contacts either using hibernate or hibernate search.
     * @param fields
     * @param query
     * @param useFti
     * @return list of transfer objects
     */
    public List<ContactTO> searchContacts(List<Contact.EntityField> fields, String query, Boolean useFti);
    
    
    /**
     * Returns a specific Contact from DB
     *
     * @param id The Id for the Contact
     * @return The specified Contact object
     */
    public ContactTO getContact(Long id);

    /**
     * Persist a new Contact in the DB
     *
     * @param name The name of the new Contact
     * @param email The e-mail address of the new Contact
     */
    public void addContact(String name, String email);


	public void addContact(ContactTO c);
    /**
     * Edit an existing Contact in the DB
     *
     * @param c The Contact to save
     */
    public void saveContact(ContactTO c);

    /**
     * Removes a specific item from the DB
     *
     * @param id of the specific Contact object, which we wants to remove
     */
    public void remove(Long id);


}
