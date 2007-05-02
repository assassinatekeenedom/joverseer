/* Copyright (c) 2004 Christopher M Butler
 
 Permission is hereby granted, free of charge, to any person obtaining a copy of 
 this software and associated documentation files (the "Software"), to deal in the 
 Software without restriction, including without limitation the rights to use, 
 copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the 
 Software, and to permit persons to whom the Software is furnished to do so, subject 
 to the following conditions:
 
 The above copyright notice and this permission notice shall be included in all 
 copies or substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
 OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. */
package org.flexdock.docking;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

import org.flexdock.docking.event.DockingListener;
import org.flexdock.docking.event.DockingMonitor;
import org.flexdock.docking.props.DockablePropertySet;


/**
 * This interface is designed to specify the API's required by <code>DockingManager</code> and 
 * <code>DockingPort</code> for dealing with dockable components in a drag-n-drop fashion.  A
 * <code>Dockable</code> is the child component that is docked into a <code>DockingPort</code>.
 * 
 * @author Christopher Butler
 */
public interface Dockable extends DockingListener, DockingMonitor {
    
    String DOCKABLE_INDICATOR = "Dockable.DOCKABLE_INDICATOR";
    
    /**
     * Returns the value of the property with the specified key.  Only
     * properties added with <code>putClientProperty</code> will return
     * a non-<code>null</code> value.  
     * 
     * @param key the key that is being queried
     * @return the value of this property or <code>null</code>
     * @see javax.swing.JComponent#getClientProperty(java.lang.Object)
     */
    Object getClientProperty(Object key);
    
    /**
     * Returns the Component that is to be dragged and docked.  This may or may not be included in
     * the list returned by <code>getDragSources()</code>.
     * <br/>
     * The framework performs indexing on the underlying <code>Component</code>.  Consequently, 
     * this method may <b>not</b> return a <code>null</code> reference.
     */
    Component getComponent();
    
    /**
     * Returns the DockingPort within which this Dockable is currently docked.  If not currently docked, 
     * this method will return null.
     */
    DockingPort getDockingPort();
    
    /**
     * Returns a <code>List</code> of the <code>Components</code> that are event sources for drag operations.  
     * The list may or may not include the Component returned by <code>getComponent()</code>.
     */
    List getDragSources();
    
    /**
     * Returns a <code>Set</code> of the <code>Components</code> that are used as frame drag sources.
     * When a <code>Dockable</code> is floated into an external frame, that frame may or may not have
     * a titlebar for repositioning.  The Components returned by this method will be setup with appropriate event 
     * listeners such that dragging them will serve to reposition the containing frame as if they were
     * the frame titlebar.  If a Component exists in both the Set returned by this method and the List
     * returned by <code>getDragSources()</code>, the "frame reposition" behavior will supercede any
     * "drag-to-dock" behavior while the Dockable is in a floating state.  
     */
    Set getFrameDragSources();
    
    /**
     * Returns a <code>String</code> identifier that is unique within a JVM instance, but persistent 
     * across JVM instances.  This is used for configuration mangement, allowing the JVM to recognize
     * a <code>Dockable</code> instance within an application instance, persist the ID, and recall it
     * in later application instances.  The ID should be unique within an appliation instance so that
     * there are no collisions with other <code>Dockable</code> instances, but it should also be 
     * consistent from JVM to JVM so that the association between a <code>Dockable</code> instance and
     * its ID can be remembered from session to session.
     * <br/>
     * The framework performs indexing on the persistent ID.  Consequently, this method may 
     * <b>not</b> return a <code>null</code> reference.
     */		
    String getPersistentId();
    
    /**
     * Adds an arbitrary key/value "client property" to this <code>Dockable</code>.
     * <code>null</code> values are allowed.
     * @see javax.swing.JComponent#putClientProperty(java.lang.Object, java.lang.Object)
     */
    void putClientProperty(Object key, Object value);
    
