package data;

import java.io.Serializable;

/**
 * Enumeration with difficulty constants.
 */
public enum Difficulty implements Serializable {
    EASY,
    HARD,
    VERY_HARD,
    HOPELESS;

    /**
     * Generates a list of enum values.
     * @return String with all enum values splitted by comma.
     */
    public static String nameList() {
        String nameList = "";
        for (Difficulty Type : values()) {      //синтаксис ForEach
            nameList += Type.name() + ", ";
        }
        return nameList.substring(0, nameList.length()-2); //обрезание запятой и пробела
    }
}
