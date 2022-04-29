package work;

import work.ClientWork;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientConnection {
    private ClientWork client = new ClientWork();
    private Scanner scanner = new Scanner(System.in);
    private String login;
    private String password;

    /**
     * Метод реализует соединение между клиентом и сервером
     */
    public void connection() throws ClassNotFoundException {
        while (true) {
            try {
                System.out.println("Введите порт для подключения:");
                int port = Integer.parseInt(scanner.nextLine());
                System.out.println("Введите хост сервера:");
                String host = scanner.nextLine();
                System.out.println("Соединение... Ожидайте");
                SocketAddress socketAddress = new InetSocketAddress(host, port);
                try (Socket socket = new Socket()) {
                    socket.connect(socketAddress, 5000);
                    System.out.println("Соединение установлено");
                    client.getAnswer(socket);
                    while (true) {
                        sign(socket);
                    }
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Время подключения вышло. Хост указан неверно или сервер недоступен");
            } catch (NumberFormatException e) {
                System.out.println("Порт не число или выходит за пределы");
            } catch (ConnectException e) {
                System.out.println("Порт не найден или недоступен");
            } catch (UnknownHostException e) {
                System.out.println("Хост введен неверно");
            } catch (IllegalArgumentException e) {
                System.out.println("Порт должен принимать значения от 1 до 65535");
            } catch (IOException e) {
                client.access = false;
                System.out.println("Нет связи с сервером. Подключиться ещё раз введите (да) или (нет)?");
                String answer;
                while (!(answer = scanner.nextLine()).equals("да")) {
                    switch (answer) {
                        case "":
                            break;
                        case "нет":
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Введите корректное значение.");
                    }
                }
            }
        }
    }

    /**
     * Метод отправляет логин и пароль для регистрации или авторизации
     */
    public void sign(Socket socket) throws IOException, ClassNotFoundException {
        String command;
        String log;
        String pass;
        while (true) {
            System.out.println("Вы зарегистрированы?. Введите (yes) или (no)");
            String regist = scanner.nextLine();
            if (regist.equals("no")) {
                System.out.println("Тогда зарегистрируемся.");
                checkLoginPassword();
                command = "reg";
                log=login;
                pass=password;
                break;
            } else if (regist.equals("yes")) {
                System.out.println("Тогда авторизуемся.");
                checkLoginPassword();
                command = "sign";
                log=login;
                pass=password;
                break;
            }
        }
        client.acs(socket, command, log, pass);
    }

    /**
     * Метод просит пользователя ввести логин и пароль
     */
    private void checkLoginPassword() {
        while (true) {
            System.out.println("Введите логин");
            login = scanner.nextLine();
            if (login.equals("")) {
                System.out.println("Логин не может быть пустой строкой");
            } else {
                break;
            }
        }
        while (true) {
            System.out.println("Введите пароль");
            password = scanner.nextLine();
            if (password.equals("")) {
                System.out.println("Пароль не может быть пустой строкой");
            } else {
                break;
            }
        }
    }
}