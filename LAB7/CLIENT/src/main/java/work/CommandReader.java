package work;

import data.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.sql.Date;

public class CommandReader {
    public CommandReader() {
    }

    /**
     * Поле типа команды
     */
    private CommandType command;
    /**
     * Поле названия команды
     */
    private String userCommand;
    /**
     * Поле типа команды и ее аргументы
     */
    private String[] finalUserCommand = new String[2];

    /**
     * Сканер для чтения информации из командной строки
     */
    Scanner scanner = new Scanner(System.in);


    /**
     * Mетод для обработки нелегитимной команды
     */
    public void invalidCommand() {
        System.out.println("такой команды не существует, для справки введите команду help");
    }

    /**
     * Метод для работы со скриптом
     *
     * @param t - путь к файлу
     * @return boolean, вошла ли программа в режим скрипта
     */
    public boolean executeScript(String t) {
        File scriptFile = null;
        FileReader fileReader = null;
        boolean flag = false;
        try {
            scriptFile = new File(t);
            fileReader = new FileReader(scriptFile);
            scanner = new Scanner(fileReader);
            flag = true;

        } catch (FileNotFoundException e) {
            System.out.println("файл не найден");
        }
        return flag;
    }

    /**
     * @return unique number.
     */
    public static int create_id() {
        return (int) Math.round(Math.random() * 32767 * 10);
    }

    /**
     * Метод для проверки при работе со скриптом
     *
     * @return boolean, остались ли строки в файле
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Метод для возращения к работе интерактивного режима
     */
    public void interactiveMod() {
        scanner = new Scanner(System.in);
    }

    /**
     * Метод для чтения команды из командной строки
     *
     * @return возвращает объект класса CommandDescription, тип команды и ее аргументы
     */
    public CommandDescription readCommand() {
        userCommand = scanner.nextLine();
        finalUserCommand = userCommand.trim().split(" ");
        try {
            command = CommandType.valueOf(finalUserCommand[0].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            command = CommandType.INVALID_COMMAND;
        }
        if (finalUserCommand.length > 1) {
            return new CommandDescription(command, finalUserCommand[1]);
        } else {
            return new CommandDescription(command, "");
        }
    }

    /**
     * Вспомогательный метод для чтения Id из командной строки
     *
     * @return возвращает объект класса String
     */
    public int readId() {
        Scanner scanner = new Scanner(System.in);

        Integer y = null;
        do {
            System.out.println("Введите id, не может быть null.");
            String s = scanner.nextLine();
            if (s.equals("")) {
                y = null;

            } else {
                try {
                    y = Integer.parseInt(s);
                } catch (IllegalArgumentException e) {
                    System.out.println("id - обязано быть числом без каких-либо разделителей.");
                }
            }
        } while (y == null);
        return y;
    }

    /**
     * Чтение данных из скрипта
     *
     * @param argument
     * @return
     */
    public String readScript(String argument) {
        StringBuilder builder = new StringBuilder();
        try {
            if (argument == null) {
                builder = null;
                throw new WrongAmountOfElementsException();
            } else {
                System.out.println("Выполняю скрипт '" + argument + "'...");
                String[] userCommand = {"", ""};
                try (Scanner scriptScanner = new Scanner(new File(argument))) {
                    if (!scriptScanner.hasNext()) throw new NoSuchElementException();
                    do {
                        userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);

                        //System.out.println(userCommand[0]);

                        if (userCommand[0].equals("update_id")) {
                            String[] comands = new String[]{"execute_script", "save", "remove_first", "add", "remove_greater", "show", "clear", "update_id", "info", "help", "man", "remove_at_index", "remove_by_id", "add_if_max", "exit", "max_by_author", "count_by_difficulty", "filter_greater_than_minimal_point"};
                            String line = (scriptScanner.nextLine().trim());
                            if (!(Arrays.asList(comands)).contains(line)) {
                                userCommand[1] = line;
                            } else {
                                userCommand[1] = "0";
                                System.out.println("Отсутствует аргумент.");
                            }

                        }
                        if (userCommand[0].equals("add")) {
                            StringBuilder command = new StringBuilder();
                            String[] comands = new String[]{"execute_script", "save", "remove_first", "add", "remove_greater", "show", "clear", "update_id", "info", "help", "man", "remove_at_index", "remove_by_id", "add_if_max", "exit", "max_by_author", "count_by_difficulty", "filter_greater_than_minimal_point"};
                            String line = (scriptScanner.nextLine().trim() + " ").split(" ")[0];
                            while (!(Arrays.asList(comands)).contains(line)) {
                                command.append(line + " ");
                                line = scriptScanner.nextLine();
                            }
                            //System.out.println(command);

                            userCommand[1] = command.toString();

                        }
                        if (userCommand[0].equals("execute_script")) {
                            //System.out.println("Рекурсия скрипта. В скрипте не может быть команды execute_script");
                            throw new ScriptRecursionException();
                        }
                        String out = userCommand[0] + " " + userCommand[1];
                        builder.append(out + ';');
                    } while (scriptScanner.hasNextLine());
                } catch (FileNotFoundException exception) {
                    System.out.println("Файл со скриптом не найден!");
                } catch (NoSuchElementException exception) {
                    System.out.println("Файл со скриптом пуст!");
                } catch (ScriptRecursionException exception) {
                    System.out.println("Скрипты не могут вызываться рекурсивно!");
                } catch (IllegalStateException exception) {
                    System.out.println("Непредвиденная ошибка!");
                    System.exit(0);
                }
            }
        } catch (WrongAmountOfElementsException exception) {
            System.out.println("Некорректные команды в скрипте!");
        }
        // System.out.println(commands);
        return String.valueOf(builder).trim();
    }

