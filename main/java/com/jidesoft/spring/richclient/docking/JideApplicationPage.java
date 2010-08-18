/*
 * Copyright 2005 the original author or authors. Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.jidesoft.spring.richclient.docking;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentPane;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.application.support.DefaultApplicationPage;
import org.springframework.richclient.application.support.DefaultViewDescriptor;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.image.IconSource;

import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.DockingManager;
import com.jidesoft.docking.Workspace;
import com.jidesoft.docking.event.DockableFrameAdapter;
import com.jidesoft.docking.event.DockableFrameEvent;
import com.jidesoft.spring.richclient.docking.view.JideAbstractView;
import com.jidesoft.spring.richclient.docking.view.JideViewDescriptor;
import com.jidesoft.spring.richclient.perspective.PerspectiveManager;

/**
 * Spring RCP ApplicationPage implementation that simply delegates most things to the underlying docking manager. The
 * page is constructed from an instance of DockingPageDescriptor that specifies a list of Spring RCP View instances that
 * are added as docked windows, and a Spring RCP View instance for the workspace.
 * 
 * @author Jonny Wray
 */
public class JideApplicationPage extends DefaultApplicationPage {

    private static final Log log = LogFactory.getLog(JideApplicationPage.class);

    private Set pageViews = new HashSet();
    private JideApplicationWindow window;
    private PageComponent workspaceComponent;
    private JComponent control;

