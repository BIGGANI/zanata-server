package org.zanata.webtrans.client.view;

import org.zanata.webtrans.client.presenter.TransUnitDetailsPresenter;
import org.zanata.webtrans.client.resources.Resources;
import org.zanata.webtrans.client.resources.WebTransMessages;
import org.zanata.webtrans.shared.model.TransUnit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class TransUnitDetailsView extends Composite implements TransUnitDetailsPresenter.Display
{

   private static TransUnitDetailsViewUiBinder uiBinder = GWT.create(TransUnitDetailsViewUiBinder.class);

   interface TransUnitDetailsViewUiBinder extends UiBinder<Widget, TransUnitDetailsView>
   {
   }

   @UiField
   Label resIdLabel, resId, sourceCommentLabel, sourceComment, msgContextLabel, msgContext, lastModifiedByLabel, lastModifiedBy, lastModifiedTimeLabel, lastModifiedTime;

   @UiField
   LayoutPanel rootPanel;

   @UiField
   VerticalPanel labelPanel;

   @UiField
   Label emptyLabel;

   @UiField
   FlowPanel emptyFlowPanel;

   @Inject
   public TransUnitDetailsView(WebTransMessages messages, Resources resources)
   {
      initWidget(uiBinder.createAndBindUi(this));
      emptyLabel.setText("Select a Translation Unit to view details.");
   }

   @Override
   public Widget asWidget()
   {
      return this;
   }

   @Override
   public void showDetails(TransUnit transUnit)
   {
      emptyFlowPanel.setVisible(false);
      resIdLabel.setText("Resource ID: ");
      resId.setText(transUnit.getResId());
      
      String context = transUnit.getMsgContext();
      if (context != null)
      {
         msgContextLabel.setText("Message Context: ");
         msgContext.setText(context);
         msgContextLabel.setVisible(true);
         msgContext.setVisible(true);
      }
      else
      {
         msgContextLabel.setVisible(false);
         msgContext.setVisible(false);
      }
      
      sourceCommentLabel.setText("Source Comment: ");
      sourceComment.setText(transUnit.getSourceComment());
      String person = transUnit.getLastModifiedBy();
      if (person != null && !person.isEmpty())
      {
         lastModifiedByLabel.setText("Last Modified By:");
         lastModifiedBy.setText(person);
         lastModifiedTimeLabel.setText("Last Modified Time:");
         lastModifiedTime.setText(transUnit.getLastModifiedTime());
      }
      else
      {
         lastModifiedByLabel.setText("");
         lastModifiedBy.setText("");
         lastModifiedTimeLabel.setText("");
         lastModifiedTime.setText("");
      }
   }
}
