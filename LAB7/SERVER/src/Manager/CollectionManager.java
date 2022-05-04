package Manager;


import java.io.File;
import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.sql.Date;

import data.*;
import work.BDActivity;
import work.ServerSender;
import work.ServerSide;


/**
 * Класс для работы с коллекцией
 *
 * @version 1.0
 * @autor Sobolev Ivan
 */
public class CollectionManager {

    /**
     * Поле коллекция
     */
    private CopyOnWriteArrayList<LabWork> collection;
    /**
     * Поле дата создания
     */
    private java.sql.Date creationDate;
    /**
     * Поле файл, в котором хранится коллекция
     */
    private File file;
    protected static HashMap<String, String> manual;
    private BDActivity bdActivity;
    private ServerSide manager;

    /**
     * Конструктор - создание объекта
     *
     * @param manager - коллекция для сохранения объектов
     */
    public CollectionManager(BDActivity bdActivity, ServerSide manager) {

        this.bdActivity = bdActivity;
        this.manager = manager;
    }

    public CollectionManager() {
    }

    private java.util.Date d = new java.util.Date();
    private Date initDate = new Date(d.getTime());
    private String answer;

    {
        manual = new HashMap<>();
        manual.put("remove_first", "удалить первый элемент из коллекции.");
        manual.put("add", "Добавить новый элемент в коллекцию.");
        manual.put("show", "Вывести в стандартный поток вывода все элементы коллекции в строковом представлении.");
        manual.put("clear", "Очистить коллекцию.");
        manual.put("update", "обновить значение элемента коллекции, id которого равен заданному.");
        manual.put("update_id", "обновить значение id элемента коллекции, id которого равен заданному.");
        manual.put("info", "Вывести в стандартный поток вывода информацию о коллекции.");
        manual.put("remove_at", "удалить элемент, находящийся в заданной позиции коллекции.");
        manual.put("remove_by_id", "удалить элемент из коллекции по его id.");
        manual.put("add_if_max", " добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции.");
        manual.put("exit", "Сохранить коллекцию в файл и завершить работу программы.");
        manual.put("max_by_author", "вывести любой объект из коллекции, значение поля author которого является максимальным.");
        manual.put("count_by_difficulty", "вывести количество элементов, значение поля difficulty которых равно заданному.");
        manual.put("filter_greater_than_minimal_point", "вывести элементы, значение поля minimalPoint которых больше заданного.");
    }

    public void help(ExecutorService poolSend, SelectionKey key) {
        Runnable help = () -> {
            poolSend.submit(new ServerSender(key, "help - вывести справку по доступным командам\n" +
                    "info - вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                    "show - вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                    "add - добавить новый элемент в коллекцию\n" +
                    "update id - обновить значение элемента коллекции, id которого равен заданному\n" +
                    "remove_by_id id - удалить элемент из коллекции по его id\n" +
                    "remove_first - удалить первый элемент из коллекции.\n" +
                    "clear - очистить коллекцию\n" +
                    "execute_script file_name - считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме\n" +
                    "exit - завершить программу (без сохранения в файл)\n" +
                    "remove_at - удалить элемент, находящийся в заданной позиции коллекции.\n" +
                    "remove_by_id - удалить элемент из коллекции по его id.\n" +
                    "max_by_author - вывести любой объект из коллекции, значение поля author которого является максимальным\n" +
                    "count_by_difficulty - вывести количество элементов, значение поля difficulty которых равно заданному.\n" +
                    "filter_greater_than_minimal_point - вывести элементы, значение поля minimalPoint которых больше заданного."));
        };
        new Thread(help).start();
    }

    public void info(ExecutorService poolSend, SelectionKey key) {
        Runnable info = () -> {
            String answer = "Тип коллекции - CopyOnWriteArrayList\n" +
                    "Дата инициализации " + initDate + "\n" +
                    "Размер коллекции " + manager.getCol().size();
            poolSend.submit(new ServerSender(key, answer));
        };
        new Thread(info).start();
    }

