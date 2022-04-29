package Manager;

import data.*;
import work.CommandDescription;
import work.CommandType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.Date;

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
        finalUserCommand = userCommand.trim().split(" ", 2);
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
                                command.append(line + ",");
                                line = scriptScanner.nextLine();
                            }
                            //System.out.println(command);

                            userCommand[1] = command.toString()+";"+line;


                        }
                        if (userCommand[0].equals("update")) {
                            StringBuilder command = new StringBuilder();
                            String[] comands = new String[]{"execute_script", "save", "remove_first", "add", "remove_greater", "show", "clear", "update_id", "info", "help", "man", "remove_at_index", "remove_by_id", "add_if_max", "exit", "max_by_author", "count_by_difficulty", "filter_greater_than_minimal_point"};
                            String line = (scriptScanner.nextLine().trim() + " ").split(" ")[0];
                            while (!(Arrays.asList(comands)).contains(line)) {
                                command.append(line + ",");
                                line = scriptScanner.nextLine();
                            }
                            //System.out.println(command);

                            userCommand[1] = command.toString()+";"+line;


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




}