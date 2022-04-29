package data;

import java.io.Serializable;

/**
 * Enumeration with color name constants.
 */
public enum Color implements Serializable {
    GREEN,
    RED,
    BLACK,
    ORANGE,
    BROWN;

    /**
     * Generates a list of enum values.
     * @return String with all enum values splitted by comma.
     */
    public static String nameList() {
        String nameList = "";
        for (Color colorType : values()) {      //синтаксис ForEach
            nameList += colorType.name() + ", ";
        }
        return nameList.substring(0, nameList.length()-2); //обрезание запятой и пробела
    }
}