    public JideApplicationPage(ApplicationWindow window, PageDescriptor pageDescriptor) {
        super(window, pageDescriptor);
        if (log.isInfoEnabled()) {
            log.info("Constructing Application page " + pageDescriptor.getId());
        }
        this.window = (JideApplicationWindow) window;
        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner",
                new FocusOwnerChangeListener());
    }

    public PerspectiveManager getPerspectiveManager() {
        return ((JidePageDescriptor) getPageDescriptor()).getPerspectiveManager();
    }


    public JComponent createControl() {
        if (control == null) {
            this.getPageDescriptor().buildInitialLayout(this);
            DockingManager manager = window.getDockingManager();
            control = manager.getDockedFrameContainer();
            Object initialEditorContents = ((JidePageDescriptor) getPageDescriptor()).getInitialEditorContents();
            if (initialEditorContents != null) {
                openEditor(initialEditorContents);
            }
        }
        return control;
    }


    protected void doRemovePageComponent(PageComponent pageComponent) {
        if (log.isDebugEnabled()) {
            log.debug("Removing " + pageComponent.getId());
        }
        DockingManager manager = window.getDockingManager();
        manager.removeFrame(pageComponent.getId());
    }

    /**
     * This sets the visible flag on all show view commands that are registered with the command manager. If the page
     * contains the view the command is visible, otherwise not. The registration of the show view command with the
     * command manager is the responsibility of the view descriptor.
     */
    public void updateShowViewCommands() {
        ViewDescriptorRegistry viewDescriptorRegistry = (ViewDescriptorRegistry) ApplicationServicesLocator.services()
                .getService(ViewDescriptorRegistry.class);
        ViewDescriptor[] views = viewDescriptorRegistry.getViewDescriptors();

        for (int i = 0; i < views.length; i++) {
            String id = views[i].getId();
            CommandManager commandManager = window.getCommandManager();
            if (commandManager.containsActionCommand(id)) {
                ActionCommand command = commandManager.getActionCommand(id);
                command.setVisible(pageViews.contains(views[i].getId()));
            }
        }
    }

    protected boolean giveFocusTo(PageComponent pageComponent) {
        if (log.isDebugEnabled()) {
            log.debug("Giving focus to " + pageComponent.getId());
        }
        PageComponentPane pane = pageComponent.getContext().getPane();
        pane.getControl().requestFocusInWindow();
        fireFocusGained(pageComponent);
        return true;
    }

    public void showView(ViewDescriptor viewDescriptor) {
        if (log.isDebugEnabled()) {
            log.debug("Adding view for " + viewDescriptor.getId());
        }
        JideViewDescriptor jvd = (JideViewDescriptor)viewDescriptor;
        super.showView(viewDescriptor);
        registerPageComponent(viewDescriptor.getId());
    }

    public void addView(String viewDescriptorId) {
        showView(viewDescriptorId);
    }

    /**
     * Implementation of the openEditor command using the editor factory injected into the page descriptor. The editor
     * input is used as the key the factory uses to get the requires editor descriptor. If not editor factory is
     * injected into the page descriptor then the default factory is used which simply returns null for every editor
     * object resulting in no editor being opened.
     */
    // public void openEditor(Object editorInput){
    // if(log.isDebugEnabled()){
    // log.debug("Attempting to open editor for "+editorInput.getClass().getName());
    // }
    // if(workspaceComponent == null){
    // log.debug("WorkspaceComponent is null");
    // return;
    // }
    // if(!(workspaceComponent instanceof WorkspaceView)){
    // log.debug("WorkspaceComponent is not a workspace view");
    // return;
    // }
    // PageDescriptor descriptor = getPageDescriptor();
    // if(descriptor instanceof JidePageDescriptor){
    // if(editorInput.getClass().isArray()){
    // Object[] array = (Object[])editorInput;
    // for(int i=0;i<array.length;i++){
    // Object editorObject = array[i];
    // processEditorObject(editorObject);
    // }
    // }
    // else{
    // processEditorObject(editorInput);
    // }
    //    		
    // }
    // }
    // private void processEditorObject(Object editorObject){
    // JidePageDescriptor pageDescriptor = (JidePageDescriptor)getPageDescriptor();
    // EditorDescriptor editorDescriptor =
    // pageDescriptor.getEditorFactory().getEditorDescriptor(editorObject);
    // if(editorDescriptor != null){
    // PageComponent pageComponent = editorDescriptor.createPageComponent();
    // AbstractEditor editor = (AbstractEditor)pageComponent;
    // editor.initialize(editorObject);
    // EditorComponentPane editorPane = new EditorComponentPane(pageComponent);
    // pageComponent.setContext(new DefaultViewContext(this, editorPane));
    // WorkspaceView workspace = (WorkspaceView)workspaceComponent;
    // workspace.addDocumentComponent(pageComponent);
    // // Fires lifecycle event so listeners can react to editor
    // // being opened.
    // LifecycleApplicationEvent event = new LifecycleApplicationEvent(
    // LifecycleApplicationEvent.CREATED, editor);
    // Application.instance().getApplicationContext().publishEvent(event);
    // }
    // }
    private void registerPageComponent(String viewDescriptorId) {
        PageComponent pageComponent = findPageComponent(viewDescriptorId);
        ViewDescriptor descriptor = getViewDescriptor(viewDescriptorId);
        if (descriptor instanceof JideViewDescriptor) {
            JideViewDescriptor viewDescriptor = (JideViewDescriptor) descriptor;
            if (viewDescriptor.isWorkspace()) {
                registerWorkspaceView(pageComponent);
            } else {
                registerNormalView(pageComponent, viewDescriptor);
                pageViews.add(viewDescriptorId);
            }
        } else {
            registerNormalView(pageComponent, null);
            pageViews.add(viewDescriptorId);
        }
    }


    private void registerWorkspaceView(PageComponent pageComponent) {
        if (log.isInfoEnabled()) {
            log.info("Registering workspace view " + pageComponent.getId());
        }
        DockingManager manager = window.getDockingManager();
        manager.getWorkspace().add(pageComponent.getControl());
        workspaceComponent = pageComponent;
    }

    private void registerNormalView(PageComponent pageComponent, JideViewDescriptor viewDescriptor) {
        if (log.isInfoEnabled()) {
            log.info("Registering view " + pageComponent.getId());
        }
        DockingManager manager = window.getDockingManager();
        String frameName = pageComponent.getId();
        if (manager.getAllFrameNames().contains(frameName)) {
            if (log.isDebugEnabled()) {
                log.debug("Showing existing docked frame " + frameName);
            }
            DockContext currentContext = manager.getContextOf(frameName);
            
            if (currentContext.isHidden() || 
            		currentContext.getAvailablePreviousState() == null) // mscoon 15/8/2010 - added this clause because otherwise initially hidden views were not being shown
            {
                if (viewDescriptor.isFloatOnShow()) {
                    manager.floatFrame(frameName, viewDescriptor.getFloatBounds(), true);
                } else {
                    manager.showFrame(frameName);
                }
            } else {
                manager.activateFrame(frameName);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Adding new dockable frame " + frameName);
            }
            manager.addFrame(createDockableFrame(pageComponent, viewDescriptor));
        }
    }

    protected DockableFrame createDockableFrame(final PageComponent pageComponent, JideViewDescriptor viewDescriptor) {

        if (log.isInfoEnabled()) {
            log.info("Creating dockable frame for page component " + pageComponent.getId());
        }
        Icon icon = pageComponent.getIcon();
        if (icon == null) {
            IconSource iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
            icon = iconSource.getIcon("applicationInfo.image");
        }
        DockableFrame dockableFrame = new DockableFrame(pageComponent.getId(), icon);
        dockableFrame.setTitle(pageComponent.getDisplayName());
        dockableFrame.setTabTitle(pageComponent.getDisplayName());
        dockableFrame.setFrameIcon(icon);
        if (viewDescriptor != null) {
            dockableFrame.getContext().setInitMode(viewDescriptor.getInitMode());
            dockableFrame.getContext().setInitSide(viewDescriptor.getInitSide());
            dockableFrame.getContext().setInitIndex(viewDescriptor.getInitIndex());
        } else {
            dockableFrame.getContext().setInitMode(DockContext.STATE_FRAMEDOCKED);
            dockableFrame.getContext().setInitSide(DockContext.DOCK_SIDE_EAST);
            dockableFrame.getContext().setInitIndex(0);
        }
        dockableFrame.addDockableFrameListener(new DockableFrameAdapter() {

            public void dockableFrameRemoved(DockableFrameEvent event) {
                if (log.isDebugEnabled()) {
                    log.debug("Frame removed event on " + pageComponent.getId());
                }
                fireClosed(pageComponent);
            }

            public void dockableFrameActivated(DockableFrameEvent e) {
                if (log.isDebugEnabled()) {
                    log.debug("Frame activated event on " + pageComponent.getId());
                }
                fireFocusLost(workspaceComponent);
                fireFocusGained(pageComponent);
            }

            public void dockableFrameDeactivated(DockableFrameEvent e) {
                if (log.isDebugEnabled()) {
                    log.debug("Frame deactivated event on " + pageComponent.getId());
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
        if (pageComponent instanceof JideAbstractView) {
            JideAbstractView view = (JideAbstractView) pageComponent;
            dockableFrame.setTitleBarComponent(view.getViewToolBar());
            dockableFrame.setJMenuBar(view.getViewMenuBar());
            final DockableFrame ff = dockableFrame;
            view.getDescriptor().addPropertyChangeListener("title", new PropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent evt) {
					ff.setTitle(evt.getNewValue().toString());
					ff.setTabTitle(pageComponent.getDisplayName());
				}
            	
            });
        }
        return dockableFrame;
    }

    /*
     * These next four methods change the access from protected to public allowing non children to fire these events.
     * This is needs to allow the Jide events to translate into spring events
     */
    public void fireClosed(PageComponent component) {
        super.fireClosed(component);
    }

    public void fireOpened(PageComponent component) {
        super.fireOpened(component);
    }

    public void fireFocusLost(PageComponent component) {
        if (component != null) {
            super.fireFocusLost(component);
        }
    }

    public void fireFocusGained(PageComponent component) {
        if (component != null) {
            super.fireFocusGained(component);
        }
    }

    /*
     * This listener (registered in the constructor on the DefaultKeyboardFocusManager) is here to deal with one odd
     * case in the JIDE to Spring RCP event translation. If a document tab is clicked after a specific view was active,
     * a documentComponentActivated event is not fired (I'm not really sure why not) causing the spring rcp command
     * framework not to follow the active frame/document. This overcomes that problem. The link documents the JIDE forum
     * thread discussing this. http://www.jidesoft.com/forum/viewtopic.php?t=2722
     */
    private class FocusOwnerChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            DefaultKeyboardFocusManager manager = (DefaultKeyboardFocusManager) evt.getSource();
            Component focusOwner = manager.getFocusOwner();
            Workspace workspace = (Workspace) getDockableWorkspace(focusOwner);
            // if( workspace != null){
            // if(logger.isDebugEnabled()){
            // logger.debug("About to fire focus gained on the active workspace component");
            // }
            // WorkspaceView workspaceView = (WorkspaceView)workspaceComponent;
            // workspaceView.fireFocusGainedOnActiveComponent();
            // }
        }

        private Component getDockableWorkspace(Component c) {
            if (c == null)
                return null;
            if (c instanceof Workspace)
                return c;
            do {
                c = c.getParent();
                if (c == null)
                    return null;
            } while (!(c instanceof Workspace));
            return c;
        }
    }

    protected PageComponent getWorkspaceComponent() {
        return workspaceComponent;
    }

    protected void setWorkspaceComponent(PageComponent workspaceComponent) {
        this.workspaceComponent = workspaceComponent;
    }

    
}
