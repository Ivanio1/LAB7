package work;

import data.LabWork;


import java.io.Serializable;

/**
 * Класс инкапсулирующий в себя тип команды и её аргументы
 * @autor Sobolev Ivan
 * @date 07.04.2022
 * @version 1.0
 */
public class CommandDescription implements Serializable {

    /** Поле типа команды */
    private CommandType command;
    /** Поле аргументов */
    private String args;
    private String login;
    private String password;
    private static final long serialVersionUID = 17L;

    private LabWork work;

    /**
     * Конструктор - создание нового объекта с определенными значениями
     * @param command - тип команды
     * @param args - аргументы команды
     */
    public CommandDescription(CommandType command, String args) {
        this.command = command;
        this.args = args;
    }
    public CommandDescription(CommandType command, String args,String login,String password) {
        this.command = command;
        this.args = args;
        this.login=login;
        this.password=password;
    }
public CommandDescription(CommandType type,String login, String password, LabWork work){
        this.command=type;
        this.login=login;
        this.password=password;
        this.work=work;
}

    public CommandDescription(CommandType command, LabWork product) {
        this.command = command;
        this.work = product;
    }

    public CommandDescription(CommandType command, String args, LabWork product) {
        this.command = command;
        this.args = args;
        this.work = product;
    }

    public CommandDescription() {
    }

    public CommandDescription(CommandType register, String login, String password) {
        this.command = register;
        this.login=login;
        this.password=password;
    }

    public void setCommand(CommandType command) {
        this.command = command;
    }

    /**
     * Метод получения поля типа команды
     * @return command - тип команды
     */
    public CommandType getCommand() {
        return command;
    }

    /**
     * Метод получения поля аргументов
     * @return command - аргументы
     */
    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public LabWork getWork() {
        return work;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return "CommandDescription{" +
                "command=" + command +
                ", args='" + args + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", work=" + work +
                '}';
    }

    public void setWork(LabWork work) {
        this.work = work;
    }
}