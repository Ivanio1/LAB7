package data;

import java.io.Serializable;
import java.util.Date;

public class Person implements Serializable {
    private String name;
    public String birthday;
    private Color eyeColor;
    private Country nationality;

    public Person(String name, String birth, Color eyeColor, Country nationality) {
        this.birthday = birth;
        this.eyeColor = eyeColor;
        this.name = name;
        this.nationality = nationality;
    }

    public Person(String name, Color eyeColor, Country nationality) {
        this.eyeColor = eyeColor;
        this.name = name;
        this.nationality = nationality;
    }

    public Person(String name, Color eyeColor) {
        // this.birthday = birthday;
        this.eyeColor = eyeColor;
        this.name = name;
    }

    public Person(String name, Color color, String birth) {
        this.birthday = birth;
        this.eyeColor = color;
        this.name = name;
    }
    public Person(){}


    /**
     * @return Color of the eyes.
     */
    public Color getEyeColor() {
        return eyeColor;
    }

    /**
     * @return Nationality of the person.
     */
    public Country getNationality() {
        return nationality;
    }

    /**
     * @return Name of the person.
     */
    public String getName() {
        return name;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }


    @Override
    public String toString() {
        String s=null;
        if(nationality!=null && birthday!=null){
            s="Person{" +
                    "name='" + name + '\'' +
                    ", eyeColor=" + eyeColor +
                    ", nationality="+nationality+
                    ", birthday="+birthday+
                    '}';
        }
        if(nationality==null && birthday!=null){
            s="Person{" +
                    "name='" + name + '\'' +
                    ", eyeColor=" + eyeColor +
                    ", birthday="+birthday+
                    '}';
        }if(nationality!=null && birthday==null){
            s="Person{" +
                    "name='" + name + '\'' +
                    ", eyeColor=" + eyeColor +
                    ", nationality="+nationality+
                    '}';
        }if(nationality==null && birthday==null){
            s="Person{" +
                    "name='" + name + '\'' +
                    ", eyeColor=" + eyeColor +
                    '}';
        }
        return s;
    }
}
