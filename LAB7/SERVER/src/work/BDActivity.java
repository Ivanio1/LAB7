package work;

import Manager.CollectionManager;
import data.*;

import java.awt.image.ColorConvertOp;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BDActivity {
    private ResultSet res;
    private Connection connect;
    private PreparedStatement ps;
    private Statement statement;
    private LabWork work;
    private MessageDigest hash;
    private static final Logger logger = Logger.getLogger("Logger");
    public int index = 1;

    /**
     * Метод подключает сервер к БД и загружает данные из таблицы
     *
     * @param file
     * @return
     * @throws ClassNotFoundException
     */
    public CopyOnWriteArrayList<LabWork> loadFromSQL(String file) throws ClassNotFoundException, IOException, SQLException, NullPointerException {
        CopyOnWriteArrayList<LabWork> col = new CopyOnWriteArrayList<>();
        FileInputStream bd = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(bd);
        String url = properties.getProperty("BD.location");
        String login = properties.getProperty("BD.login");
        String password = properties.getProperty("BD.password");
        Class.forName("org.postgresql.Driver");
        connect = DriverManager.getConnection(url, login, password);
        statement = connect.createStatement();
        res = statement.executeQuery("SELECT * FROM labworks;");
        Person person;
        Color color;
        Difficulty difficulty;
        LabWork work;
        String birth;
        Country nationality;
        while (res.next()) {
            int id = res.getInt("id");
            String name = res.getString("name");
            long x = res.getLong("x");
            long y = res.getLong("y");
            Coordinates coordinates = new Coordinates(x, y);
            String creationDate = res.getString("creationdate");
            Double minimalPoint = res.getDouble("minimalPoint");

            try {
                difficulty = Difficulty.valueOf(res.getString("difficulty"));
            } catch (IllegalArgumentException e) {
                difficulty = null;
            }
            String perName = res.getString("pername");
            try {
                birth = res.getString("birthDate");
            } catch (IllegalArgumentException e) {
                birth = null;
            }
            color = Color.valueOf(res.getString("eyeColor"));
            try {
                nationality = Country.valueOf(res.getString("nationality"));
            } catch (IllegalArgumentException e) {
                nationality = null;
            }
            login=res.getString("login");
            work = new LabWork(id, name, coordinates, creationDate, minimalPoint, difficulty, new Person(perName, birth, color, nationality),login);
            col.add(work);
        }
        logger.info("Сервер подключился к БД");
        return col;
    }

    /**
     * Метод регистрирует пользователя в БД
     *
     * @param command
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String registration(CommandDescription command) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String s = null;
        boolean f = true;
        try {
            hash = MessageDigest.getInstance("SHA-224");
            ResultSet rs = statement.executeQuery("SELECT login, password FROM labwork_login_password;");
            while (rs.next()) {

                if (Objects.equals(rs.getString(1), command.getLogin())) {
                    s = "Такой пользователь уже существует. Перезагрузите клиент и авторизуйтесь.";
                    f = true;
                    break;
                } else {
                    f = false;
                }
            }
            if (!f) {
                PreparedStatement ps = connect.prepareStatement("INSERT INTO labwork_login_password (login, password) VALUES (?, ?);");
                ps.setString(1, command.getLogin());
                ps.setString(2, Base64.getEncoder().encodeToString(hash.digest(command.getPassword().getBytes("UTF-8"))));
                ps.execute();
                logger.info("Пользователь с логином " + command.getLogin() + " успешно зарегистрирован");
                s = "Регистрация прошла успешно. Вы можете вводить команды.";
            }
        } catch (
                SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при работе с БД (вероятно что-то с БД)");

        }
        return s;
    }


    /**
     * Метод авторизует пользователя
     *
     * @param command
     * @return
     * @throws UnsupportedEncodingException
     */
    public boolean authorization(CommandDescription command) throws
            UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            hash = MessageDigest.getInstance("SHA-224");
            ps = connect.prepareStatement("SELECT * FROM labwork_login_password WHERE (login = ?);");
            ps.setString(1, command.getLogin());
            res = ps.executeQuery();
            res.next();
            return Base64.getEncoder().encodeToString(hash.digest(command.getPassword().getBytes("UTF-8")))
                    .equals(res.getString("password"));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при работе с БД (вероятно что-то с БД)");
            return false;
        }
    }

    /**
     * Метод добавляет элемент в БД
     *
     * @param work
     * @param login
     * @throws SQLException
     */
    public void addToSQL(LabWork work, String login, int id) throws SQLException, NullPointerException {
        ps = connect.prepareStatement("INSERT INTO labworks (id, name, x, y, " +
                "creationdate, minimalPoint, difficulty, pername, birthDate, eyeColor, nationality, login) " +
                "VALUES (? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        ps.setInt(1, id);
        ps.setString(2, work.getName());
        ps.setLong(3, work.getCoordinates().getX());
        ps.setLong(4, work.getCoordinates().getY());
        ps.setString(5, String.valueOf(work.getCreationDate()));
        ps.setDouble(6, work.getMinimalPoint());
        try {
            ps.setString(7, String.valueOf(work.getDifficulty()));
        } catch (NullPointerException e) {
            ps.setObject(7, null);
        }
        ps.setString(8, work.getAuthor().getName());
        try {
            ps.setString(9, String.valueOf(work.getAuthor().getBirthday()));
        } catch (NullPointerException e) {
            ps.setObject(9, null);
        }
        ps.setString(10, String.valueOf(work.getAuthor().getEyeColor()));
        try {
            ps.setString(11, String.valueOf(work.getAuthor().getNationality()));
        } catch (NullPointerException e) {
            ps.setObject(11, null);
        }
        ps.setString(12, login);
        ps.execute();
    }
    /**
     * Метод добавляет элемент в БД
     *
     * @param work
     * @param login
     * @throws SQLException
     */
    public void saveSQL(LabWork work, String login, int id) throws SQLException, NullPointerException {
        ps = connect.prepareStatement("INSERT INTO final_labworks (id, name, x, y, " +
                "creationdate, minimalPoint, difficulty, pername, birthDate, eyeColor, nationality, login) " +
                "VALUES (? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        ps.setInt(1, id);
        ps.setString(2, work.getName());
        ps.setLong(3, work.getCoordinates().getX());
        ps.setLong(4, work.getCoordinates().getY());
        ps.setString(5, String.valueOf(work.getCreationDate()));
        ps.setDouble(6, work.getMinimalPoint());
        try {
            ps.setString(7, String.valueOf(work.getDifficulty()));
        } catch (NullPointerException e) {
            ps.setObject(7, null);
        }
        ps.setString(8, work.getAuthor().getName());
        try {
            ps.setString(9, String.valueOf(work.getAuthor().getBirthday()));
        } catch (NullPointerException e) {
            ps.setObject(9, null);
        }
        ps.setString(10, String.valueOf(work.getAuthor().getEyeColor()));
        try {
            ps.setString(11, String.valueOf(work.getAuthor().getNationality()));
        } catch (NullPointerException e) {
            ps.setObject(11, null);
        }
        ps.setString(12, login);
        ps.execute();
    }

    /**
     * Метод получает сгенерированное id
     *
     * @return
     * @throws SQLException
     */
    public long getSQLId() throws SQLException {
        ResultSet res = statement.executeQuery("SELECT nextval('idsequence');");
        res.next();
        return res.getInt(1);
    }

    /**
     * Метод удаляет все элементы из SQL принадлежавшие одному пользователю
     *
     * @param login
     * @throws SQLException
     */
    public void clearSQL(String login) throws SQLException {
        ps = connect.prepareStatement("DELETE FROM labworks WHERE login = ?;");
        ps.setString(1, login);
        ps.execute();
    }
    public void clear1SQL() throws SQLException {
        ps = connect.prepareStatement("DELETE FROM final_labworks;");
        ps.execute();
    }

    public void rm_rf(String login,String name,int id) throws SQLException {
        ps = connect.prepareStatement("DELETE FROM labworks WHERE (login = ?) AND (name =?) AND (id=?);");
        ps.setString(1, login);
        ps.setString(2, name);
        ps.setInt(3, id);
        ps.execute();
    }

    /**
     * Удаляет все элементы из БД по индексу
     *
     * @throws SQLException
     */
    public void removeAt(String name, String pername, String login) throws SQLException {
            ps = connect.prepareStatement("DELETE FROM labworks WHERE (name=?) AND (pername=?) AND (login = ?)");
            ps.setString(1, name);
            ps.setString(2, pername);
            ps.setString(3, login);
            ps.execute();

    }


    /**
     * Удаляет элемент из БД по его id
     *
     * @param id
     * @param login
     * @throws SQLException
     */
    public void removeById(int id, String login) throws SQLException {
        ps = connect.prepareStatement("DELETE FROM labworks WHERE(id = ?) AND (login = ?)");
        ps.setInt(1, id);
        ps.setString(2, login);
        ps.execute();
    }


    /**
     * Метод обновляет в БД элемент по его id
     *
     * @param id
     * @param login
     * @throws SQLException
     */
    public void update(int id, String login,LabWork work) throws SQLException, NullPointerException {
       // System.out.println(work.getAuthor().getName());
        ps = connect.prepareStatement("UPDATE labworks SET name = ? , x = ? , y = ?" +
                ", creationdate = ?, minimalPoint = ? , difficulty = ?, pername = ?, birthDate = ?, eyeColor = ? " +
                ", nationality = ? WHERE id = ? AND login = ?;");
        ps.setString(1, work.getName());
        ps.setLong(2, work.getCoordinates().getX());
        ps.setLong(3, work.getCoordinates().getY());
       // System.out.println(work.getCreationDate()+" "+work.getAuthor().getBirthday());
        ps.setString(4, String.valueOf(work.getCreationDate()));
        ps.setDouble(5, work.getMinimalPoint());
        try {
            ps.setString(6, String.valueOf(work.getDifficulty()));
        } catch (NullPointerException e) {
            ps.setObject(6, null);
        } try {
            ps.setString(6, String.valueOf(work.getDifficulty()));
        } catch (NullPointerException e) {
            ps.setObject(6, null);
        }
        ps.setString(7, work.getAuthor().getName());
        try {
            ps.setString(8, String.valueOf(work.getAuthor().getBirthday()));
        } catch (NullPointerException e) {
            ps.setObject(8, null);
        }
        ps.setString(9, String.valueOf(work.getAuthor().getEyeColor()));
        try {
            ps.setString(10, String.valueOf(work.getAuthor().getNationality()));
        } catch (NullPointerException e) {
            ps.setObject(10, null);
        }
        ps.setInt(11, id);
        ps.setString(12, login);
        ps.execute();
    }
}