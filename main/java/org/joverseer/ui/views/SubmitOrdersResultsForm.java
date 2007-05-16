package org.joverseer.ui.views;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;

public class SubmitOrdersResultsForm extends AbstractForm {
	static String FORM_ID = "submitOrdersResultsForm"; 
	
	JEditorPane htmlResponse;
	
	
	public SubmitOrdersResultsForm(FormModel arg0) {
		super(arg0, FORM_ID);
	}

	protected JComponent createFormControl() {
		htmlResponse = new JEditorPane();
		htmlResponse.setContentType("text/html");
		htmlResponse.setEditorKit(new HTMLEditorKit());
		
		htmlResponse.setPreferredSize(new Dimension(600, 500));
		JScrollPane scp = new JScrollPane(htmlResponse);
		
		return scp;
	}
	
	public JEditorPane getJEditorPane() {
		return htmlResponse;
	}
	

}