    public void show(ExecutorService poolSend, SelectionKey key) {
        Runnable show = () -> {
            if (manager.getCol().size() != 0) {
                Stream<LabWork> stream = manager.getCol().stream();
                poolSend.submit(new ServerSender(key, stream.map(LabWork::toString).collect(Collectors.joining("\n"))));
            } else {
                poolSend.submit(new ServerSender(key, "Коллекция пуста."));
            }
        };
        new Thread(show).start();
    }

    public void add(ExecutorService poolSend, SelectionKey key, LabWork work, String login) {
        Runnable addElement = () -> {
            try {
                int id = (int) bdActivity.getSQLId();
                bdActivity.addToSQL(work, login, id);
                work.setId(id);
                work.setLogin(login);
                manager.getCol().add(work);
                poolSend.submit(new ServerSender(key, "Элемент коллекции добавлен, чтобы сохранить введите save."));
            } catch (SQLException e) {
                poolSend.submit(new ServerSender(key, "Ошибка при работе с БД (вероятно что-то с БД)"));
            } catch (NullPointerException e) {
                poolSend.submit(new ServerSender(key, "Данные в скрипте введены не верно"));
            }
        };
        new Thread(addElement).start();
    }

    public String ADD(LabWork work, String login) {
        String answer = "";
        try {
            int id = (int) bdActivity.getSQLId();
            bdActivity.addToSQL(work, login, id);
            work.setId(id);

            manager.getCol().add(work);
            answer = "Элемент коллекции добавлен, чтобы сохранить введите save.";
        } catch (SQLException e) {
            answer = "Ошибка при работе с БД (вероятно что-то с БД)";
        } catch (NullPointerException e) {
            answer = "Данные в скрипте введены не верно";
        }
        return answer;
    }

    public void add_if_max(ExecutorService poolSend, SelectionKey key, LabWork work, String login) {
        Runnable addElement = () -> {
            if (!(manager.getCol().size() == 0)) {
                Stream<LabWork> stream = manager.getCol().stream();
                Integer nameMAX = stream.filter(col -> col.getName() != null)
                        .max(Comparator.comparingInt(p -> p.getName().length())).get().getName().length();
                if (work.getName() != null && work.getName().length() > nameMAX) {
                    try {
                        int id = (int) bdActivity.getSQLId();
                        bdActivity.addToSQL(work, login, id);
                        work.setId(id);
                        work.setLogin(login);
                        manager.getCol().add(work);
                        poolSend.submit(new ServerSender(key, "Элемент коллекции добавлен, чтобы сохранить введите save."));
                    } catch (SQLException e) {
                        poolSend.submit(new ServerSender(key, "Ошибка при работе с БД (вероятно что-то с БД)."));
                    } catch (NullPointerException e) {
                        poolSend.submit(new ServerSender(key, "Данные в скрипте введены не верно."));
                    }
                } else {
                    poolSend.submit(new ServerSender(key, "Элемент коллекции не сохранен, так как его имя меньше " +
                            "имён других элементов коллекции или равен null."));
                }
            } else {
                poolSend.submit(new ServerSender(key, "Коллекция пуста"));
            }
        };
        new Thread(addElement).start();
    }

    public String ADD_IF_MAX(LabWork work, String login) {
        String answer = "";
        if (!(manager.getCol().size() == 0)) {
            Stream<LabWork> stream = manager.getCol().stream();
            Integer nameMAX = stream.filter(col -> col.getName() != null)
                    .max(Comparator.comparingInt(p -> p.getName().length())).get().getName().length();
            if (work.getName() != null && work.getName().length() > nameMAX) {
                try {
                    int id = (int) bdActivity.getSQLId();
                    bdActivity.addToSQL(work, login, id);
                    work.setId(id);
                    work.setLogin(login);
                    manager.getCol().add(work);
                    answer = "Элемент коллекции добавлен, чтобы сохранить введите save.";
                } catch (SQLException e) {
                    answer = "Ошибка при работе с БД (вероятно что-то с БД).";
                } catch (NullPointerException e) {
                    answer = "Данные в скрипте введены не верно.";
                }
            } else {
                answer = "Элемент коллекции не сохранен, так как его имя меньше " +
                        "имён других элементов коллекции или равен null.";
            }
        } else {
            answer = "Коллекция пуста";
        }
        return answer;
    }

