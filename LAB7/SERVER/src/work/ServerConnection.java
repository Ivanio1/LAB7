package work;

import Manager.CollectionManager;

import Manager.CommandReader;
import work.BDActivity;
import work.ServerReceiver;
import work.ServerSender;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ServerConnection {
    private static final Logger logger = Logger.getLogger("Logger");
    private ServerSide server = new ServerSide();
    private BDActivity bdActivity = new BDActivity();
    private CollectionManager manager = new CollectionManager(bdActivity, server);
    private Scanner scanner = new Scanner(System.in);
    private ExecutorService poolSend = Executors.newCachedThreadPool();
    private ExecutorService poolReceiver = Executors.newFixedThreadPool(2);

    /**
     * Метод реализует соединение и работу с клиентом
     *
     * @throws IOException
     */
    public void connection(String file) throws IOException, ClassNotFoundException, InterruptedException {
        logger.info("Сервер запущен.");
        int i = 0;
        while (true) {
            try {
                System.out.println("Введите порт");
                int port = Integer.parseInt(scanner.nextLine());
                Selector selector = Selector.open();
                try (ServerSocketChannel socketChannel = ServerSocketChannel.open()) {
                    socketChannel.bind(new InetSocketAddress(port));
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_ACCEPT);
                    String infCol = server.loadToCol(file, bdActivity);
                    logger.info("Сервер ожидает подключения клиентов");
                    while (selector.isOpen()) {
                        int count = selector.select();
                        if (count == 0) {
                            continue;
                        }
                        Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                        while (iter.hasNext()) {
                            SelectionKey key = iter.next();
                            try {
                                if (key.isAcceptable()) {
                                    SocketChannel channel = socketChannel.accept();
                                    logger.info("К серверу подключился клиент");
                                    i++;
                                    channel.configureBlocking(false);
                                    channel.register(selector, SelectionKey.OP_WRITE);
                                }
                                if (key.isWritable()) {
                                    if (infCol == null) {
                                        poolSend.execute(new ServerSender(key, "Загружена коллекция размером " + server.getCol().size()));
                                        key.interestOps(SelectionKey.OP_READ);
                                    } else {
                                        poolSend.execute(new ServerSender(key, infCol));
                                        Thread.sleep(1000);
                                        System.exit(1);
                                    }
                                }
                                if (key.isReadable()) {
                                    poolReceiver.submit(new ServerReceiver(key, manager, bdActivity, poolSend));
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                                iter.remove();
                            } catch (CancelledKeyException e) {
                                logger.info("Клиент отключился");
                                logger.info("Сервер ожидает подключения клиентов");
                                i--;
                                if (i == 0) {
                                    logger.info("На сервере нет активного клиента, введите exit и нажмите ENTER - если хотите выключить сервер.");
                                    Runnable save = () -> {
                                        try {
                                            serverMod();
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                    };
                                    new Thread(save).start();

                                }
                            }
                        }
                    }
                }
            } catch (BindException e) {
                logger.info("Такой порт уже используется");
            } catch (NumberFormatException e) {
                logger.info("Порт не число или выходит за пределы");
            } catch (IllegalArgumentException e) {
                logger.info("Порт должен принимать значения от 1 до 65535");
            } catch (SocketException e) {
                logger.info("Недопустимый порт");
            }
        }
    }

    /**
     * Метод для обработки команд save и exit на сервере
     */
    public void serverMod() throws IOException {
        String s = "";
        CommandReader commandReader = new CommandReader();
        CommandDescription command = commandReader.readCommand();
        switch (command.getCommand()) {
            case EXIT:
                logger.info("Программа сервера успешно завершена.");
                System.exit(0);
                break;
            default:
                System.out.println("На сервере поддерживаются только команда exit.");

        }
    }
}