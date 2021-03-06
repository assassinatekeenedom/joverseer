package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.ScalableAbstractView;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class BaseHtmlReportView extends ScalableAbstractView implements ApplicationListener {
	JEditorPane editor;

	protected String getReportContents() {
		return null;
	}

	protected void handleHyperlinkEvent(String url) {

	}

	protected JPanel getCriteriaPanel() {
		return null;
	}

	@Override
	protected JComponent createControl() {
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.relatedGapRow();
		JPanel pnl = getCriteriaPanel();
		if (pnl != null) {
			tlb.cell(pnl, "align=left"); //$NON-NLS-1$
			tlb.relatedGapRow();
		}
		JButton btn = new JButton(Messages.getString("BaseHtmlReportView.Generate")); //$NON-NLS-1$
		btn.setText(Messages.getString("BaseHtmlReportView.Generate")); //$NON-NLS-1$
		btn.setPreferredSize(this.uiSizes.newDimension(100/20, this.uiSizes.getHeight5()));
		tlb.cell(btn, "align=left"); //$NON-NLS-1$
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!GameHolder.hasInitializedGame()) {
					BaseHtmlReportView.this.editor.setText(""); //$NON-NLS-1$
					return;
				}
				BaseHtmlReportView.this.editor.setText(getReportContents());
			}

		});
		tlb.relatedGapRow();

		this.editor = new JEditorPane();
		this.editor.setEditable(false);
		this.editor.setContentType("text/html"); //$NON-NLS-1$
		this.editor.setText(""); //$NON-NLS-1$
		this.editor.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == EventType.ACTIVATED) {
					String s = e.getURL().toString();
					handleHyperlinkEvent(s);
				}
			}

		});
		tlb.cell(this.editor);
		JScrollPane scp = new JScrollPane(tlb.getPanel());
		scp.setPreferredSize(new Dimension(240, 1500));
		scp.getVerticalScrollBar().setUnitIncrement(32);
		return scp;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			JOverseerEvent e = (JOverseerEvent) applicationEvent;
			if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString()) || e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
				this.editor.setText(""); //$NON-NLS-1$
			}
		}
	}
}
