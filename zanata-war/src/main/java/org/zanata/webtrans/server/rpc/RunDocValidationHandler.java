package org.zanata.webtrans.server.rpc;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.zanata.dao.DocumentDAO;
import org.zanata.dao.LocaleDAO;
import org.zanata.model.HDocument;
import org.zanata.model.HLocale;
import org.zanata.service.ValidationService;
import org.zanata.webtrans.server.ActionHandlerFor;
import org.zanata.webtrans.shared.model.DocumentId;
import org.zanata.webtrans.shared.model.WorkspaceId;
import org.zanata.webtrans.shared.rpc.RunDocValidationAction;
import org.zanata.webtrans.shared.rpc.RunDocValidationResult;

@Name("webtrans.gwt.RunDocValidationHandler")
@Scope(ScopeType.STATELESS)
@ActionHandlerFor(RunDocValidationAction.class)
public class RunDocValidationHandler extends AbstractActionHandler<RunDocValidationAction, RunDocValidationResult>
{
   @In
   private ValidationService validationServiceImpl;

   @In
   private LocaleDAO localeDAO;

   @In
   private DocumentDAO documentDAO;

   @Override
   public RunDocValidationResult execute(RunDocValidationAction action, ExecutionContext context) throws ActionException
   {
      WorkspaceId workspaceId = action.getWorkspaceId();
      HLocale locale = localeDAO.findByLocaleId(workspaceId.getLocaleId());

      List<HDocument> hDocs = documentDAO.getDocumentsByIds(action.getDocIds());

      Map<DocumentId, Boolean> result;

      result = validationServiceImpl.runValidations(hDocs, action.getValidationIds(), locale.getId());
      return new RunDocValidationResult(result, workspaceId.getLocaleId());
   }

   @Override
   public void rollback(RunDocValidationAction action, RunDocValidationResult result, ExecutionContext context) throws ActionException
   {
   }

}