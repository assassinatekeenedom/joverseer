/*
 * Created on 2005-03-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective;



import java.io.Serializable;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.LayoutNode;
import org.flexdock.perspective.event.LayoutListener;

/**
 * @author Mateusz Szczap
 */
public class Perspective implements Cloneable, Serializable {
	private String m_persistentId;
	private String m_perspectiveName;
	private Layout m_layout;
	private LayoutSequence m_initalSequence;

	
	public Perspective(String persistentId, String perspectiveName) {
		this(persistentId, perspectiveName, false);
	}
	
	public Perspective(String persistentId, String perspectiveName, boolean defaultMode) {
		if (persistentId == null) throw new NullPointerException("persistentId cannot be null");
		if (perspectiveName == null) throw new NullPointerException("perspectiveName cannot be null");
		m_persistentId = persistentId;
		m_perspectiveName = perspectiveName;
		m_layout = new Layout();
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#getPerspectiveName()
	 */
	public String getName() {
		return m_perspectiveName;
	}
	
	public String getPersistentId() {
		return m_persistentId;
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#addDockable(java.lang.String, org.flexdock.view.Dockable)
	 */
	public void addDockable(String dockableId) {
		getLayout().add(dockableId);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#removeDockable(java.lang.String)
	 */
	public boolean removeDockable(String dockableId) {
		return (getLayout().remove(dockableId) != null);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#getDockable(java.lang.String)
	 */
	public Dockable getDockable(String dockableId) {
		return (Dockable) getLayout().getDockable(dockableId);
	}
	
	public void addLayoutListener(LayoutListener listener) {
		getLayout().addListener(listener);
	}
	
	public void removeLayoutListener(LayoutListener listener) {
		getLayout().removeListener(listener);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#getDockableIds()
	 */
	public Dockable[] getDockables() {
		return getLayout().getDockables();
	}
	
	public DockingState getDockingState(String dockable) {
		return getLayout().getDockingState(dockable, false);
	}
	
	public DockingState getDockingState(Dockable dockable) {
		return getLayout().getDockingState(dockable, false);
	}

	public DockingState getDockingState(String dockable, boolean load) {
		return getLayout().getDockingState(dockable, load);
	}
	
	public DockingState getDockingState(Dockable dockable, boolean load) {
		return getLayout().getDockingState(dockable, load);
	}
	
	public LayoutSequence getInitialSequence() { 
		return getInitialSequence(false);
	}
	
	public LayoutSequence getInitialSequence(boolean create) {
		if(m_initalSequence==null && create)
			m_initalSequence = new LayoutSequence();
		return m_initalSequence;
	}
	
	public void setInitialSequence(LayoutSequence sequence) { 
		m_initalSequence = sequence;
	}
	
	public Layout getLayout() {
		return m_layout;
	}
    
    public void setLayout(Layout layout) {
        m_layout = layout;
    }
	
	public void reset(DockingPort port) {
		if(m_initalSequence!=null) {
			m_initalSequence.apply(port);
			
			Layout layout = getLayout();
			if(layout!=null)
				layout.update(m_initalSequence);
		}
	}
	
	public void load(DockingPort port) {
		Layout layout = getLayout();
		if(layout.isInitialized())
			layout.apply(port);
		else {
			reset(port);
		}
	}
	
	public void unload() {
		Dockable[] dockables = getLayout().getDockables();
		for(int i=0; i<dockables.length; i++) {
			DockingManager.close(dockables[i]);
		}
	}
	
	public void cacheLayoutState(DockingPort port) {
		if(port!=null) {
			Layout layout = getLayout();
			LayoutNode node = port.exportLayout();
			layout.setRestorationLayout(node);
		}
	}
	
	public Object clone() {
		Perspective clone = new Perspective(m_persistentId, m_perspectiveName);
		clone.m_layout = (Layout)m_layout.clone();
		clone.m_initalSequence = m_initalSequence==null? null: (LayoutSequence)m_initalSequence.clone();
		return clone;
	}
	
}
