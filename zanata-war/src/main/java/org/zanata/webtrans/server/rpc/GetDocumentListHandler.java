package org.zanata.webtrans.server.rpc;

import java.util.ArrayList;
import java.util.Collection;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.zanata.common.LocaleId;
import org.zanata.common.TranslationStats;
import org.zanata.dao.DocumentDAO;
import org.zanata.dao.ProjectIterationDAO;
import org.zanata.model.HDocument;
import org.zanata.model.HProjectIteration;
import org.zanata.security.ZanataIdentity;
import org.zanata.webtrans.server.ActionHandlerFor;
import org.zanata.webtrans.shared.model.DocumentId;
import org.zanata.webtrans.shared.model.DocumentInfo;
import org.zanata.webtrans.shared.model.ProjectIterationId;
import org.zanata.webtrans.shared.rpc.GetDocumentList;
import org.zanata.webtrans.shared.rpc.GetDocumentListResult;

@Name("webtrans.gwt.GetDocsListHandler")
@Scope(ScopeType.STATELESS)
@ActionHandlerFor(GetDocumentList.class)
public class GetDocumentListHandler extends AbstractActionHandler<GetDocumentList, GetDocumentListResult>
{
   @In
   private ZanataIdentity identity;

   @In
   private ProjectIterationDAO projectIterationDAO;

   @In
   private DocumentDAO documentDAO;

   @Override
   public GetDocumentListResult execute(GetDocumentList action, ExecutionContext context) throws ActionException
   {
      identity.checkLoggedIn();

      LocaleId localeId = action.getWorkspaceId().getLocaleId();
      ProjectIterationId iterationId = action.getProjectIterationId();
      ArrayList<DocumentInfo> docs = new ArrayList<DocumentInfo>();
      HProjectIteration hProjectIteration = projectIterationDAO.getBySlug(iterationId.getProjectSlug(), iterationId.getIterationSlug());
      Collection<HDocument> hDocs = hProjectIteration.getDocuments().values();
      for (HDocument hDoc : hDocs)
      {
         if (action.getFilters() == null || action.getFilters().isEmpty() || action.getFilters().contains(hDoc.getPath() + hDoc.getName()))
         {
            DocumentId docId = new DocumentId(hDoc.getId());
            TranslationStats stats = documentDAO.getStatistics(hDoc.getId(), localeId);
            DocumentInfo doc = new DocumentInfo(docId, hDoc.getName(), hDoc.getPath(), hDoc.getLocale().getLocaleId(), stats);
            docs.add(doc);
         }
      }
      return new GetDocumentListResult(iterationId, docs);
   }

   @Override
   public void rollback(GetDocumentList action, GetDocumentListResult result, ExecutionContext context) throws ActionException
   {
   }

}