    /**
     * Метод для чтения объекта LabWork из командной строки
     *
     * @return возвращает объект класса LabWork
     */
    public LabWork readWork(String login) throws ParseException {
        LabWork W = null;
        int id = create_id();
        String name = readWorkName();
        Coordinates coordinates = readCoordinates();
        java.util.Date creationDate = java.util.Date.from(Instant.now());
        Double minimalPoint = readMinimalPoint();
        Difficulty difficulty = readDifficulty();
        Person author = readPerson();
        if (difficulty != null) {
            LabWork work = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, difficulty, author, login);
            W = work;
        }
        if (difficulty == null) {
            LabWork work = new LabWork(id, name, coordinates, creationDate.toString(), minimalPoint, author, login);
            W = work;
        }

        return W;
    }


    /**
     * Вспомогательный метод для чтения объекта Coordinates из командной строки
     *
     * @return возвращает объект класса Coordinates
     */
    public Coordinates readCoordinates() {
        Scanner scanner = new Scanner(System.in);
        Long x = null;
        Long y = null;
        do {
            System.out.println("Введите x, не может быть null.");
            String s = scanner.nextLine();
            if (s.equals("")) {
                x = null;

            } else {
                try {
                    x = Long.parseLong(s);
                } catch (IllegalArgumentException e) {
                    System.out.println("x - обязано быть числом без каких-либо разделителей.");
                }
            }
        } while (x == null);

        do {
            System.out.println("Введите y, не может быть null.");
            String s = scanner.nextLine();
            if (s.equals("")) {
                y = null;

            } else {
                try {
                    y = Long.parseLong(s);
                } catch (IllegalArgumentException e) {
                    System.out.println("y - обязано быть числом без каких-либо разделителей.");
                }
            }
        } while (y == null);

        return new Coordinates(x, y);
    }

    /**
     * Вспомогательный метод для чтения строки из командной строки
     *
     * @return возвращает объект класса String
     */
    public String readWorkName() {
        String name = "";
        while (name.equals("")) {
            System.out.println("Введите название лабораторной работы, поле не может быть пустой строкой");
            name = scanner.nextLine();
        }
        // System.out.println(name);
        return name;
    }

    /**
     * Вспомогательный метод для чтения строки из командной строки
     *
     * @return возвращает объект класса String
     */
    public String readPersonName() {
        String name = "";
        while (name.equals("")) {
            System.out.println("Введите имя автора, поле не может быть пустой строкой");
            name = scanner.nextLine();
        }
        // System.out.println(name);
        return name;
    }

    public String readLogin() {
        String name = "";
        while (name.equals("")) {
            System.out.println("Введите логин автора, поле не может быть пустой строкой");
            name = scanner.nextLine();
        }
        // System.out.println(name);
        return name;
    }

    /**
     * Вспомогательный метод для чтения цены из командной строки
     *
     * @return возвращает цену Double
     */
    public Double readMinimalPoint() {
        Double point = null;
        do {
            System.out.println("Введите MinimalPoint, поле не может быть null, Значение поля должно быть больше 0");
            String s = scanner.nextLine();
            try {
                point = Double.parseDouble(s);
                if (point <= 0) {
                    System.out.println("MinimalPoint - обязано быть вещественным числом > 0. Формат ввода: 100.0 или 100");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("MinimalPoint - обязано быть вещественным числом > 0. Формат ввода: 100.0 или 100");
            }


        } while ((point == null) || (point <= 0));
        return point;
    }

    /**
     * Вспомогательный метод для чтения строки из командной строки
     *
     * @return возвращает объект класса String
     */
    public Date readBirth() throws ParseException, WrongDate {
        String name = "";
        String[] arr = new String[0];
        boolean flag = Boolean.TRUE;
        java.sql.Date date = null;
        while (flag) {
            try {
                System.out.println("Введите день рождения автора(Формат ввода: DD.MM.YYYY,HH.MM.SS). Можно не заполнять - Enter." + "\nВнимательно вводите данные, чтобы избежать ошибки(Например: часы не могут быть больше 23).");
                String s = scanner.nextLine();
                if (s.equals("")) {
                    name = null;
                    date = null;
                    flag = Boolean.FALSE;
                } else {
                    name = s;
                    String[] date_test = name.split(",");
                    String[] date_1 = date_test[0].split("\\.");
                    String[] date_2 = date_test[1].split("\\.");
                    if (Integer.parseInt(date_1[0]) <= 0 || Integer.parseInt(date_1[0]) > 31 || Integer.parseInt(date_1[1]) <= 0 || Integer.parseInt(date_1[1]) > 12 || Integer.parseInt(date_2[0]) < 0 || Integer.parseInt(date_2[0]) > 23 || Integer.parseInt(date_2[1]) < 0 || Integer.parseInt(date_2[1]) > 59 || Integer.parseInt(date_2[2]) < 0 || Integer.parseInt(date_2[2]) > 59) {
                        System.out.println("Неверный формат даты! Введите заново.");
                        flag = Boolean.TRUE;
                    } else {
                        date = (Date) new SimpleDateFormat("dd.MM.yyyy,hh.mm.ss").parse(name);
                        flag = Boolean.FALSE;
                    }
                }

            } catch (IllegalArgumentException e) {
                System.out.println("ERROR! Неправильный формат даты");
                flag = Boolean.TRUE;
            }
        }


        return date;
    }

    /**
     * Вспомогательный метод для чтения объекта UnitOfMeasure из командной строки
     *
     * @return возвращает объект класса UnitOfMeasure
     */
    public Difficulty readDifficulty() {
        Difficulty diff = null;
        boolean flag = Boolean.TRUE;
        while (flag) {
            try {
                System.out.println("Введите Difficulty, значение поля может быть равно: EASY, HARD, VERY_HARD, HOPELESS. Можно не заполнять - ENTER.");
                String s = scanner.nextLine();
                if (s.equals("")) {
                    diff = null;
                    flag = Boolean.FALSE;
                } else {
                    diff = Difficulty.valueOf(s);
                    flag = Boolean.FALSE;
                }

            } catch (IllegalArgumentException e) {
                System.out.println("ERROR! Значение поля может быть равно: EASY, HARD, VERY_HARD, HOPELESS");
                flag = Boolean.TRUE;
            }
        }

        return diff;
    }

    /**
     * Вспомогательный метод для чтения объекта Person из командной строки
     *
     * @return возвращает объект класса Person
     */
    public Person readPerson() throws ParseException {
        String name = readPersonName();
        Color color = readColor();
        Country nationality = readNationality();
        Date birth = readBirth();
        //System.out.println(name+color+nationality+birth);

        if (nationality != null && birth != null) {
            return new Person(name, birth.toString(), color, nationality);
        }
        if (nationality == null && birth != null) {
            return new Person(name, color, birth.toString());
        }
        if (nationality != null && birth == null) {
            return new Person(name, color, nationality);
        }
        if (nationality == null && birth == null) {
            return new Person(name, color);
        }

        return null;
    }

    /**
     * Вспомогательный метод для чтения Color из командной строки
     *
     * @return возвращает объект Color
     */
    public Color readColor() {
        Color color = null;
        while (color == null) {
            try {
                System.out.println("Введите цвет глаз автора, значение поля может быть равно: RED, GREEN, ORANGE,BLACK, BROWN");
                color = Color.valueOf(scanner.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println("значение поля может быть равно: RED, GREEN, ORANGE,BLACK, BROWN");
            }
        }
        return color;
    }

    /**
     * Вспомогательный метод для чтения объекта Location из командной строки
     *
     * @return возвращает объект класса Location
     */
    public Country readNationality() {
        Country c = null;
        boolean flag = true;
        while (flag) {
            try {
                System.out.println("Введите национальность автора, значение поля может быть равно: USA, GERMANY, INDIA,VATICAN, SOUTH_KOREA. Поле может быть пустым - ENTER");
                String s = scanner.nextLine();
                if (s.equals("")) {
                    c = null;
                    flag = false;
                } else {
                    c = Country.valueOf(s);
                    flag = false;
                }

            } catch (IllegalArgumentException e) {
                System.out.println("ERROR! Значение поля может быть равно: USA, GERMANY, INDIA,VATICAN, SOUTH_KOREA");
                flag = true;
            }
        }


        return c;
    }


}