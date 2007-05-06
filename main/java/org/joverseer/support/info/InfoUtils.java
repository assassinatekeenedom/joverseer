package org.joverseer.support.info;

import java.util.HashMap;

import org.joverseer.domain.ArmyElementType;
import org.joverseer.support.AsciiUtils;

public class InfoUtils {

    public static Boolean isDragon(String charName) {
        Info info = InfoRegistry.instance().getInfo("dragons");
        if (info == null)
            return null;
        if (info.getRowIdx(charName) > -1)
            return true;
        return false;
    }

    public static String getCharacterStatsFromTitle(String title) {
        Info info = InfoRegistry.instance().getInfo("characterTitles");
        if (info == null)
            return null;
        for (int i = 1; i < info.getColumnHeaders().size(); i++) {
            for (int j = 1; j < info.getRowHeaders().size(); j++) {
                if (info.getValue(j, i).equals(title)) {
                    return info.getValue(j, 0);
                }
            }
        }
        return null;
    }

    public static String getCharacterStatsTypeFromTitle(String title) {
        Info info = InfoRegistry.instance().getInfo("characterTitles");
        if (info == null)
            return null;
        for (int i = 1; i < info.getColumnHeaders().size(); i++) {
            for (int j = 1; j < info.getRowHeaders().size(); j++) {
                if (info.getValue(j, i).equals(title)) {
                    return info.getValue(0, i);
                }
            }
        }
        return null;
    }
    
    public static String getHealthRangeFromWounds(String woundsDescription) {
    	Info info = InfoRegistry.instance().getInfo("characterWounds");
        if (info == null)
            return null;
        int i = info.getRowIdx(woundsDescription);
        return info.getValue(i, 1);
    }

    public static ArmyElementType getElementTypeFromDescription(String description) {
        Info info = InfoRegistry.instance().getInfo("troopTypeDescriptions");
        if (info == null)
            return null;
        description = AsciiUtils.convertNonAscii(description);
        for (int j = 1; j < info.getRowHeaders().size(); j++) {
            if (info.getValue(j, 3).equals(description)) {
                String t = info.getValue(j, 2);
                if (t.equals("1")) {
                    return ArmyElementType.HeavyCavalry;
                } else if (t.equals("2")) {
                    return ArmyElementType.LightCavalry;
                } else if (t.equals("3")) {
                    return ArmyElementType.HeavyInfantry;
                } else if (t.equals("4")) {
                    return ArmyElementType.LightInfantry;
                } else if (t.equals("5")) {
                    return ArmyElementType.Archers;
                } else if (t.equals("6")) {
                    return ArmyElementType.MenAtArms;
                }; 
                return null;
            }
        }
        return null;
    }
    
    public static String getArmyWareTypeRange(String description) {
        Info info = InfoRegistry.instance().getInfo("armyWareTypes");
        if (info == null)
            return null;
        for (int j = 1; j < info.getRowHeaders().size(); j++) {
            if (info.getValue(j, 0).equals(description)) {
                String t = info.getValue(j, 1);
                return t;
            }
        }
        return null;
    }
    
    public static String getArmyTrainingRange(String description) {
    	Info info = InfoRegistry.instance().getInfo("armyTrainingDescriptions");
        if (info == null)
            return null;
        for (int j = 1; j < info.getRowHeaders().size(); j++) {
            if (info.getValue(j, 0).toString().toLowerCase().equals(description.toLowerCase())) {
                String t = info.getValue(j, 1);
                return t;
            }
        }
        return null;
    }
    
    public static String getArmyLossesRange(String description) {
    	Info info = InfoRegistry.instance().getInfo("armyLossesDescriptions");
        if (info == null)
            return null;
        for (int j = 1; j < info.getRowHeaders().size(); j++) {
            if (info.getValue(j, 0).toString().toLowerCase().equals(description.toLowerCase())) {
                String t = info.getValue(j, 1);
                return t;
            }
        }
        return null;
    }
    
    public static String getArmyMoraleRange(String description) {
    	Info info = InfoRegistry.instance().getInfo("armyMoraleDescriptions");
        if (info == null)
            return null;
        for (int j = 1; j < info.getRowHeaders().size(); j++) {
            if (description.toLowerCase().indexOf(info.getValue(j, 0).toString().toLowerCase()) > -1) {
                String t = info.getValue(j, 1);
                return t;
            }
        }
        return null;
    }
}
