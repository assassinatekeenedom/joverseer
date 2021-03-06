package org.joverseer.metadata.domain;

import java.io.Serializable;

/**
 * Enumeration for allegiances. 
 * 
 * Values are according to the xml turn file values.
 * 
 * @author Marios Skounakis
 *
 */
public enum NationAllegianceEnum implements Serializable {
    FreePeople (1),
    DarkServants (2),
    Neutral (3);

    private final int allegiance;

    NationAllegianceEnum(int allegiance) {
       this.allegiance = allegiance;
   }
    
   public int getAllegiance() {
       return this.allegiance;
   }
}
