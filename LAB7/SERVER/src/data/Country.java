package data;

import java.io.Serializable;

/**
 * Enumeration with country name constants.
 */
public enum Country implements Serializable {
    USA,
    GERMANY,
    INDIA,
    VATICAN,
    SOUTH_KOREA;

    /**
     * Generates a list of enum values.
     * @return String with all enum values splitted by comma.
     */
    public static String nameList() {
        String nameList = "";
        for (Country Type : values()) {      //синтаксис ForEach
            nameList += Type.name() + ", ";
        }
        return nameList.substring(0, nameList.length()-2); //обрезание запятой и пробела
    }
}
