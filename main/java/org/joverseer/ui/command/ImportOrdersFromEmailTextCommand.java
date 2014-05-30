package org.joverseer.ui.command;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.orders.OrderTextReader;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Imports orders from text using the ParseOrdersForm form
 * 
 * @author Marios Skounakis
 */
public class ImportOrdersFromEmailTextCommand extends ActionCommand {
	ParseOrdersForm form;

	public ImportOrdersFromEmailTextCommand() {
		super("importOrdersFromEmailTextCommand"); //$NON-NLS-1$
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;
		this.form = new ParseOrdersForm(FormModelHelper.createFormModel(new String()));
		FormBackedDialogPage pg = new FormBackedDialogPage(this.form);
		final ClipboardOwner clipboardOwner = this.form;
		TitledPageApplicationDialog dlg = new TitledPageApplicationDialog(pg) {
			@Override
			protected boolean onFinish() {
				loadOrders(ImportOrdersFromEmailTextCommand.this.form.getOrderText(), ImportOrdersFromEmailTextCommand.this.form.getOrderTextType());
				return true;
			}

			@Override
			protected Object[] getCommandGroupMembers() {
				return new Object[] { new ActionCommand("copyFromClipboardCommand") { //$NON-NLS-1$
					@Override
					protected void doExecuteCommand() {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						Transferable s = clipboard.getContents(clipboardOwner);
						try {
							ImportOrdersFromEmailTextCommand.this.form.setOrderText(s.getTransferData(DataFlavor.stringFlavor).toString());
						} catch (Exception e) {
							// do nothing
						}
					}
				}, new ActionCommand("parseOrdersCommand") { //$NON-NLS-1$
					@Override
					protected void doExecuteCommand() {
						ImportOrdersFromEmailTextCommand.this.form.setParseResults(parseOrders(ImportOrdersFromEmailTextCommand.this.form.getOrderText(), ImportOrdersFromEmailTextCommand.this.form.getOrderTextType()));
					}
				}, getFinishCommand(), getCancelCommand() };
			}

		};
		dlg.setTitle(Messages.getString("importOrdersFromEmailTextCommand.ImportOrdersFromText")); //$NON-NLS-1$
		dlg.showDialog();
	}

	private ArrayList<String> parseOrders(String text, String textType) {
		OrderTextReader orderTextReader = new OrderTextReader();
		if (textType.equals("Standard")) { //$NON-NLS-1$
			orderTextReader.setTextType(OrderTextReader.STANDARD_ORDER_TEXT);
		} else if (textType.equals("Order Checker")) { //$NON-NLS-1$
			orderTextReader.setTextType(OrderTextReader.ORDERCHECKER_ORDER_TEXT);
		}
		orderTextReader.setGame(GameHolder.instance().getGame());
		orderTextReader.setOrderText(text);
		orderTextReader.readOrders(0);
		return orderTextReader.getLineResults();
	}

	private void loadOrders(String text, String textType) {
		OrderTextReader orderTextReader = new OrderTextReader();
		if (textType.equals("Standard")) { //$NON-NLS-1$
			orderTextReader.setTextType(OrderTextReader.STANDARD_ORDER_TEXT);
		} else if (textType.equals("Order Checker")) { //$NON-NLS-1$
			orderTextReader.setTextType(OrderTextReader.ORDERCHECKER_ORDER_TEXT);
		}
		orderTextReader.setGame(GameHolder.instance().getGame());
		orderTextReader.setOrderText(text);
		orderTextReader.readOrders(1);
		MessageDialog dialog = new MessageDialog(Messages.getString("importOrdersFromEmailTextCommand.ImportOrders"), 
					Messages.getString("importOrdersFromEmailTextCommand.OrdersImported", new Object[] {orderTextReader.getOrders()})); //$NON-NLS-1$ //$NON-NLS-2$
		dialog.showDialog();
		Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), GameHolder.instance().getGame(), this));

	}

	class ParseOrdersForm extends AbstractForm implements ClipboardOwner {
		JTextArea orderText;
		JTextArea parseResults;
		JScrollPane orderScp;
		JScrollPane parseScp;
		JComboBox orderTextTypeCmb;

		private ParseOrdersForm(FormModel arg0) {
			super(arg0, "parseOrdersForm"); //$NON-NLS-1$
		}

		@Override
		protected JComponent createFormControl() {
			TableLayoutBuilder tlb = new TableLayoutBuilder();
			tlb.cell(new JLabel(Messages.getString("importOrdersFromEmailTextCommand.OrdersColon")), "colspec=left:60px valign=top"); //$NON-NLS-1$ //$NON-NLS-2$
			tlb.gapCol();

			this.orderText = new JTextArea();
			this.orderScp = new JScrollPane(this.orderText);
			this.orderScp.setPreferredSize(new Dimension(300, 500));
			this.orderScp.getViewport().addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					if (!ParseOrdersForm.this.parseResults.getText().equals("")) { //$NON-NLS-1$
						ParseOrdersForm.this.parseScp.getViewport().setViewPosition(ParseOrdersForm.this.orderScp.getViewport().getViewPosition());
					}
				}

			});
			tlb.cell(this.orderScp);

			tlb.gapCol();
			this.parseResults = new JTextArea();
			this.parseResults.setEditable(false);
			this.parseScp = new JScrollPane(this.parseResults);
			this.parseScp.setPreferredSize(new Dimension(300, 500));
			this.parseScp.getViewport().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					ParseOrdersForm.this.orderScp.getViewport().setViewPosition(ParseOrdersForm.this.parseScp.getViewport().getViewPosition());
				}
			});
			tlb.cell(this.parseScp);
			tlb.relatedGapRow();

			this.orderTextTypeCmb = new JComboBox();
			this.orderTextTypeCmb.setPreferredSize(new Dimension(200, 20));
			this.orderTextTypeCmb.addItem("Standard"); //$NON-NLS-1$
			this.orderTextTypeCmb.addItem("Order Checker"); //$NON-NLS-1$
			tlb.cell(new JLabel(Messages.getString("importOrdersFromEmailTextCommand.TypeColon"))); //$NON-NLS-1$
			tlb.gapCol();
			TableLayoutBuilder tlb2 = new TableLayoutBuilder();
			tlb2.cell(this.orderTextTypeCmb, "colspan=1 colspec=left:100px"); //$NON-NLS-1$
			tlb2.relatedGapRow();
			tlb.cell(tlb2.getPanel(), "colspan=1"); //$NON-NLS-1$
			tlb.row();

			return tlb.getPanel();
		}

		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {
		}

		public void setParseResults(ArrayList<String> results) {
			String res = ""; //$NON-NLS-1$
			for (String r : results) {
				res += r + "\n"; //$NON-NLS-1$
			}
			this.parseResults.setText(res);
			this.orderText.setCaretPosition(0);
			this.parseResults.setCaretPosition(0);
		}

		public String getOrderText() {
			return this.orderText.getText();
		}

		public void setOrderText(String text) {
			this.orderText.setText(text);
		}

		public String getOrderTextType() {
			return this.orderTextTypeCmb.getSelectedItem().toString();
		}
	}

}
