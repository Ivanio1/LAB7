package work;

import data.LabWork;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Входная точка в программу
 *
 * @version 1.0
 * @author Sobolev Ivan
 * @date 07.04.2022
 */
public class ServerSide {
    private CopyOnWriteArrayList<LabWork> col = new CopyOnWriteArrayList<>();
    private static final Logger logger = Logger.getLogger("Logger");
    private String file;

    /**
     * Основной метод сервера
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        logger.info("Запуск сервера");
        ServerConnection serverConnection = new ServerConnection();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    logger.info("Отключение сервера");
                }
            });
            serverConnection.connection(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.log(Level.SEVERE, "Вы не ввели имя файла");
        } catch (NoSuchElementException e) {
            //Для ctrl+D
        }
    }

    /**
     * Метод помещает данные из SQL таблицы в коллекцию
     *
     * @return
     */
    public String loadToCol(String file, BDActivity bdActivity) throws ClassNotFoundException {
        this.file=file;
        try {
            this.col = bdActivity.loadFromSQL(file);

            for (LabWork work : this.col) {
                int id = (int) bdActivity.getSQLId();
                int ID= work.getId();
                bdActivity.addToSQL(work, work.getLogin(), id);
                bdActivity.rm_rf(work.getLogin(),work.getName(), ID);
                this.col.remove(work);
            }
            this.col = bdActivity.loadFromSQL(file);
            //System.out.println(col);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Сервер не подключился к БД");
            return "Ошибка сервер не может подключиться к БД (вероятно что-то с БД)";
        } catch (IOException e) {
            // e.printStackTrace();
            logger.log(Level.SEVERE, "Сервер не подключился к БД.");
            return "Файл с данными БД не найден";
        } catch (NullPointerException e) {
            return null;
        }
        return null;
    }

    public CopyOnWriteArrayList<LabWork> getCol() {
        return col;
    }

    public String getFile() {
        return file;
    }

    public void setCol(CopyOnWriteArrayList<LabWork> col) {
        this.col = col;
    }
}
