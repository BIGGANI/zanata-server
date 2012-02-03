package org.zanata.webtrans.client.rpc;

import org.zanata.webtrans.shared.auth.Identity;
import org.zanata.webtrans.shared.auth.SessionId;
import org.zanata.webtrans.shared.model.Person;
import org.zanata.webtrans.shared.model.PersonId;
import org.zanata.webtrans.shared.model.WorkspaceContext;
import org.zanata.webtrans.shared.rpc.ActivateWorkspaceAction;
import org.zanata.webtrans.shared.rpc.ActivateWorkspaceResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DummyActivateWorkspaceCommand implements Command
{

   private final ActivateWorkspaceAction action;
   private final AsyncCallback<ActivateWorkspaceResult> callback;

   public DummyActivateWorkspaceCommand(ActivateWorkspaceAction gwcAction, AsyncCallback<ActivateWorkspaceResult> gwcCallback)
   {
      this.action = gwcAction;
      this.callback = gwcCallback;
   }

   @Override
   public void execute()
   {
      Log.info("ENTER DummyActivateWorkspaceCommand.execute()");
      WorkspaceContext context = new WorkspaceContext(action.getWorkspaceId(), "Dummy Workspace", "Mock Sweedish", false);

      Identity identity = new Identity(new SessionId("123456"), new Person(new PersonId("bob"), "Bob The Builder"));
      callback.onSuccess(new ActivateWorkspaceResult(context, identity));
      Log.info("EXIT DummyActivateWorkspaceCommand.execute()");
   }

}
