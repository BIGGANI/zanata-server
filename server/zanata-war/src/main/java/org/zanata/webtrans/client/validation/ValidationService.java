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
package org.zanata.webtrans.client.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.customware.gwt.presenter.client.EventBus;

import org.zanata.webtrans.client.events.NotificationEvent;
import org.zanata.webtrans.client.events.NotificationEvent.Severity;
import org.zanata.webtrans.client.events.UpdateValidationErrorEvent;
import org.zanata.webtrans.client.resources.TableEditorMessages;
import org.zanata.webtrans.shared.model.TransUnitId;
import org.zanata.webtrans.shared.validation.ValidationObject;
import org.zanata.webtrans.shared.validation.action.HtmlXmlTagValidation;
import org.zanata.webtrans.shared.validation.action.ValidationAction;

import com.google.inject.Inject;

/**
 *
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 *
 **/

public class ValidationService
{
   private final Map<String, ValidationAction> validationMap = new HashMap<String, ValidationAction>();
   private final EventBus eventBus;
   private final TableEditorMessages messages;
   
   @Inject
   public ValidationService(final EventBus eventBus, final TableEditorMessages messages)
   {
      this.eventBus = eventBus;
      this.messages = messages;

      HtmlXmlTagValidation htmlxmlValidation = new HtmlXmlTagValidation("HTML/XML tag", "HTML/XML tag validation");
      validationMap.put(htmlxmlValidation.getId(), htmlxmlValidation);
   }

   /**
    * Execute list of validation actions if the action is enabled
    * 
    * @param tu
    */
   public void execute(TransUnitId id, String source, String target, boolean fireNotification)
   {
      List<String> errors = new ArrayList<String>();
      
      for (String key : validationMap.keySet())
      {
         ValidationAction action = validationMap.get(key);

         if (action != null && action.isEnabled())
         {
            action.clearErrorMessage();
            action.validate(source, target);
            errors.addAll(action.getError());
         }
      }
      fireErrorMessage(id, errors, fireNotification);
   }
   
   /**
    * enable/disable validation action from UI
    * 
    * @param key
    * @param isEnabled
    */
   public void updateStatus(String key, boolean isEnabled)
   {
      ValidationAction action = validationMap.get(key);
      action.setEnabled(isEnabled);
      validationMap.put(key, action);
   }

   public List<ValidationObject> getValidationList()
   {
      return new ArrayList<ValidationObject>(validationMap.values());
   }

   /**
    * Clear all validation plugin's error message
    */
   public void clearAllMessage()
   {
      for (String key : validationMap.keySet())
      {
         ValidationAction action = validationMap.get(key);

         if (action != null)
         {
            action.clearErrorMessage();
         }
      }
   }

   public void fireErrorMessage(TransUnitId id, List<String> errors, boolean fireNotification)
   {
      if (!errors.isEmpty() && fireNotification)
      {
         eventBus.fireEvent(new NotificationEvent(Severity.Info, messages.notifyValidationError()));
      }
      eventBus.fireEvent(new UpdateValidationErrorEvent(id, errors));
   }
}


 