    public void clear(ExecutorService poolSend, SelectionKey key, String login) {
        Runnable clear = () -> {
            try {
                bdActivity.clearSQL(login);
                if (manager.getCol().removeIf(col -> col.getLogin().equals(login))) {
                    poolSend.submit(new ServerSender(key, "Коллекция очищена. Удалены все принадлежащие вам элементы, чтобы сохранить введите save."));
                } else {
                    poolSend.submit(new ServerSender(key, "В коллекции нет элементов принадлежащих пользователю"));
                }
            } catch (SQLException e) {
                poolSend.submit(new ServerSender(key, "Ошибка при работе с БД (вероятно что-то с БД)"));
            }
        };
        new Thread(clear).start();
    }

    public void save(ExecutorService poolSend, SelectionKey key) {
        Runnable save = () -> {
            boolean f = false;
            try {
                bdActivity.clear1SQL();

                for (LabWork work : manager.getCol()) {
                    int id = (int) bdActivity.getSQLId();
                    bdActivity.saveSQL(work, work.getLogin(), id);
                    manager.getCol().remove(work);
                    f = true;
                }
                if (f) {
                    poolSend.submit(new ServerSender(key, "Коллекция сохранена.\n Также Ваши изменения сохранены в таблице final_labworks."));
                }

            } catch (SQLException e) {
                poolSend.submit(new ServerSender(key, "Ошибка при работе с БД (вероятно что-то с БД)"));
            }
        };
        new Thread(save).start();
    }

    public void update(ExecutorService poolSend, SelectionKey key, int id, LabWork work, String login) {
        Runnable update = () -> {
            try {
                if (!(work == null)) {
                    if (!(manager.getCol().size() == 0)) {
                        bdActivity.update(id, login, work);
                        if (manager.getCol().removeIf(col -> col.getId() == id && col.getLogin().equals(login))) {
                            work.setId(id);
                            work.setLogin(login);
                            manager.getCol().add(work);
                            poolSend.submit(new ServerSender(key, "Элемент обновлен, чтобы сохранить введите save."));
                        } else {
                            poolSend.submit(new ServerSender(key, "Элемента с таким id нет или пользователь не имеет доступа к этому элементу"));
                        }
                    } else {
                        poolSend.submit(new ServerSender(key, "Коллекция пуста"));
                    }
                } else {
                    poolSend.submit(new ServerSender(key, "Ошибка при добавлении элемента. Поля указаны не верно"));
                }
            } catch (SQLException e) {
                //e.printStackTrace();
                poolSend.submit(new ServerSender(key, "Ошибка при работе с БД (вероятно что-то с БД)"));
            } catch (NullPointerException e) {
                // e.printStackTrace();
                poolSend.submit(new ServerSender(key, "Данные в скрипте введены не верно"));
            }
        };
        new Thread(update).start();
    }

