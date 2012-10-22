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

import org.zanata.webtrans.client.resources.Resources;
import org.zanata.webtrans.client.resources.WebTransMessages;
import org.zanata.webtrans.client.ui.HasPager;
import org.zanata.webtrans.client.ui.Pager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TranslationEditorView extends Composite implements TranslationEditorDisplay
{

   private static TranslationEditorViewUiBinder uiBinder = GWT.create(TranslationEditorViewUiBinder.class);

   interface TranslationEditorViewUiBinder extends UiBinder<Widget, TranslationEditorView>
   {
   }

   @UiField
   HTMLPanel transUnitNavigationContainer;

   @UiField
   LayoutPanel editor;

   @UiField(provided = true)
   Pager pager;

   @UiField(provided = true)
   Resources resources;

   @UiField
   HTMLPanel filterPanelContainer;
   
   @UiField
   InlineLabel refreshCurrentPage;

   private Listener listener;

   @Inject
   public TranslationEditorView(final WebTransMessages messages, final Resources resources)
   {
      this.resources = resources;
      this.pager = new Pager(messages, resources);

      initWidget(uiBinder.createAndBindUi(this));
      
      refreshCurrentPage.setTitle(messages.refreshCurrentPage());
   }

   @Override
   public void setEditorView(Widget editor)
   {
      this.editor.clear();
      this.editor.add(editor);

   }

   @Override
   public void setTransUnitNavigation(Widget navigationWidget)
   {
      transUnitNavigationContainer.clear();
      transUnitNavigationContainer.add(navigationWidget);
   }

   // @Override
   // public void setUndoRedo(Widget undoRedoWidget)
   // {
   // undoRedoContainer.clear();
   // undoRedoContainer.add(undoRedoWidget);
   // }

   @Override
   public Widget asWidget()
   {
      return this;
   }

   @Override
   public HasPager getPageNavigation()
   {
      return pager;
   }

   @Override
   public void setFilterView(Widget filterView)
   {
      filterPanelContainer.clear();
      filterPanelContainer.add(filterView);
   }

   @Override
   public boolean isPagerFocused()
   {
      return pager.isFocused();
   }
   
   @UiHandler("refreshCurrentPage")
   public void onRedrawCurrentPageClicked(ClickEvent event)
   {
      listener.refreshCurrentPage();
   }

   public void setListener(Listener listener)
   {
      this.listener = listener;
   }

}
