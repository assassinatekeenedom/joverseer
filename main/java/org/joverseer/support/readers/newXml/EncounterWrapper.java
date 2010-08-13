package org.joverseer.support.readers.newXml;

import org.joverseer.domain.Encounter;
import org.joverseer.support.StringUtils;

public class EncounterWrapper {
	int reacting;
	String hex;
	String charName;
	String text;
	String header;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCharName() {
		return charName;
	}
	public void setCharName(String charName) {
		this.charName = charName;
	}
	public String getHex() {
		return hex;
	}
	public void setHex(String hex) {
		this.hex = hex;
	}
	public int getReacting() {
		return reacting;
	}
	public void setReacting(int reacting) {
		this.reacting = reacting;
	}
	
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public Encounter getEncounter() {
        Encounter e = new Encounter();
        if (getHeader() == null) {
        	return null;
        }
        charName = StringUtils.getUniquePart(getHeader(), "Encounter for ", " at" , false, false);
        hex = StringUtils.getUniquePart(getHeader(), charName + " at ", "\\.", false, false);
        
        e.setCharacter(getCharName());
        try {
        	e.setHexNo(Integer.parseInt(getHex()));
        }
        catch (Exception exc) {
        	return null;
        }
        e.setDescription(getText());
        return e;
    }
}
