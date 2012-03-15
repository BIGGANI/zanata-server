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
package org.zanata.webtrans.client.editor.table;

import org.zanata.webtrans.client.resources.NavigationMessages;
import org.zanata.webtrans.client.ui.HighlightingLabel;
import org.zanata.webtrans.shared.model.TransUnit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SourcePanel extends Composite implements HasValue<TransUnit>, HasClickHandlers
{

   private final FlowPanel panel;
   private final VerticalPanel sourceLabels;
   private TransUnit value;

   public SourcePanel(TransUnit value, TableResources resources, NavigationMessages messages)
   {
      this.value = value;
      panel = new FlowPanel();
      panel.setSize("100%", "100%");

      initWidget(panel);
      setStylePrimaryName("TableEditorSource");

      sourceLabels = new VerticalPanel();
      sourceLabels.setSize("100%", "100%");

      // for (String source : value.getSources)
      // {
      // HighlightingLabel sourceLabel = new HighlightingLabel(source);
      // sourceLabel.setStylePrimaryName("TableEditorContent");
      // sourceLabel.setTitle(messages.sourceCommentLabel() +
      // value.getSourceComment());
      // sourceLabels.add(sourceLabel);
      // }

      HighlightingLabel sourceLabel = new HighlightingLabel(value.getSource());
      sourceLabel.setStylePrimaryName("TableEditorContent");
      sourceLabel.setTitle(messages.sourceCommentLabel() + value.getSourceComment());

      sourceLabels.add(sourceLabel);
      panel.add(sourceLabels);
   }

   public VerticalPanel getLabels()
   {
      return sourceLabels;
   }

   @Override
   public TransUnit getValue()
   {
      return value;
   }

   @Override
   public void setValue(TransUnit value)
   {
      setValue(value, true);
   }

   @Override
   public void setValue(TransUnit value, boolean fireEvents)
   {
      if (this.value != value)
      {
         this.value = value;
         if (fireEvents)
         {
            ValueChangeEvent.fire(this, value);
         }
      }
   }

   @Override
   public HandlerRegistration addValueChangeHandler(ValueChangeHandler<TransUnit> handler)
   {
      return addHandler(handler, ValueChangeEvent.getType());
   }

   @Override
   public HandlerRegistration addClickHandler(ClickHandler handler)
   {
      return addHandler(handler, ClickEvent.getType());
   }
   
   public void highlightSearch(String search)
   {
      for (int i = 0; i < sourceLabels.getWidgetCount(); i++)
      {
         HighlightingLabel sourceLabel = (HighlightingLabel) sourceLabels.getWidget(i);
         sourceLabel.highlightSearch(search);
      }
   }
}
