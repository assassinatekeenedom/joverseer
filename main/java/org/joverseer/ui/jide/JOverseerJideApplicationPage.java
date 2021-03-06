package org.joverseer.ui.jide;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.image.IconSource;

import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.event.DockableFrameAdapter;
import com.jidesoft.docking.event.DockableFrameEvent;
import com.jidesoft.spring.richclient.docking.JideApplicationPage;
import com.jidesoft.spring.richclient.docking.view.JideAbstractView;
import com.jidesoft.spring.richclient.docking.view.JideViewDescriptor;

/**
 * Specializes JideApplicationPage
 * 
 * @author Marios Skounakis
 */
public class JOverseerJideApplicationPage extends JideApplicationPage {
	private static final Log log = LogFactory.getLog(JideApplicationPage.class);

	public JOverseerJideApplicationPage(ApplicationWindow window, PageDescriptor pageDescriptor) {
		super(window, pageDescriptor);
	}

	@Override
	protected DockableFrame createDockableFrame(final PageComponent pageComponent, 
    		JideViewDescriptor viewDescriptor) {

		if (log.isInfoEnabled()) {
			log.info("Creating dockable frame for page component "+ pageComponent.getId());
		}
		Icon icon = pageComponent.getIcon();
        IconSource iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
        DockableFrame dockableFrame;
        if (viewDescriptor != null) {
        	icon = iconSource.getIcon(viewDescriptor.getId() + ".icon");
        }
		if (icon == null) {
            icon = iconSource.getIcon("applicationInfo.image");
		}
		dockableFrame = new DockableFrame(pageComponent.getId(), icon);
                
		dockableFrame.setTitle(pageComponent.getDisplayName());
		dockableFrame.setTabTitle(pageComponent.getDisplayName());
        dockableFrame.setToolTipText(pageComponent.getDisplayName());
		dockableFrame.setFrameIcon(icon);
		if(viewDescriptor != null){
			dockableFrame.getContext().setInitMode(viewDescriptor.getInitMode());
			dockableFrame.getContext().setInitSide(viewDescriptor.getInitSide());
			dockableFrame.getContext().setInitIndex(viewDescriptor.getInitIndex());
			if (viewDescriptor instanceof JOverseerJideViewDescriptor) {
				JOverseerJideViewDescriptor jovViewDescriptor = (JOverseerJideViewDescriptor)viewDescriptor;
				dockableFrame.setPreferredSize(new Dimension(jovViewDescriptor.getPreferredWidth(), jovViewDescriptor.getPreferredHeight()));
			} 
		}
		else{
			dockableFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
			dockableFrame.getContext().setInitSide(DockContext.DOCK_SIDE_EAST);
			dockableFrame.getContext().setInitIndex(0);
		}
		dockableFrame.addDockableFrameListener(new DockableFrameAdapter() {
            
			@Override
			public void dockableFrameRemoved(DockableFrameEvent event) {
				if(log.isDebugEnabled()){
					log.debug("Frame removed event on "+pageComponent.getId());
				}
				fireClosed(pageComponent);
			}

			@Override
			public void dockableFrameActivated(DockableFrameEvent e) {
				if(log.isDebugEnabled()){
					log.debug("Frame activated event on "+pageComponent.getId());
				}
				fireFocusLost(getWorkspaceComponent());
                fireFocusGained(pageComponent);
            }
            
            @Override
			public void dockableFrameDeactivated(DockableFrameEvent e){
				if(log.isDebugEnabled()){
					log.debug("Frame deactivated event on "+pageComponent.getId());
				}
            	fireFocusLost(pageComponent);
            }
        }); 

		dockableFrame.getContentPane().setLayout(new BorderLayout());
		dockableFrame.getContentPane().add(pageComponent.getControl());
	
		// This is where the view specific toolbar and menu bar get added. Note,
		// that this is different from the editors. With the views they are part
		// of the dockable frame, but with editors we add them to the editor
		// pane itself in EditorComponentPane
		if(pageComponent instanceof JideAbstractView){
			JideAbstractView view = (JideAbstractView)pageComponent;
			dockableFrame.setTitleBarComponent(view.getViewToolBar());
			dockableFrame.setJMenuBar(view.getViewMenuBar());
			
		}
		if (pageComponent instanceof AbstractView) {
			AbstractView view = (AbstractView)pageComponent;
			final DockableFrame ff = dockableFrame;
            view.getDescriptor().addPropertyChangeListener("displayName", new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					ff.setTitle(evt.getNewValue().toString());
				}
            	
            });
		}
		return dockableFrame;
	}
	
}