    public String UPDATE(int id, LabWork work, String login) {
        String answer="";
        try {
            if (!(work == null)) {
                if (!(manager.getCol().size() == 0)) {
                    bdActivity.update(id, login, work);
                    if (manager.getCol().removeIf(col -> col.getId() == id && col.getLogin().equals(login))) {
                        work.setId(id);
                        work.setLogin(login);
                        manager.getCol().add(work);
                        answer= "Элемент обновлен, чтобы сохранить введите save.";
                    } else {
                        answer= "Элемента с таким id нет или пользователь не имеет доступа к этому элементу";
                    }
                } else {
                    answer= "Коллекция пуста";
                }
            } else {
                answer= "Ошибка при добавлении элемента. Поля указаны не верно";
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            answer= "Ошибка при работе с БД (вероятно что-то с БД)";
        } catch (NullPointerException e) {
            // e.printStackTrace();
            answer= "Данные в скрипте введены не верно";
        }
        return answer;
    }

    public void remove_by_id(ExecutorService poolSend, SelectionKey key, String str, String login) {
        Runnable delete = () -> {
            if (!(manager.getCol().size() == 0)) {
                try {
                    int id = Integer.parseInt(str);
                    try {
                        bdActivity.removeById(id, login);
                    } catch (SQLException e) {
                        poolSend.submit(new ServerSender(key, "Ошибка при работе с БД (вероятно что-то с БД)"));
                    } catch (NumberFormatException e) {
                        poolSend.submit(new ServerSender(key, "Неправильный аргумент."));
                    }
                    if (manager.getCol().removeIf(col -> col.getId() == id && col.getLogin().equals(login))) {
                        poolSend.submit(new ServerSender(key, "Элемент удален, чтобы сохранить введите save."));
                    } else
                        poolSend.submit(new ServerSender(key, "Нет элемента с таким id или пользователь не имеет доступа к этому элементу"));
                } catch (NumberFormatException e) {
                    poolSend.submit(new ServerSender(key, "Неправильный аргумент."));
                }
            } else {
                poolSend.submit(new ServerSender(key, "Коллекция пуста"));
            }
        };
        new Thread(delete).start();
    }

    public void remove_at(ExecutorService poolSend, SelectionKey key, String str, String login) {
        Runnable delete = () -> {
            if (!(manager.getCol().size() == 0)) {
                try {
                    int i = Integer.parseInt(str);
                    LabWork type = manager.getCol().get(i);
                    String name = type.getName();
                    String pername = type.getAuthor().getName();
                    String log = type.getLogin();
                    try {
                        bdActivity.removeAt(name, pername, login);
                        if (Objects.equals(log, login)) {
                            manager.getCol().remove(type);
                            poolSend.submit(new ServerSender(key, "Элемент удален, чтобы сохранить введите save."));
                        } else {
                            poolSend.submit(new ServerSender(key, "Объект вам не принадлежит."));
                        }
                    } catch (SQLException e) {
                        poolSend.submit(new ServerSender(key, "Ошибка с БД или объект вам не принадлежит."));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        poolSend.submit(new ServerSender(key, "Элемента по данному индексу нет."));
                    }

                } catch (NumberFormatException exception) {
                    poolSend.submit(new ServerSender(key, "Неверный ввод данных"));
                } catch (ArrayIndexOutOfBoundsException exception) {
                    poolSend.submit(new ServerSender(key, "Элемента по данному индексу нет."));
                }
            } else {
                poolSend.submit(new ServerSender(key, "Коллекция пуста"));
            }
        };
        new Thread(delete).start();
    }

    public void remove_first(ExecutorService poolSend, SelectionKey key, String login) {
        Runnable delete = () -> {
            if (!(manager.getCol().size() == 0)) {
                try {
                    LabWork type = manager.getCol().get(0);
                    String name = type.getName();
                    String pername = type.getAuthor().getName();
                    String log = type.getLogin();
                    try {
                        bdActivity.removeAt(name, pername, login);
                        if (Objects.equals(log, login)) {
                            manager.getCol().remove(0);
                            poolSend.submit(new ServerSender(key, "Элемент удален, чтобы сохранить введите save."));
                        } else {
                            poolSend.submit(new ServerSender(key, "Объект вам не принадлежит."));
                        }

                    } catch (SQLException e) {
                        poolSend.submit(new ServerSender(key, "Ошибка с БД или объект вам не принадлежит."));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        poolSend.submit(new ServerSender(key, "Элемента по данному индексу нет."));
                    }


                } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                    poolSend.submit(new ServerSender(key, "Неверный ввод данных"));
                }
            } else {
                poolSend.submit(new ServerSender(key, "Коллекция пуста"));
            }
        };
        new Thread(delete).start();
    }

