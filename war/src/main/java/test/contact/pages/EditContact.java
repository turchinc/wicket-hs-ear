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
package test.contact.pages;

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import test.contact.api.ContactAPI;
import test.contact.api.ContactTO;

@SuppressWarnings("serial")
public class EditContact extends WebPage {

	private Form<ContactTO> insertForm;
	private IModel<ContactTO> contactModel = new Model<ContactTO>(new ContactTO());
	private long contactId = -1;

	@Inject
	private ContactAPI contactApi;


	public EditContact() {

	}
	
	public EditContact(PageParameters pageParameters) {
		contactId = pageParameters.get("id").toLong();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		if (contactId > 0)
			contactModel = new Model<ContactTO>(contactApi.getContact(contactId));

		add(new FeedbackPanel("feedback"));

		insertForm = new Form<ContactTO>("insertForm") {

			@Override
			protected void onSubmit() {

				if (contactModel.getObject().getId() > 0) {
					contactApi.saveContact(contactModel.getObject());
				} else {
					contactApi.addContact(contactModel.getObject());
				}

				setResponsePage(ListContacts.class);
			}
		};

		insertForm.add(new RequiredTextField<>("name", new PropertyModel<String>(contactModel, "name")));
		insertForm.add(new RequiredTextField<>("email", new PropertyModel<String>(contactModel, "email")));
		insertForm.add(new Button("save", new Model<>(contactModel.getObject().getId() > 0 ? "Save" : "Insert")));
		add(insertForm);
	}

}
