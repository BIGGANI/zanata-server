/*
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.webtrans.client.ui;

import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.presenter.client.EventBus;

import org.zanata.webtrans.client.events.ButtonDisplayChangeEvent;
import org.zanata.webtrans.client.events.FilterViewEvent;
import org.zanata.webtrans.client.events.UserConfigChangeEvent;
import org.zanata.webtrans.client.resources.EditorConfigConstants;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 *
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 *
 **/
public class EditorOptionsPanel extends Composite
{
   private static EditorOptionsUiBinder uiBinder = GWT.create(EditorOptionsUiBinder.class);

   interface EditorOptionsUiBinder extends UiBinder<Widget, EditorOptionsPanel>
   {
   }

   @UiField
   VerticalPanel contentPanel;

   @UiField
   Label header, navOptionHeader, filterHeader;

   @UiField
   CheckBox enterChk, escChk, editorButtonsChk;
   
   @UiField
   CheckBox translatedChk, needReviewChk, untranslatedChk;

   @UiField
   ListBox optionsList;

   private final EventBus eventBus;
   private Map<String, Boolean> configMap = new HashMap<String, Boolean>();
   private boolean filterTranslated, filterNeedReview, filterUntranslated;

   private final ValueChangeHandler<Boolean> configChangeHandler = new ValueChangeHandler<Boolean>()
   {
      @Override
      public void onValueChange(ValueChangeEvent<Boolean> event)
      {
         if (event.getSource() == enterChk)
         {
            Log.info("Enable 'Enter' Key to save and move to next string: " + event.getValue());
            configMap.put(EditorConfigConstants.BUTTON_ENTER, event.getValue());
         }
         else if (event.getSource() == escChk)
         {
            Log.info("Enable 'Esc' Key to close editor: " + event.getValue());
            configMap.put(EditorConfigConstants.BUTTON_ESC, event.getValue());
         }
         eventBus.fireEvent(new UserConfigChangeEvent(configMap));
      }
   };

   private final ValueChangeHandler<Boolean> filterChangeHandler = new ValueChangeHandler<Boolean>()
   {
      @Override
      public void onValueChange(ValueChangeEvent<Boolean> event)
      {
         if (event.getSource() == translatedChk)
         {
            filterTranslated = !event.getValue();
         }
         else if (event.getSource() == needReviewChk)
         {
            filterNeedReview = !event.getValue();
         }
         else if (event.getSource() == untranslatedChk)
         {
            filterUntranslated = !event.getValue();
         }
         eventBus.fireEvent(new FilterViewEvent(filterTranslated, filterNeedReview, filterUntranslated));
      }
   };

   public EditorOptionsPanel(final EventBus eventBus)
   {
      this.eventBus = eventBus;
      initWidget(uiBinder.createAndBindUi(this));

      header.setText(EditorConfigConstants.LABEL_EDITOR_OPTIONS);
      enterChk.setText(EditorConfigConstants.LABEL_ENTER_BUTTON_SAVE);
      escChk.setText(EditorConfigConstants.LABEL_ESC_KEY_CLOSE);
      editorButtonsChk.setText(EditorConfigConstants.LABEL_EDITOR_BUTTONS);
      navOptionHeader.setText(EditorConfigConstants.LABEL_NAV_OPTION);

      enterChk.setValue(false);
      escChk.setValue(false);
      editorButtonsChk.setValue(true);

      translatedChk.setText(EditorConfigConstants.LABEL_TRANSLATED);
      needReviewChk.setText(EditorConfigConstants.LABEL_NEED_REVIEW);
      untranslatedChk.setText(EditorConfigConstants.LABEL_UNTRANSLATED);
      filterHeader.setText(EditorConfigConstants.LABEL_FILTERS);

      translatedChk.setValue(true);
      needReviewChk.setValue(true);
      untranslatedChk.setValue(true);
      
      optionsList.addItem(EditorConfigConstants.OPTION_FUZZY_UNTRANSLATED);
      optionsList.addItem(EditorConfigConstants.OPTION_FUZZY);
      optionsList.addItem(EditorConfigConstants.OPTION_UNTRANSLATED);

      optionsList.setSelectedIndex(0);

      configMap.put(EditorConfigConstants.BUTTON_ENTER, false);
      configMap.put(EditorConfigConstants.BUTTON_ESC, false);
      configMap.put(EditorConfigConstants.BUTTON_FUZZY, true);
      configMap.put(EditorConfigConstants.BUTTON_UNTRANSLATED, true);

      enterChk.addValueChangeHandler(configChangeHandler);
      escChk.addValueChangeHandler(configChangeHandler);

      editorButtonsChk.addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {
            Log.info("Show editor buttons: " + event.getValue());
            eventBus.fireEvent(new ButtonDisplayChangeEvent(event.getValue()));
         }
      });

      translatedChk.addValueChangeHandler(filterChangeHandler);
      needReviewChk.addValueChangeHandler(filterChangeHandler);
      untranslatedChk.addValueChangeHandler(filterChangeHandler);

      optionsList.addChangeHandler(new ChangeHandler()
      {
         @Override
         public void onChange(ChangeEvent event)
         {
            String selectedOption = optionsList.getItemText(optionsList.getSelectedIndex());
            if (selectedOption.equals(EditorConfigConstants.OPTION_FUZZY_UNTRANSLATED))
            {
               configMap.put(EditorConfigConstants.BUTTON_UNTRANSLATED, true);
               configMap.put(EditorConfigConstants.BUTTON_FUZZY, true);
            }
            else if (selectedOption.equals(EditorConfigConstants.OPTION_FUZZY))
            {
               configMap.put(EditorConfigConstants.BUTTON_FUZZY, true);
               configMap.put(EditorConfigConstants.BUTTON_UNTRANSLATED, false);
            }
            else if (selectedOption.equals(EditorConfigConstants.OPTION_UNTRANSLATED))
            {
               configMap.put(EditorConfigConstants.BUTTON_FUZZY, false);
               configMap.put(EditorConfigConstants.BUTTON_UNTRANSLATED, true);
            }
            eventBus.fireEvent(new UserConfigChangeEvent(configMap));
         }
      });
   }

   @Override
   public Widget asWidget()
   {
      return this;
   }
}


 