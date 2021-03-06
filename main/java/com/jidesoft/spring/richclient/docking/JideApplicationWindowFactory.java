/*
 * Copyright 2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.jidesoft.spring.richclient.docking;

import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;

import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.ApplicationWindowFactory;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.application.support.DefaultApplicationWindowConfigurer;

import com.jidesoft.docking.DefaultDockableHolder;
import com.jidesoft.docking.DockingManager;
import com.jidesoft.docking.PopupMenuCustomizer;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.utils.Lm;

/**
 * Factory for JideApplicationWindow objects. Actually constructs the
 * underlying JFrame extension from JIDE that forms the holder frame
 * for the application and configures the underlying docking manager
 * based on the various configurable parameters.
 * 
 * @author Jonny Wray
 *
 */
public class JideApplicationWindowFactory implements ApplicationWindowFactory {

	private Short layoutVersion = null;
	private String profileKey = "profileKey";
	
	private boolean saveLayoutOnClose = true;
	
	private boolean floatable = true;
	private boolean rearrangable = true;
	private boolean resizable = true;
	private boolean continuousLayout = false;
	private int sensitiveAreaSize = 20;
	private int outlineMode = 0;
	private boolean groupAllowedOnSidePane = true;
	private boolean easyTabDocking = false;
	private boolean showGripper = false;
	private boolean showTitleBar = true;
	private int doubleClickAction = DockingManager.DOUBLE_CLICK_TO_FLOAT;
	private boolean heavyweightComponentEnabled = false;
	private boolean showWorkspace = true;
	private DockingManager.TabbedPaneCustomizer tabbedPaneCustomizer = new DefaultCustomizer();
	private PopupMenuCustomizer popupMenuCustomizer = null;
	
	
	private boolean showMenuBar = true;
	private boolean showToolBar = true;
	private boolean showStatusBar = true;
	
	@Override
	public ApplicationWindow createApplicationWindow() {
		DefaultDockableHolder dockableHolder = new DefaultDockableHolder();

		configureDockingManager(dockableHolder.getDockingManager());
		dockableHolder.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		Lm.setParent(dockableHolder);
		
		JideApplicationWindow window = new JideApplicationWindow(dockableHolder){
		    @Override
			protected ApplicationWindowConfigurer initWindowConfigurer() {
		    	DefaultApplicationWindowConfigurer configurer = new DefaultApplicationWindowConfigurer( this );
		    	configurer.setShowMenuBar(JideApplicationWindowFactory.this.showMenuBar);
		    	configurer.setShowToolBar(JideApplicationWindowFactory.this.showToolBar);
		    	configurer.setShowStatusBar(JideApplicationWindowFactory.this.showStatusBar);
		    	return configurer;
		    }

		};
		JideApplicationWindowCloseListener closeListener = new JideApplicationWindowCloseListener(window,
				dockableHolder.getDockingManager(), this.saveLayoutOnClose);
		dockableHolder.addWindowListener(closeListener);
		return window;
	}


    private void configureDockingManager(DockingManager manager){
    	if(this.layoutVersion != null){
    		manager.setVersion(this.layoutVersion.shortValue());
    	}
    	manager.setProfileKey(this.profileKey);
    	manager.setRearrangable(this.rearrangable);
    	manager.setResizable(this.resizable);
    	manager.setContinuousLayout(this.continuousLayout);
    	manager.setSensitiveAreaSize(this.sensitiveAreaSize);
    	manager.setOutlineMode(this.outlineMode);
    	manager.setGroupAllowedOnSidePane(this.groupAllowedOnSidePane);
    	manager.setEasyTabDock(this.easyTabDocking);
    	manager.setShowGripper(this.showGripper);
    	manager.setShowTitleBar(this.showTitleBar);
    	manager.setDoubleClickAction(this.doubleClickAction);
    	manager.setHeavyweightComponentEnabled(this.heavyweightComponentEnabled);
    	manager.setShowWorkspace(this.showWorkspace);
    	manager.setFloatable(this.floatable);
    	manager.setTabbedPaneCustomizer(this.tabbedPaneCustomizer);
    	if(this.popupMenuCustomizer != null){
    		manager.setPopupMenuCustomizer(this.popupMenuCustomizer);
    	}
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(!this.heavyweightComponentEnabled);
    }
	
    public void setShowToolBar(boolean showToolBar){
    	this.showToolBar = showToolBar;
    }
    
    public void setShowMenuBar(boolean showMenuBar){
    	this.showMenuBar = showMenuBar;
    }
    
    public void setShowStatusBar(boolean showStatusBar){
    	this.showStatusBar = showStatusBar;
    }
    
    /**
     * Specify the popup menu customizer to be used the the docking manager.
     * 
     * @param customizer
     */
    public void setPopupMenuCustomizer(PopupMenuCustomizer customizer){
    	this.popupMenuCustomizer = customizer;
    }
    
    /**
     * Specify the tabbed pane customizer to be used by the docking manager
     * 
     * @param tabbedPaneCustomizer
     */
    public void setTabbedPaneCustomizer(DockingManager.TabbedPaneCustomizer tabbedPaneCustomizer){
    	this.tabbedPaneCustomizer = tabbedPaneCustomizer;
    }
    
	/**
	 * Specify the profileKey for persistance of layouts
	 * 
	 * @param profileKey
	 */
	public void setProfileKey(String profileKey){
		this.profileKey = profileKey;
	}
	
	public void setShowWorkspace(boolean showWorkspace){
		this.showWorkspace = showWorkspace;
	}
	
	public void setFloatable(boolean floatable){
		this.floatable = floatable;
	}
	
	public void setHeavyweightComponentEnabled(boolean heavyweightComponentEnabled){
		this.heavyweightComponentEnabled = heavyweightComponentEnabled;
	}
	
	public void setRearrangable(boolean rearrangable){
		this.rearrangable = rearrangable;
	}
	
	public void setContinuousLayout(boolean continuousLayout) {
		this.continuousLayout = continuousLayout;
	}

	public void setDoubleClickAction(int doubleClickAction) {
		this.doubleClickAction = doubleClickAction;
	}

	public void setEasyTabDocking(boolean easyTabDocking) {
		this.easyTabDocking = easyTabDocking;
	}

	public void setGroupAllowedOnSidePane(boolean groupAllowedOnSidePane) {
		this.groupAllowedOnSidePane = groupAllowedOnSidePane;
	}

	public void setLayoutVersion(Short layoutVersion) {
		this.layoutVersion = layoutVersion;
	}

	public void setOutlineMode(int outlineMode) {
		this.outlineMode = outlineMode;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	public void setSensitiveAreaSize(int sensitiveAreaSize) {
		this.sensitiveAreaSize = sensitiveAreaSize;
	}

	public void setShowGripper(boolean showGripper) {
		this.showGripper = showGripper;
	}

	public void setShowTitleBar(boolean showTitleBar) {
		this.showTitleBar = showTitleBar;
	}

	public void setSaveLayoutOnClose(boolean saveLayoutOnClose){
		this.saveLayoutOnClose = saveLayoutOnClose;
	}

	private static class DefaultCustomizer implements DockingManager.TabbedPaneCustomizer{
		@Override
		public void customize(JideTabbedPane tabbedPane) {
			tabbedPane.setShowTabButtons(true);
		}
	}
}

