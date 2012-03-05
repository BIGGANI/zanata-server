/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.webtrans.client.view;

import net.customware.gwt.presenter.client.EventBus;

import org.zanata.webtrans.client.presenter.DocumentListPresenter;
import org.zanata.webtrans.client.resources.Resources;
import org.zanata.webtrans.client.resources.UiMessages;
import org.zanata.webtrans.client.resources.WebTransMessages;
import org.zanata.webtrans.client.rpc.CachingDispatchAsync;
import org.zanata.webtrans.client.ui.ClearableTextBox;
import org.zanata.webtrans.client.ui.DocumentListTable;
import org.zanata.webtrans.client.ui.DocumentNode;
import org.zanata.webtrans.shared.model.DocumentInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Inject;

public class DocumentListView extends Composite implements DocumentListPresenter.Display, HasSelectionHandlers<DocumentInfo>
{

   private static DocumentListViewUiBinder uiBinder = GWT.create(DocumentListViewUiBinder.class);

   interface DocumentListViewUiBinder extends UiBinder<LayoutPanel, DocumentListView>
   {
   }

   @UiField
   ScrollPanel documentScrollPanel;

   @UiField(provided = true)
   ClearableTextBox filterTextBox;

   @UiField
   CheckBox exactSearchCheckBox;

   CellTable<DocumentNode> documentListTable;

   private final Resources resources;
   private final WebTransMessages messages;
   
   private ListDataProvider<DocumentNode> dataProvider;

   @Inject
   public DocumentListView(Resources resources, WebTransMessages messages, UiMessages uiMessages, final CachingDispatchAsync dispatcher, EventBus eventBus)
   {
      this.resources = resources;
      this.messages = messages;
      
      filterTextBox = new ClearableTextBox(resources, uiMessages);
      // TODO set this from the presenter if possible
      dataProvider = new ListDataProvider<DocumentNode>();

      initWidget(uiBinder.createAndBindUi(this));
   }

   @Override
   public Widget asWidget()
   {
      return this;
   }

   @Override
   public HasData<DocumentNode> getDocumentListTable()
   {
      return documentListTable;
   }

   @Override
   public HasValue<String> getFilterTextBox()
   {
      return filterTextBox.getTextBox();
   }

   @Override
   public HasSelectionHandlers<DocumentInfo> getDocumentList()
   {
      return this;
   }

   @Override
   public HandlerRegistration addSelectionHandler(SelectionHandler<DocumentInfo> handler)
   {
      return addHandler(handler, SelectionEvent.getType());
   }


   @Override
   public void setPageSize(int pageSize)
   {
      documentListTable.setPageSize(pageSize);
   }

   @Override
   public ListDataProvider<DocumentNode> getDataProvider()
   {
      return dataProvider;
   }

   @Override
   public HasValue<Boolean> getExactSearchCheckbox()
   {
      return exactSearchCheckBox;
   }
   
   @Override
   public void renderTable()
   {
      // documentListTable = DocumentListTable.initDocumentListTable(this,
      // resources, messages, dataProvider);
      documentListTable = new DocumentListTable(this, resources, messages, dataProvider);
      dataProvider.addDataDisplay(documentListTable);

      documentScrollPanel.clear();
      documentScrollPanel.add(documentListTable);
   }
}