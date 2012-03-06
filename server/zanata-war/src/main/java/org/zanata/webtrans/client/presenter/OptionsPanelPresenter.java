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
package org.zanata.webtrans.client.presenter;

import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import org.zanata.webtrans.client.events.ButtonDisplayChangeEvent;
import org.zanata.webtrans.client.events.FilterViewEvent;
import org.zanata.webtrans.client.events.FilterViewEventHandler;
import org.zanata.webtrans.client.events.UserConfigChangeEvent;
import org.zanata.webtrans.client.events.WorkspaceContextUpdateEvent;
import org.zanata.webtrans.client.events.WorkspaceContextUpdateEventHandler;
import org.zanata.webtrans.client.resources.EditorConfigConstants;
import org.zanata.webtrans.shared.model.WorkspaceContext;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;

public class OptionsPanelPresenter extends WidgetPresenter<OptionsPanelPresenter.Display>
{
   public interface Display extends WidgetDisplay
   {

      HasValue<Boolean> getTranslatedChk();

      HasValue<Boolean> getNeedReviewChk();

      HasValue<Boolean> getUntranslatedChk();

      HasValue<Boolean> getEditorButtonsChk();

      HasValue<Boolean> getEnterChk();

      HasValue<Boolean> getEscChk();

      void setEditorOptionsVisible(boolean visible);

      void setNavOptionVisible(boolean visible);

      void setValidationOptionsVisible(boolean visible);

      HasChangeHandlers getFilterOptionsSelect();

      // possible filter values
      static final String KEY_FUZZY_UNTRANSLATED = "FU";
      static final String KEY_FUZZY = "F";
      static final String KEY_UNTRANSLATED = "U";

      String getSelectedFilter();
   }

   private final ValidationOptionsPresenter validationOptionsPresenter;

   private Map<String, Boolean> configMap = new HashMap<String, Boolean>();
   private final WorkspaceContext workspaceContext;

   @Inject
   public OptionsPanelPresenter(final Display display, final EventBus eventBus, final ValidationOptionsPresenter validationDetailsPresenter, final WorkspaceContext workspaceContext)
   {
      super(display, eventBus);
      this.validationOptionsPresenter = validationDetailsPresenter;
      this.workspaceContext = workspaceContext;

      configMap.put(EditorConfigConstants.BUTTON_ENTER, false);
      configMap.put(EditorConfigConstants.BUTTON_ESC, false);
      configMap.put(EditorConfigConstants.BUTTON_FUZZY, true);
      configMap.put(EditorConfigConstants.BUTTON_UNTRANSLATED, true);
   }

   private final ValueChangeHandler<Boolean> filterChangeHandler = new ValueChangeHandler<Boolean>()
   {
      @Override
      public void onValueChange(ValueChangeEvent<Boolean> event)
      {
         eventBus.fireEvent(new FilterViewEvent(display.getTranslatedChk().getValue(), display.getNeedReviewChk().getValue(), display.getUntranslatedChk().getValue(), false));
      }
   };

   @Override
   protected void onBind()
   {
      validationOptionsPresenter.bind();
      if (workspaceContext.isReadOnly())
      {
         setReadOnly(true);
      }

      registerHandler(display.getTranslatedChk().addValueChangeHandler(filterChangeHandler));
      registerHandler(display.getNeedReviewChk().addValueChangeHandler(filterChangeHandler));
      registerHandler(display.getUntranslatedChk().addValueChangeHandler(filterChangeHandler));

      registerHandler(eventBus.addHandler(FilterViewEvent.getType(), new FilterViewEventHandler()
      {
         @Override
         public void onFilterView(FilterViewEvent event)
         {
            if (event.isCancelFilter())
            {
               display.getTranslatedChk().setValue(event.isFilterTranslated(), false);
               display.getNeedReviewChk().setValue(event.isFilterNeedReview(), false);
               display.getUntranslatedChk().setValue(event.isFilterUntranslated(), false);
            }

            // if filter view, hide modal navigation
            boolean showingFullList = (event.isFilterTranslated() == event.isFilterNeedReview()) && (event.isFilterTranslated() == event.isFilterUntranslated());
            if (showingFullList)
            {
               display.setNavOptionVisible(true);
            }
            else
            {
               display.setNavOptionVisible(false);
            }
         }
      }));

      registerHandler(display.getEditorButtonsChk().addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {
            Log.info("Show editor buttons: " + event.getValue());
            eventBus.fireEvent(new ButtonDisplayChangeEvent(event.getValue()));
         }
      }));

      registerHandler(display.getEnterChk().addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {
            Log.info("Enable 'Enter' Key to save and move to next string: " + event.getValue());
            configMap.put(EditorConfigConstants.BUTTON_ENTER, event.getValue());
            eventBus.fireEvent(new UserConfigChangeEvent(configMap));
         }
      }));

      registerHandler(display.getEscChk().addValueChangeHandler(new ValueChangeHandler<Boolean>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<Boolean> event)
         {
            Log.info("Enable 'Esc' Key to close editor: " + event.getValue());
            configMap.put(EditorConfigConstants.BUTTON_ESC, event.getValue());
            eventBus.fireEvent(new UserConfigChangeEvent(configMap));
         }
      }));

      // editor buttons always shown by default
      display.getEditorButtonsChk().setValue(true, false);
      display.getEnterChk().setValue(configMap.get(EditorConfigConstants.BUTTON_ENTER), false);
      display.getEscChk().setValue(configMap.get(EditorConfigConstants.BUTTON_ESC), false);

      registerHandler(display.getFilterOptionsSelect().addChangeHandler(new ChangeHandler()
      {
         @Override
         public void onChange(ChangeEvent event)
         {
            String selectedOption = display.getSelectedFilter();
            if (selectedOption.equals(Display.KEY_FUZZY_UNTRANSLATED))
            {
               configMap.put(EditorConfigConstants.BUTTON_UNTRANSLATED, true);
               configMap.put(EditorConfigConstants.BUTTON_FUZZY, true);
            }
            else if (selectedOption.equals(Display.KEY_FUZZY))
            {
               configMap.put(EditorConfigConstants.BUTTON_FUZZY, true);
               configMap.put(EditorConfigConstants.BUTTON_UNTRANSLATED, false);
            }
            else if (selectedOption.equals(Display.KEY_UNTRANSLATED))
            {
               configMap.put(EditorConfigConstants.BUTTON_FUZZY, false);
               configMap.put(EditorConfigConstants.BUTTON_UNTRANSLATED, true);
            }
            eventBus.fireEvent(new UserConfigChangeEvent(configMap));
         }
      }));

      registerHandler(eventBus.addHandler(WorkspaceContextUpdateEvent.getType(), new WorkspaceContextUpdateEventHandler()
      {
         @Override
         public void onWorkspaceContextUpdated(WorkspaceContextUpdateEvent event)
         {
            setReadOnly(event.isReadOnly());
         }
      }));
   }

   void setReadOnly(boolean readOnly)
   {
      boolean displayButtons = readOnly ? false : display.getEditorButtonsChk().getValue();
      eventBus.fireEvent(new ButtonDisplayChangeEvent(displayButtons));
      display.setEditorOptionsVisible(!readOnly);
      display.setValidationOptionsVisible(!readOnly);
   }

   @Override
   protected void onUnbind()
   {
   }

   @Override
   public void onRevealDisplay()
   {
   }

}