    public void maxByAuthor(ExecutorService poolSend, SelectionKey key) {
        Runnable maxByAuthor = () -> {
            String s = null;
            CopyOnWriteArrayList<LabWork> works = manager.getCol();
            if (works.size() != 0) {
                try {
                    ArrayList<String> names = new ArrayList<>();
                    for (LabWork work : works) {
                        String a = work.getAuthor().getName();
                        names.add(a);
                    }
                    String Max_name = "";
                    for(int i=0;i< names.size();i++){
                        if(names.get(i).length()>Max_name.length()){
                            Max_name= names.get(i);
                        }
                    }
                   //System.out.println(Max_name);

                    for (LabWork work : works) {
                        if (Objects.equals(work.getAuthor().getName(), Max_name)) {
                            s = work.toString();
                        }
                    }
                    poolSend.submit(new ServerSender(key, s));
                } catch (NoSuchElementException e) {
                    poolSend.submit(new ServerSender(key, "Элемент не с чем сравнивать. Коллекция пуста."));
                }
            } else poolSend.submit(new ServerSender(key, "Элемент не с чем сравнивать. Коллекция пуста."));

        };
        new Thread(maxByAuthor).start();
    }

    public void countBydiff(ExecutorService poolSend, SelectionKey key, String addCommand) {
        Runnable countBydiff = () -> {
            String s = null;
            CopyOnWriteArrayList<LabWork> works = manager.getCol();
            if (works.size() != 0) {
                int c = 0;
                int n = 0;
                for (LabWork work : works) {
                    if (work.getDifficulty() != null) {

                        if (Objects.equals(work.getDifficulty().toString(), addCommand) && work.getDifficulty() != null) {

                            c += 1;
                        }
                    } else {
                        n += 1;
                        s += "У " + n + " элементов коллекции сложности нет.";
                    }


                }
                if (!Objects.equals(addCommand, "")) {
                    poolSend.submit(new ServerSender(key, "Количество элементов со сложностью " + addCommand + "=" + c));
                } else poolSend.submit(new ServerSender(key, "Вы не ввели сложность."));
            } else poolSend.submit(new ServerSender(key, "Элемент не с чем сравнивать. Коллекция пуста."));
        };
        new Thread(countBydiff).start();
    }

    public void filterGreaterThan(ExecutorService poolSend, SelectionKey key, String point) {
        Runnable maxByAuthor = () -> {
            String s = null;
            CopyOnWriteArrayList<LabWork> works = manager.getCol();
            try {
                if (works.size() != 0) {
                    for (LabWork work : works) {
                        if (work.getMinimalPoint() >= Double.parseDouble(point.trim())) {
                            s += work + "\n";
                        }
                    }
                    poolSend.submit(new ServerSender(key, s));
                } else poolSend.submit(new ServerSender(key, "Элемент не с чем сравнивать. Коллекция пуста."));
            } catch (NumberFormatException e) {
                poolSend.submit(new ServerSender(key, "Неверный формат введенных данных."));
            }
        };
        new Thread(maxByAuthor).start();
    }

