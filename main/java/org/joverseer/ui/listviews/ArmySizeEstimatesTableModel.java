package org.joverseer.ui.listviews;

import org.joverseer.tools.armySizeEstimator.ArmySizeEstimate;
import org.springframework.context.MessageSource;

/**
 * Table model for ArmySizeEstimate objects
 * 
 * @author Marios Skounakis
 */
public class ArmySizeEstimatesTableModel extends ItemTableModel {
    public ArmySizeEstimatesTableModel(MessageSource messageSource) {
        super(ArmySizeEstimate.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[]{"type", "size", "min", "max", "countKnown", "countUnknown"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class};
    }

}
