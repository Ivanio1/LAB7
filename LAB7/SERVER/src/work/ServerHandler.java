package work;


import Manager.CollectionManager;
import Manager.CommandReader;
import data.LabWork;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ServerHandler {
    private static final Logger logger = Logger.getLogger("Logger");
    public CommandReader commandReader = new CommandReader();
    private boolean interactiveMod;
    private ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
    private boolean flag = false;

    /**
     * Метод регистрирует, авторизует пользователя или выполняет команду
     */
    public void handler(CommandDescription command, CollectionManager manager, BDActivity bdActivity, ExecutorService poolSend, SelectionKey key) {
        try {
            if (command.getCommand() == CommandType.REGISTER) {
                String line = bdActivity.registration(command);
                if (Objects.equals(line, "Такой пользователь уже существует. Перезагрузите клиент и авторизуйтесь.")) {
                    logger.info("Пользователь ввел неверный пароль или такой пользователь уже существует");
                    forkJoinPool.submit(new ServerSender(key, "Такой пользователь уже существует. Перезагрузите клиент и авторизуйтесь."));
                } else {
                    forkJoinPool.submit(new ServerSender(key, line));
                }

            } else if (command.getCommand() == CommandType.SIGN) {
                if (bdActivity.authorization(command)) {
                    logger.info("Пользователь с логином " + command.getLogin() + " успешно авторизован.");
                    forkJoinPool.submit(new ServerSender(key, "Авторизация прошла успешно. Вы можете вводить команды."));
                } else {
                    logger.info("Пользователь ввел неверный пароль");
                    logger.info("Завершение работы сервера.");
                    forkJoinPool.submit(new ServerSender(key, "Введён неверный логин или пароль. У вас нет полномочий на сервере. Перезагрузите сервер и зайдите заново."));
                    System.exit(0);
                }
            } else {
                // System.out.println(command.getCommand());
                switch (command.getCommand()) {
                    case HELP:
                        manager.help(poolSend, key);
                        break;
                    case REMOVE_AT:
                        manager.remove_at(poolSend, key, command.getArgs(), command.getLogin());
                        break;
                    case REMOVE_FIRST:
                        manager.remove_first(poolSend, key, command.getLogin());
                        break;
                    case INFO:
                        manager.info(poolSend, key);
                        break;
                    case SHOW:
                        manager.show(poolSend, key);
                        break;
                    case COUNT_BY_DIFFICULTY:
                        manager.countBydiff(poolSend, key, command.getArgs());
                        break;
                    case FILTER_GREATER_THAN_MINIMAL_POINT:
                        manager.filterGreaterThan(poolSend, key, command.getArgs());
                        break;
                    case ADD:
                        manager.add(poolSend, key, command.getWork(), command.getLogin());
                        break;
                    case UPDATE:
                        manager.update(poolSend, key, command.getWork().getId(), command.getWork(), command.getLogin().trim());
                        break;
                    case MAX_BY_AUTHOR:
                        manager.maxByAuthor(poolSend, key);
                        break;
                    case REMOVE_BY_ID:
                        manager.remove_by_id(poolSend, key, command.getArgs(), command.getLogin());
                        break;
                    case CLEAR:
                        manager.clear(poolSend, key, command.getLogin());
                        break;
                    case SAVE:
                        manager.save(poolSend, key);
                        break;
                    case ADD_IF_MAX:
                        manager.add_if_max(poolSend, key, command.getWork(), command.getLogin());
                        break;
                    case EXECUTE_SCRIPT:
                        System.out.println("Скрипт в доработке");
                        String s=scriptMod(manager, command.getArgs(),command);
                        poolSend.submit(new ServerSender(key, s));
                        break;
                    default:
                        System.out.println("Invalid Command");
                }
                logger.info("Обработана команда " + command.getCommand());
            }
        } catch (NoSuchAlgorithmException |
                UnsupportedEncodingException e) {
            // Все под контролем
        }
    }

    /**
     * Метод обработки команды скрипт
     */
    public String scriptMod(CollectionManager manager, String arg,CommandDescription comm) {
        String arr = commandReader.readScript(arg);
        if (!Objects.equals(arr, "")) {
            String s = "";
            String[] Arr = arr.split(";");
            for (String command : Arr) {
                String[] finalUserCommand = command.split(" ", 2);
                try {
                    switch (finalUserCommand[0]) {
                        case "":
                            break;
                        case "remove_first":
                            s += (manager.script_removeFirst(comm.getLogin())) + "\n";
                            break;
                        case "add":
                            if (finalUserCommand[1] != null) {
                                s += manager.ADD(manager.script_add(finalUserCommand[1],comm.getLogin()),comm.getLogin()) + "\n";
                            } else {
                                s += "Неверный ввод данных в скрипте. ";
                            }
                            break;
                        case "update":
                            if (finalUserCommand[1] != null) {
                                LabWork work=manager.script_update(finalUserCommand[1],comm.getLogin());
                                s += manager.UPDATE(work.getId(),work,comm.getLogin()) + "\n";
                            } else {
                                s += "Неверный ввод данных в скрипте. ";
                            }
                            break;
                        case "remove_by_id":
                            s += manager.removeByID(finalUserCommand[1].trim(),comm.getLogin()) + "\n";
                            break;
                        case "remove_at":
                            s += manager.script_removeAt((finalUserCommand[1].trim()),comm.getLogin()) + "\n";
                            break;
                        case "show":
                            s += manager.script_show();
                            break;
                        case "clear":
                            s += manager.script_clear(comm.getLogin()) + "\n";
                            break;
                        case "save":
                            s += manager.script_save();
                            break;
                        case "info":
                            s += manager.script_info() + "\n";
                            break;
                        case "add_if_max":
                            s += manager.ADD_IF_MAX(manager.script_add(finalUserCommand[1],comm.getLogin()),comm.getLogin());
                            break;
                        case "help":
                            s += manager.script_help() + "\n";
                            break;
                        case "exit":
                            s += "\nПроцесс завершён." + "\n";
                            System.exit(0);
                            break;
                        case "max_by_author":
                            s += manager.script_max_by_author() + "\n";
                            break;
                        case "execute_script":
                            s += "Рекурсия скрипта." + "\n";
                            break;
                        case "count_by_difficulty":
                            s += manager.script_count_by_difficulty(finalUserCommand[1].trim()) + "\n";
                            break;
                        case "filter_greater_than_minimal_point":
                            s += manager.script_filter_greater_than_minimal_point(finalUserCommand[1].trim()) + "\n";
                            break;
                        default:
                            s += "Неопознанная команда. Наберите 'help' для справки." + "\n";
                    }

                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.out.println("Отсутствует аргумент.");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            this.commandReader.interactiveMod();
            this.interactiveMod = true;
            return s;
        }
        return "Файл со скриптом пуст";
    }
}
