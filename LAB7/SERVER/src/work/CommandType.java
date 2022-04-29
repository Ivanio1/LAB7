package work;

import java.io.Serializable;

/**
 * Класс enum связанный с командами
 * @autor Sobolev Ivan
 * @date 07.04.2022
 * @version 1.0
 */
public enum CommandType implements Serializable {
    HELP("help"),
    REGISTER("reg"),
    SIGN("sign"),
    INFO("info"),
    SHOW("show"),
    ADD("add"),
    UPDATE_ID("update_id"),
    UPDATE("update"),
    SAVE("save"),
    REMOVE_BY_ID("remove_by_id"),
    CLEAR("clear"),
    EXECUTE_SCRIPT("execute_script"),
    EXIT("exit"),
    ADD_IF_MAX("add_if_max"),
    REMOVE_FIRST("remove_first"),
    REMOVE_AT("remove_at"),
    COUNT_BY_DIFFICULTY("count_by_difficulty"),
    FILTER_GREATER_THAN_MINIMAL_POINT("filter_greater_than_minimal_point"),
    MAX_BY_AUTHOR("max_by_author"),
    INVALID_COMMAND("invalid_command");

    /** Поле прописное название команды */
    private final String commandName;

    /**
     * Конструктор - создание нового объекта Enum с определенными значениями
     * @param commandName - название команды
     */
    CommandType(String commandName){
        this.commandName = commandName;
    }


    /**
     * Метод получения поля названия команды
     * @return commandName - название команды
     */
    public String getCommandName(){
        return commandName;
    }
}