    public LabWork script_add(String line, String login) throws ParseException {
        String[] args = line.split(",");
        if (isNumeric(args[1]) && isNumeric(args[2]) && isDouble(args[3])) {
            LabWork W = null;
            try {
                int id = create_id();
                String name = args[0];
                String pname = null;
                Color color = null;
                Country country = null;
                Date birth = null;
                Coordinates coordinates = new Coordinates(Long.parseLong(args[1]), Long.parseLong(args[2]));
                java.util.Date creationDate = java.util.Date.from(Instant.now());
                Double minimalPoint = Double.parseDouble(args[3]);
                // System.out.println(args[0]+args[1]+args[2]+args[3]);
                if (Objects.equals(args[4], "EASY") || Objects.equals(args[4], "HARD") || Objects.equals(args[4], "VERY_HARD") || Objects.equals(args[4], "HOPELESS")) {
                    Difficulty diff = Difficulty.valueOf(args[4]);
                    pname = args[5];
                    color = Color.valueOf(args[6]);
                    if (args.length > 7) {
                        country = Country.valueOf(args[7]);
                        if (args.length > 8) {
                            birth = (Date) new SimpleDateFormat("dd.MM.yyyy").parse(args[8]);
                            Person p = new Person(pname, birth.toString(), color, country);
                            W = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, diff, p, login);

                        } else {
                            Person p = new Person(pname, color, country);
                            W = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, diff, p, login);
                        }
                    } else {
                        Person p = new Person(pname, color);
                        W = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, diff, p, login);
                    }
                } else {
                    pname = args[4];
                    color = Color.valueOf(args[5]);
                    if (args.length > 6) {
                        country = Country.valueOf(args[6]);
                        if (args.length > 7) {
                            birth = (Date) new SimpleDateFormat("dd.MM.yyyy").parse(args[7]);
                            Person p = new Person(pname, birth.toString(), color, country);
                            W = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, p, login);
                        } else {
                            Person p = new Person(pname, color, country);
                            W = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, p, login);
                        }
                    } else {
                        Person p = new Person(pname, color);
                        W = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, p, login);
                    }
                }

            } catch (IllegalArgumentException e) {
                System.out.println("ERROR! Значение поля неверно");
            } catch (NullPointerException e) {
                System.out.println("ERROR! Значение полей неверно");
            }
            return W;
        }
        return null;
    }

    public static boolean isNumeric(String string) {
        int intValue;


        if (string == null || string.equals("")) {

            return false;
        }

        try {
            intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {

        }
        return false;
    }

    public static boolean isDouble(String string) {
        double intValue;


        if (string == null || string.equals("")) {
            ;
            return false;
        }

        try {
            intValue = Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {

        }
        return false;
    }

    public LabWork script_update(String line, String login) throws ParseException {
        String[] args = line.split(",");

        if (isNumeric(args[0]) && isNumeric(args[2]) && isNumeric(args[3]) && isDouble(args[4])) {
            LabWork W = null;
            try {
                int id = Integer.parseInt(args[0].trim());
                String name = args[1];
                String pname = null;
                Color color = null;
                Country country = null;
                Date birth = null;
                Coordinates coordinates = new Coordinates(Long.parseLong(args[2]), Long.parseLong(args[3]));
                java.util.Date creationDate = java.util.Date.from(Instant.now());
                Double minimalPoint = Double.parseDouble(args[4]);
                // System.out.println(args[0]+args[1]+args[2]+args[3]);
                if (Objects.equals(args[5], "EASY") || Objects.equals(args[5], "HARD") || Objects.equals(args[5], "VERY_HARD") || Objects.equals(args[5], "HOPELESS")) {
                    Difficulty diff = Difficulty.valueOf(args[5]);
                    pname = args[6];
                    color = Color.valueOf(args[7]);
                    if (args.length > 8) {
                        country = Country.valueOf(args[8]);
                        if (args.length > 9) {
                            birth = (Date) new SimpleDateFormat("dd.MM.yyyy").parse(args[9]);
                            Person p = new Person(pname, birth.toString(), color, country);
                            W = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, diff, p, login);

                        } else {
                            Person p = new Person(pname, color, country);
                            W = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, diff, p, login);
                        }
                    } else {
                        Person p = new Person(pname, color);
                        W = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, diff, p, login);
                    }
                } else {
                    pname = args[5];
                    color = Color.valueOf(args[6]);
                    if (args.length > 7) {
                        country = Country.valueOf(args[7]);
                        if (args.length > 8) {
                            birth = (Date) new SimpleDateFormat("dd.MM.yyyy").parse(args[8]);
                            Person p = new Person(pname, birth.toString(), color, country);
                            W = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, p, login);
                        } else {
                            Person p = new Person(pname, color, country);
                            W = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, p, login);
                        }
                    } else {
                        Person p = new Person(pname, color);
                        W = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, p, login);
                    }
                }

            } catch (IllegalArgumentException e) {
                System.out.println("ERROR! Значение поля неверно");
            } catch (NullPointerException e) {
                System.out.println("ERROR! Значение полей неверно");
            }
            return W;
        }
        return null;
    }

    /**
     * @return unique number.
     */
    public static int create_id() {
        return (int) Math.round(Math.random() * 32767 * 10);
    }

    public String script_add_if_max(LabWork W) {
        String s;
        if (!this.collection.isEmpty()) {
            LabWork competitor = Collections.max(manager.getCol());

            if (competitor.getName().length() < W.getName().length()) {
                this.collection.add(W);
                s = "Элемент успешно добавлен.";
            } else s = "Не удалось добавить элемент. Он меньше максимального.";
        } else s = "Элемент не с чем сравнивать. Коллекция пуста.";
        return s;
    }

    /**
     * Выводит на экран список доступных пользователю команд.
     */
    public String script_help() {
        String s = "Данные коллекции сохраняются автоматически после каждой успешной модификации.\n" + "Команды: " + manual.keySet();
        return s;

    }

    /**
     * Показывает объекты в коллекции со всеми полями
     */
    public String script_show() {
        String s = "";
        LabWork product;
        if (manager.getCol() != null && !manager.getCol().isEmpty()) {
            for (Iterator<LabWork> var2 = manager.getCol().iterator(); var2.hasNext(); s = s + product.toString() + "\n") {
                product = (LabWork) var2.next();
            }
        } else {
            s = "В коллекции нет элементов";
        }

        return s;
    }

    /**
     * Показывает информацию о коллекции: тип, дата создания, размер
     */
    public String script_info() {
        return "Тип коллекции: " + manager.getCol().getClass().toString() + " дата создания:" + initDate + " размер: " + manager.getCol().size();
    }

    /**
     * Метод для удаления объекта с заданным id
     *
     * @param str - id объекта, который надо удалить
     */
    public String removeByID(String str, String login) {
        String answer = "";
        if (!(manager.getCol().size() == 0)) {
            try {
                int id = Integer.parseInt(str);
                try {
                    bdActivity.removeById(id, login);
                } catch (SQLException e) {
                    answer = "Ошибка при работе с БД (вероятно что-то с БД)";
                } catch (NumberFormatException e) {
                    answer = "Неправильный аргумент.";
                }
                if (manager.getCol().removeIf(col -> col.getId() == id && col.getLogin().equals(login))) {
                    answer = "Элемент удален, чтобы сохранить введите save.";
                } else
                    answer = "Нет элемента с таким id или пользователь не имеет доступа к этому элементу";
            } catch (NumberFormatException e) {
                answer = "Неправильный аргумент.";
            }
        } else {
            answer = "Коллекция пуста";
        }
        return answer;
    }

    /**
     * Удаляет все объекты из коллекции
     */
    public String script_clear(String login) {
        String answer;
        try {
            bdActivity.clearSQL(login);
            if (manager.getCol().removeIf(col -> col.getLogin().equals(login))) {
                answer = "Коллекция очищена. Удалены все принадлежащие вам элементы, чтобы сохранить введите save.";
            } else {
                answer = "В коллекции нет элементов принадлежащих пользователю";
            }
        } catch (SQLException e) {
            answer = "Ошибка при работе с БД (вероятно что-то с БД)";
        }
        return answer;
    }

    /**
     * Метод для удаления объектов по индексу
     *
     * @param str - index
     */
    public String script_removeAt(String str, String login) {
        String answer = "";
        if (!(manager.getCol().size() == 0)) {
            try {
                int i = Integer.parseInt(str);
                LabWork type = manager.getCol().get(i);
                String name = type.getName();
                String pername = type.getAuthor().getName();
                String log = type.getLogin();
                try {
                    bdActivity.removeAt(name, pername, login);
                    if (Objects.equals(log, login)) {
                        manager.getCol().remove(0);
                        answer = "Элемент удален, чтобы сохранить введите save.";
                    } else {
                        answer = "Объект вам не принадлежит.";
                    }
                } catch (SQLException e) {
                    answer = "Ошибка с БД или объект вам не принадлежит.";
                } catch (ArrayIndexOutOfBoundsException e) {
                    answer = "Элемента по данному индексу нет.";
                }

            } catch (NumberFormatException exception) {
                answer = "Неверный ввод данных";
            } catch (ArrayIndexOutOfBoundsException exception) {
                answer = "Элемента по данному индексу нет.";
            }
        } else {
            answer = "Коллекция пуста";
        }
        return answer;
    }

    /**
     * Метод для удаления первого элемента
     */
    public String script_removeFirst(String login) {
        String answer = "";
        if (!(manager.getCol().size() == 0)) {
            try {
                LabWork type = manager.getCol().get(0);
                String name = type.getName();
                String pername = type.getAuthor().getName();
                String log = type.getLogin();
                try {
                    bdActivity.removeAt(name, pername, login);
                    if (Objects.equals(log, login)) {
                        manager.getCol().remove(0);
                        answer = "Элемент удален, чтобы сохранить введите save.";
                    } else {
                        answer = "Объект вам не принадлежит.";
                    }

                } catch (SQLException e) {
                    answer = "Ошибка с БД или объект вам не принадлежит.";
                } catch (ArrayIndexOutOfBoundsException e) {
                    answer = "Элемента по данному индексу нет.";
                }


            } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                answer = "Неверный ввод данных";
            }
        } else {
            answer = "Коллекция пуста";
        }
        return answer;
    }

    public String script_save() {
        String answer = "";
        boolean f = false;
        try {
            bdActivity.clear1SQL();
            for (LabWork work : manager.getCol()) {
                int id = (int) bdActivity.getSQLId();
                bdActivity.saveSQL(work, work.getLogin(), id);
                f = true;
            }
            if (f) {
                answer = "Коллекция сохранена в таблице final_labworks.";
            }

        } catch (SQLException e) {
            answer = "Ошибка при работе с БД (вероятно что-то с БД)";
        }
        return answer;
    }

    /**
     * Выводит любой объект из коллекции, значение поля author которого является максимальным
     */
    public String script_max_by_author() {
        String answer = "";
        String s = null;
        CopyOnWriteArrayList<LabWork> works = manager.getCol();
        if (works.size() != 0) {
            try {
                ArrayList names = new ArrayList<>();
                for (LabWork work : works) {
                    String a = work.getAuthor().getName();
                    names.add(a);
                }

                Comparable name = Collections.max(names);
                String Max_name = name.toString();
                for (LabWork work : works) {
                    if (work.getAuthor().getName() == Max_name) {
                        s = work.toString();
                    }
                }
                answer = s;
            } catch (NoSuchElementException e) {
                answer = "Элемент не с чем сравнивать. Коллекция пуста.";
            }
        } else answer = "Элемент не с чем сравнивать. Коллекция пуста.";
        return answer;
    }

    /**
     * Считает количество элементов, значение поля difficulty которых равно заданному
     */
    public String script_count_by_difficulty(String addCommand) {
        String answer = "";
        String s = null;
        CopyOnWriteArrayList<LabWork> works = manager.getCol();
        if (works.size() != 0) {
            int c = 0;
            int n = 0;
            for (LabWork work : works) {
                if (work.getDifficulty() != null) {

                    if (Objects.equals(work.getDifficulty().toString(), addCommand) && work.getDifficulty() != null) {

                        c += 1;
                    }
                } else {
                    n += 1;
                    s += "У " + n + " элементов коллекции сложности нет.";
                }


            }
            if (!Objects.equals(addCommand, "")) {
                answer = "Количество элементов со сложностью " + addCommand + "=" + c;
            } else answer = "Вы не ввели сложность.";
        } else answer = "Элемент не с чем сравнивать. Коллекция пуста.";
        return answer;
    }

    /**
     * Выводит элементы, значение поля minimalPoint которых больше заданного
     *
     * @param point
     */
    public String script_filter_greater_than_minimal_point(String point) {
        String answer = "";
        String s = null;
        CopyOnWriteArrayList<LabWork> works = manager.getCol();
        try {
            if (works.size() != 0) {
                for (LabWork work : works) {
                    if (work.getMinimalPoint() >= Double.parseDouble(point.trim())) {
                        s += work + "\n";
                    }
                }
                answer = s;
            } else answer = "Элемент не с чем сравнивать. Коллекция пуста.";
        } catch (NumberFormatException e) {
            answer = "Неверный формат введенных данных.";
        }
        return answer;
    }
}
