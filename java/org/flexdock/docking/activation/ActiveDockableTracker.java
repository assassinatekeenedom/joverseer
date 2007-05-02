/*
 * Created on Mar 4, 2005
 */
package org.flexdock.docking.activation;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JTabbedPane;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.util.DockingUtility;
import org.flexdock.util.RootWindow;
import org.flexdock.util.SwingUtility;


/**
 * @author Christopher Butler
 */
public class ActiveDockableTracker {
    public static final String CURRENT_DOCKABLE = "ActiveDockableTracker.CURRENT_DOCKABLE";
    private static final String KEY = "ActiveDockableTracker.KEY";
//    private static final ActiveDockableTracker GLOBAL_TRACKER = new ActiveDockableTracker();
	private static ActiveDockableTracker currentTracker;
	private static final Object LOCK = new Object();
	private Dockable currentDockable;
	private PropertyChangeSupport changeSupport;
	
	
	static {
		initialize();
	}
	
	
	private static void initialize() {
	    // make sure DockingManager has been fully initialized
		Class c = DockingManager.class;
	}
	
	
	public static ActiveDockableTracker getTracker(Component component) {
		RootWindow window = RootWindow.getRootContainer(component);
		return getTracker(window);
	}
	
	public static ActiveDockableTracker getCurrentTracker() {
		synchronized(LOCK) {
			if(currentTracker==null) {
			    Window window = SwingUtility.getActiveWindow();
			    if(window!=null) {
			        currentTracker = getTracker(window);
			    }
			}
			return currentTracker;
		}
	}
	
	private static ActiveDockableTracker getTracker(RootWindow window) {
		if(window==null)
			return null;

		ActiveDockableTracker tracker = (ActiveDockableTracker)window.getClientProperty(KEY);
		
		if(tracker==null) {
			tracker = new ActiveDockableTracker();
			window.putClientProperty(KEY, tracker);
		}
		return tracker;
	}
	
	
	
	public static Dockable getActiveDockable() {
	    ActiveDockableTracker tracker = getCurrentTracker();
	    return tracker==null? null: tracker.currentDockable;
	}
	
	public static Dockable getActiveDockable(Component window) {
	    ActiveDockableTracker tracker = getTracker(window);
	    return tracker==null? null: tracker.currentDockable;
	}
	
	
	
	static void windowActivated(Component c) {
		RootWindow window = RootWindow.getRootContainer(c);
		ActiveDockableTracker tracker = getTracker(window);
		synchronized(LOCK) {
			currentTracker = tracker;
		}
	}

	public static void requestDockableActivation(Component c) {
		requestDockableActivation(c, false);
	}
	
	public static void requestDockableActivation(Component c, boolean forceChange) {
		if(c==null)
			return;
		
		Dockable dockable = DockingUtility.getAncestorDockable(c);
		if(dockable!=null) {
			requestDockableActivation(c, dockable, forceChange);
		}
	}
	
	public static void requestDockableActivation(final Component c, final Dockable dockable, final boolean forceChange) {
		if(c==null || dockable==null)
			return;
		
		// make sure the window is currently active
		SwingUtility.activateWindow(c);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
			    focusDockable(c, dockable, forceChange);
			}
		});
	}
	
	
	
	static void focusDockable(Component child, final Dockable parentDockable, boolean forceChange) {
		// if the dockable is already active, then leave it alone.
	    // skip this check if they're trying to force a change
		if(!forceChange && parentDockable.getDockingProperties().isActive().booleanValue())
			return;

		Component parentComp = parentDockable.getComponent();
		Container focusRoot = parentComp instanceof Container? (Container)parentComp: null;
		Component focuser = focusRoot==null? null: SwingUtility.getNearestFocusableComponent(child, focusRoot);
		if(focuser==null)
			focuser = parentComp;
		focuser.requestFocus();
		
		// if we're in a hidden tab, then bring the tab to the front
		if(parentComp.getParent() instanceof JTabbedPane) {
		    JTabbedPane tabPane = (JTabbedPane)parentComp.getParent();
		    int indx = tabPane.indexOfComponent(parentComp);
		    if(indx!=tabPane.getSelectedIndex())
		        tabPane.setSelectedIndex(indx);
		}
		
		// if the dockable is not currently active, activation may be pending for
		// the next item in the event queue.  dispatch another item to the event queue
		// and if the dockable hasn't been activated in the interim, then activate it.
		if(!DockingUtility.isActive(parentDockable)) {
		    EventQueue.invokeLater(new Runnable() {
		       public void run() {
		           if(!DockingUtility.isActive(parentDockable)) {
		               parentDockable.getDockingProperties().setActive(true);
		           }
		       }
		    });
		}
	}
	
	public ActiveDockableTracker() {
	    changeSupport = new PropertyChangeSupport(this);
	}
	
	public void setActive(boolean b) {
		if(currentDockable==null)
			return;
		
		currentDockable.getDockingProperties().setActive(b);
	}
	
	public void setActive(Dockable dockable) {
		if(dockable!=currentDockable) {
		    Dockable oldValue = currentDockable;
			setActive(false);
			currentDockable = dockable;
			setActive(true);
			changeSupport.firePropertyChange(CURRENT_DOCKABLE, oldValue, dockable);
		}
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	    changeSupport.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
	    changeSupport.removePropertyChangeListener(listener);
	}
	
	public PropertyChangeListener[] getPropertyChangeListeners() {
	    return changeSupport.getPropertyChangeListeners();
	}
	
	public boolean containsPropertyChangeListener(PropertyChangeListener listener) {
	    PropertyChangeListener[] listeners = getPropertyChangeListeners();
	    for(int i=0; i<listeners.length; i++) {
	        if(listeners[i]==listener)
	            return true;
	    }
	    return false;
	}

}
