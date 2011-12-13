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
package org.zanata.webtrans.client.ui;

import org.zanata.webtrans.client.presenter.SidePanelPresenter;
import org.zanata.webtrans.client.resources.WebTransMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SidePanel extends Composite implements SidePanelPresenter.Display
{

   private static SidePanelUiBinder uiBinder = GWT.create(SidePanelUiBinder.class);

   interface SidePanelUiBinder extends UiBinder<LayoutPanel, SidePanel>
   {
   }

   private final LayoutPanel validationDetailContainer;


   @UiField
   StackLayoutPanel mainPanel;

   private final LayoutPanel rootPanel;

   private final WebTransMessages messages;

   @Inject
   public SidePanel(WebTransMessages messages)
   {
      validationDetailContainer = new LayoutPanel();

      this.messages = messages;

      rootPanel = uiBinder.createAndBindUi(this);
      initWidget(rootPanel);

      mainPanel.add(validationDetailContainer, messages.validationDetailsHeading(), 20);
   }

   @Override
   public void setValidationDetailView(Widget widget)
   {
      validationDetailContainer.clear();
      validationDetailContainer.add(widget);
   }

   @Override
   public Widget asWidget()
   {
      return this;
   }

}
