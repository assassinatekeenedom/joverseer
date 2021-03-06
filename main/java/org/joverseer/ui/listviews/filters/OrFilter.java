package org.joverseer.ui.listviews.filters;

import java.util.ArrayList;

import org.joverseer.ui.listviews.AbstractListViewFilter;
/**
 * Filter that combines filters with an or operator
 * 
 * @author Marios Skounakis
 */
public class OrFilter extends AbstractListViewFilter {
	ArrayList<AbstractListViewFilter> filters = new ArrayList<AbstractListViewFilter>();
	public OrFilter() {
		super("");
	}
	
	public void addFilter(AbstractListViewFilter filter) {
		this.filters.add(filter);
	}

	@Override
	public boolean accept(Object obj) {
		for (AbstractListViewFilter filter : this.filters) {
			if (filter == null) continue;
			if (filter.accept(obj)) return true;
		}
		return false;
	}
}
