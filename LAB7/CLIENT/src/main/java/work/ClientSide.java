package work;


import java.util.NoSuchElementException;

/**
 * Входная точка в программу
 * @autor Sobolev Ivan
 * @date 07.04.2022
 * @version 1.0
 */
public class ClientSide {
    public static void main(String[] args) throws ClassNotFoundException {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("Отключение клиента");
                }
            });
            System.out.println("Запуск клиента...");
            ClientConnection client = new ClientConnection();
            client.connection();
        } catch (NoSuchElementException e) {
            // для ctrl+D
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("Ошибка регистрации. Такой пользователь уже существует. Авторизуйтесь заново.");
        }
    }
}
