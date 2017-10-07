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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import test.contact.api.ContactAPI;
import test.contact.api.ContactTO;
import test.contact.model.Contact;

/**
 * Dynamic behavior for the ListContact page
 *
 * @author Filippo Diotalevi
 */
@SuppressWarnings("serial")
public class ListContacts extends WebPage {

	// Inject the ContactAPI using @Inject
	@Inject
	private ContactAPI contactApi;

	@Resource(name = "welcomeMessage")
	private String welcome;
	
	private String queryString = "";
	private Model<String> queryModel = Model.of(queryString);
	private Model<Boolean> ftiModel = Model.of(Boolean.FALSE);
	private List<IModel<ContactTO>> contactListModel = new ArrayList<>();
	private WebMarkupContainer listContainer;
	
	// Set up the dynamic behavior for the page, widgets bound by id
	public ListContacts() {
		
	}

	@Override
	protected void onInitialize() {

		super.onInitialize();
		System.out.println("onInitialize");
		// Add the dynamic welcome message, specified in web.xml
		add(new Label("welcomeMessage", welcome));
		
		buildSearchField(this);
		
		// Populate the table of contacts
		for (ContactTO contact : contactApi.getContacts()) {
			contactListModel.add(Model.of(contact));
		}

		listContainer = new WebMarkupContainer("listcontainer");
		listContainer.setOutputMarkupId(true);
		listContainer.add(getRefreshingView());
		add(listContainer);
	}

	private void buildSearchField(WebPage page) {
		System.out.println("adding search");
		

		page.add(new AjaxCheckBox("fti", ftiModel){
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				System.out.println("use fti: " + ftiModel.getObject());
			}
		});
		
		TextField<String> queryField = new TextField<String>("queryParam", queryModel);
		// not in form, so add ajax behavior
		queryField.add(new OnChangeAjaxBehavior() {

			private static final long serialVersionUID = 2462233190993745889L;

			@SuppressWarnings("unchecked")
			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				System.out.println(((TextField<String>) getComponent()).getModelObject());
			}
		});

		page.add(queryField);

		
		// not in form, so use ajax link
		page.add(new AjaxLink<Object>("search") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				System.out.println("searching...");
				List<Contact.EntityField> searchFields = new ArrayList<>();
				searchFields.add(Contact.EntityField.name);
				searchFields.add(Contact.EntityField.email);
				
				if (queryModel.getObject() == null || queryModel.getObject().isEmpty())
					queryModel.setObject("%");
					
				contactListModel.clear();
				
				for (ContactTO contact : contactApi.searchContacts(searchFields, queryModel.getObject(), ftiModel.getObject())) {
					contactListModel.add(Model.of(contact));
				}
				// add the list container to the target to refresh it (adding repeater directly not possible)
				target.add(listContainer); 
			}
		});
		
		page.add(new AjaxLink<Object>("reindex") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				System.out.println("rebuilding...");
				contactApi.reindex();
			}
		});
	}

	private RefreshingView<ContactTO> getRefreshingView() {
		return new RefreshingView<ContactTO>("contacts") {

			@Override
			protected Iterator<IModel<ContactTO>> getItemModels() {
				System.out.println("iterator list size: " + contactListModel.size());
				return contactListModel.iterator();
			}

			@Override
			protected void populateItem(final Item<ContactTO> item) {
				ContactTO contact = item.getModelObject();
				item.add(new Label("name", contact.getName()));
				item.add(new Label("email", contact.getEmail()));
				item.add(new Link<ContactTO>("edit", item.getModel()) {

					// Add a click handler
					@Override
					public void onClick() {
						PageParameters pageParameters = new PageParameters();
						pageParameters.add("id", item.getModelObject().getId());
						setResponsePage(EditContact.class, pageParameters);
					}
				});
				item.add(new Link<ContactTO>("delete", item.getModel()) {

					// Add a click handler
					@Override
					public void onClick() {
						contactApi.remove(item.getModelObject().getId());
						setResponsePage(ListContacts.class);
					}
				});
			}
		};
		
	}
}
