package org.joverseer.ui.command;

import java.util.Locale;

import org.joverseer.ui.views.CreditsForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Shows the program credits using the CreditsForm
 * 
 * @author Marios Skounakis
 */
public class ShowCreditsCommand  extends ActionCommand {
    public ShowCreditsCommand() {
        super("ShowCreditsCommand");
    }

    @Override
	protected void doExecuteCommand() {
        Resource res = Application.instance().getApplicationContext().getResource("classpath:ui/credits.htm");
        FormModel formModel = FormModelHelper.createFormModel(res);
        final CreditsForm form = new CreditsForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
            }

            @Override
			protected boolean onFinish() {
                return true;
            }

            @Override
			protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] {
                        getFinishCommand()
                };
            }
        };
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dialog.setTitle(ms.getMessage("creditsDialog.title", new Object[]{}, Locale.getDefault()));
        dialog.showDialog();
    }
}
