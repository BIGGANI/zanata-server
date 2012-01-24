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
package org.zanata.webtrans.client.events;

import java.util.List;

import org.zanata.webtrans.shared.model.TransUnitId;

import com.google.gwt.event.shared.GwtEvent;

/**
 *
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 *
 **/
public class UpdateValidationWarningsEvent extends GwtEvent<UpdateValidationWarningsEventHandler>
{
   /**
    * Handler type.
    */
   private static Type<UpdateValidationWarningsEventHandler> TYPE;

   /**
    * Gets the type associated with this event.
    * 
    * @return returns the handler type
    */
   public static Type<UpdateValidationWarningsEventHandler> getType()
   {
      return TYPE != null ? TYPE : (TYPE = new Type<UpdateValidationWarningsEventHandler>());
   }

   private TransUnitId id;
   private List<String> errors;

   public UpdateValidationWarningsEvent(TransUnitId id, List<String> errors)
   {
      this.id = id;
      this.errors = errors;
   }

   @Override
   public Type<UpdateValidationWarningsEventHandler> getAssociatedType()
   {
      return getType();
   }


   @Override
   protected void dispatch(UpdateValidationWarningsEventHandler handler)
   {
      handler.onUpdate(this);
   }

   public TransUnitId getId()
   {
      return id;
   }

   public List<String> getErrors()
   {
      return errors;
   }
}


 