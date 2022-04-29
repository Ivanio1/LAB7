package work;

import data.LabWork;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;


/**
 * Класс отвечающий за отправку и получение команд от сервера, выборки конкретной команды
 *
 * @version 1.0
 * @autor Sobolev Ivan
 * @date 17.04.2022
 */
public class ClientWork {


    /**
     * Поле исполнителя команд связанных с вводом команд и их аргументов
     */
    private final CommandReader commandReader = new CommandReader();
    public boolean access;
    private Scanner scanner = new Scanner(System.in);





    /**
     * Основной метод клиента (отправляет необходимый объект на сервер)
     *
     * @param socket
     * @param command
     * @param login
     * @param password
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void acs(Socket socket, String command, String login, String password) throws IOException, ClassNotFoundException {

        if (command.equals("reg")) {
           // System.out.println(login+" "+password+"\n");
            CommandDescription request = new CommandDescription(CommandType.REGISTER, login, password);
            //System.out.println(request.getCommand());System.out.println(request.getLogin()+" "+request.getPassword());
            sendCommand(socket, request);
            getAnswer(socket);
            access=true;
        } else if (command.equals("sign")) {
            CommandDescription request = new CommandDescription(CommandType.SIGN, login, password);
            sendCommand(socket, request);
            getAnswer(socket);
            access=true;
        }
        if (access) {
          //  System.out.println("Вы можете вводить команды");
            while (true) {
                work(socket, login, password);
            }
        }
    }


    /**
     * Метод обрабатывает команды
     *
     * @throws IOException
     */
    public void work(Socket socket,String login, String password) throws IOException {
        try {
            if (commandReader.hasNextLine()) {
                CommandDescription command = commandReader.readCommand();
                //System.out.println(command.getCommand()+command.getArgs());
                switch (command.getCommand()) {
                    case HELP:
                    case INFO:
                    case SHOW:
                    case SAVE:
                    case EXECUTE_SCRIPT:
                    case CLEAR:
                    case MAX_BY_AUTHOR:
                    case REMOVE_BY_ID:
                    case COUNT_BY_DIFFICULTY:
                    case FILTER_GREATER_THAN_MINIMAL_POINT:
                    case REMOVE_FIRST:
                    case REMOVE_AT:
                    case REGISTER:
                    case SIGN:
                        command.setPassword(password);
                        command.setLogin(login);
                        sendCommand(socket, command);
                        getAnswer(socket);
                        break;
                    case ADD:
                    case ADD_IF_MAX:
                        command.setWork(commandReader.readWork(login));
                        command.setLogin(login);
                        sendCommand(socket, command);
                        getAnswer(socket);
                        break;
                    case UPDATE:
                        int id = commandReader.readId();
                        LabWork work = commandReader.readWork(login);
                        work.setId(id);
                        work.setCreationDate(String.valueOf(create_date()));
                        command.setWork(work);
                        command.setLogin(login);
                        sendCommand(socket, command);
                        getAnswer(socket);
                        break;
                    case EXIT:
                        System.out.println("Программа клиента успешно завершена.");
                        System.exit(0);
                        break;

                    default:
                        commandReader.invalidCommand();
                }
            }
        } catch (ArrayIndexOutOfBoundsException | ClassNotFoundException e) {
            System.out.println("Отсутствует аргумент");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод отправляет команду на сервер
     *
     * @param socket
     * @param answer
     * @throws IOException
     */
    public void sendCommand(Socket socket, CommandDescription answer) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream toServer = new ObjectOutputStream(baos);
        baos.flush();
        //System.out.println(answer.);
        toServer.writeObject(answer);
        byte[] out = baos.toByteArray();
        socket.getOutputStream().write(out);
    }

    /**
     * Метод получает результат от сервера
     *
     * @param socket
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void getAnswer(Socket socket) throws IOException, ClassNotFoundException {
        String answer;
        ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
        answer = (String) fromServer.readObject();
        switch (answer) {
            case "exit":
                System.exit(0);
            case "Авторизация прошла успешно":
                access = true;
                System.out.println("Вы успешно авторизованы. Введите help чтобы узнать список доступных команд.");
                break;
            case "Такой пользователь уже существует. Перезагрузите клиент и авторизуйтесь.":
                System.exit(0);
                break;
            default:
                System.out.println(answer);
                break;
        }
    }
    /**
     * @return current date.
     */
    public static Date create_date() {
        return new Date();
    }
}