package data;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class LabWork implements Comparable<LabWork>, Serializable {
    private Integer id;
    private String unique_id;
    private String name;
    private Coordinates coordinates;
    private String creationDate;
    private String date;
    private Double minimalPoint;
    private Difficulty difficulty;
    private Person author;
    private String login;



    public LabWork(int id, String name, Coordinates coordinates, String creationDate, Double minimalPoint, Difficulty difficulty, Person author,String login) {
        this.author = author;
        this.id = id;
        this.creationDate = creationDate;
        this.coordinates = coordinates;
        this.name = name;
        this.difficulty = difficulty;
        this.minimalPoint = minimalPoint;
        this.login = login;
    }

    public LabWork(int id, String name, Coordinates coordinates, String creationDate, Double minimalPoint, Person author, String login) {
        this.author = author;
        this.id = id;
        this.creationDate = creationDate;
        this.coordinates = coordinates;
        this.name = name;
        this.minimalPoint = minimalPoint;
        this.login = login;

    }


    //    /**
//     * @return minimal point.
//     */
//    public Double getMinimalPoint() {
//        return minimalPoint;
//    }
//    /**
//     * @return Integer id.
//     */
//    public Integer getId() {
//        return id;
//    }
//    /**
//     * @return String id.
//     */
//    public String getUnique_id() {
//        return unique_id;
//    }
//
//    public void setLogin(String login) {
//        this.login = login;
//    }
//
//    /**
//     * set id.
//     */
//    public void setUnique_id(String id) {
//        this.unique_id = id;
//    }
//    /**
//     * @return author.
//     */
//    public Person getAuthor() {
//        return author;
//    }
//    /**
//     * @return difficulty.
//     */
//    public Difficulty getDifficulty() {
//        return difficulty;
//    }
//
//    /**
//     *
//     * @return name
//     */
//    public String getName() {
//        return name;
//    }
    public Long getX() {
        return coordinates.getX();
    }

    public Long getY() {
        return coordinates.getY();
    }
//    public Coordinates getCoordinates() {
//        return coordinates;
//    }
//
//    public String getLogin() {
//        return login;
//    }
//
//    /**
//     *
//     * @return дату создания
//     */
//    public Date getCreationDate() {
//        return creationDate;
//    }
//
//    /**
//     *
//     * @return дату создания в формате строки
//     */
//    public String getDate() {
//        return date;
//    }
//
//    /**
//     * Устанавливает id
//     * @param id
//     */
//    public void setId(Integer id) {
//        this.id = id;
//    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }



    @Override
    public String toString() {
        String S = null;
        if (difficulty != null) {
            S = "LabWork {" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", coordinates=" + coordinates.toString() +
                    ", creationDate=" + creationDate +
                    ", minimalPoint=" + minimalPoint +
                    ", difficulty='" + difficulty + '\'' +
                    ", owner=" + author.toString() +
                    '}';
        }
        if (difficulty == null) {
            S = "LabWork {" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", coordinates=" + coordinates.toString() +
                    ", creationDate=" + creationDate +
                    ", minimalPoint=" + minimalPoint +
                    ", owner=" + author.toString() +
                    '}';
        }
        return S;
    }

    @Override
    public int compareTo(LabWork o) {

        return 0;
    }
}