    /**
     * Returns a <code>DockablePropertySet</code> instance associated with this <code>Dockable</code>.
     * Developers implementing the <code>Dockable</code> interface may or may not choose to 
     * provide their own <code>DockablePropertySet</code> implementation for use with this method.
     * A default implementation is supplied by the framework and most <code>Dockable</code> 
     * implementations, including all implementations provided by the framework, will return 
     * the default <code>DockablePropertySet</code> via a call to 
     * <code>org.flexdock.docking.props.PropertyManager</code>.  Developers are encouraged to 
     * take advantage of this by calling <code>PropertyManager.getDockablePropertySet(this)</code>.
     * 
     * @return the <code>DockablePropertySet</code> associated with this <code>Dockable</code>  This
     * method may not return a <code>null</code> reference.
     * @see org.flexdock.docking.props.DockablePropertySet#
     * @see org.flexdock.docking.props.PropertyManager#getDockablePropertySet(Dockable)
     */
    DockablePropertySet getDockingProperties();
    
    /**
     * Implements the semantics for docking an external <code>Dockable</code> to this 
     * <code>Dockable</code> and returns a <code>boolean</code> indicating whether or not 
     * the docking operation was successful.
     * <br/>
     * The framework already provides a default implementation for this method through 
     * <code>DockingManager.dock(Dockable dockable, Dockable parent)</code>.
     * While users are free to provide their own implementation for this method, the
     * recommended approach is to use the default implementation with the following line:
     * <br/>
     * <code>return DockingManager.dock(dockable, this);</code>
     *
     * @param dockable the <code>Dockable</code> to dock relative to this <code>Dockable</code>
     * @return <code>true</code> if the docking operation was successful; <code>false</code>
     * otherwise.
     * @see #dock(Dockable, String)
     * @see #dock(Dockable, String, float)
     * @see DockingManager#dock(Dockable, Dockable)
     */
    boolean dock(Dockable dockable);
    
    /**
     * Implements the semantics for docking an external <code>Dockable</code> to the 
     * specified region of this <code>Dockable</code> and returns a 
     * <code>boolean</code> indicating whether or not the docking operation was 
     * successful.  If the docking operation results in a split layout, this method
     * should determine an appropriate ratio of available space to allot to the
     * new sibling <code>Dockable</code>.
     * <br/>
     * The framework already provides a default implementation for this method through 
     * <code>DockingManager.dock(Dockable dockable, Dockable parent, String region)</code>.
     * While users are free to provide their own implementation for this method, the
     * recommended approach is to use the default implementation with the following line:
     * <br/>
     * <code>return DockingManager.dock(dockable, this, relativeRegion);</code>
     *
     * @param dockable the <code>Dockable</code> to dock relative to this <code>Dockable</code>
     * @param relativeRegion the docking region into which to dock the specified <code>Dockable</code>
     * @return <code>true</code> if the docking operation was successful; <code>false</code>
     * otherwise.
     * @see #dock(Dockable, String, float)
     * @see DockingManager#dock(Dockable, Dockable, String)
     */
    boolean dock(Dockable dockable, String relativeRegion);
    
    /**
     * Implements the semantics for docking an external <code>Dockable</code> to the 
     * specified region of this <code>Dockable</code> with the specified layout ratio, 
     * returning a <code>boolean</code> indicating whether or not the docking operation 
     * was successful.  If the docking operation results in a split layout, this method
     * should use the specified <code>ratio</code> to determine the amount of available 
     * space to allot to the new sibling <code>Dockable</code>.
     * <br/>
     * The framework already provides a default implementation for this method through 
     * <code>DockingManager.dock(Dockable dockable, Dockable parent, String region, float proportion)</code>.
     * While users are free to provide their own implementation for this method, the
     * recommended approach is to use the default implementation with the following line:
     * <br/>
     * <code>return DockingManager.dock(dockable, this, relativeRegion, ratio);</code>
     *
     * @param dockable the <code>Dockable</code> to dock relative to this <code>Dockable</code>
     * @param relativeRegion the docking region into which to dock the specified <code>Dockable</code>
     * @param ratio the proportion of available space in the resulting layout to allot to the
     * new sibling <code>Dockable</code>.
     * @return <code>true</code> if the docking operation was successful; <code>false</code>
     * otherwise.
     * @see DockingManager#dock(Dockable, Dockable, String, float)
     */
    boolean dock(Dockable dockable, String relativeRegion, float ratio);
    
    void addPropertyChangeListener(PropertyChangeListener listener);
    
    void removePropertyChangeListener(PropertyChangeListener listener);
    
}
