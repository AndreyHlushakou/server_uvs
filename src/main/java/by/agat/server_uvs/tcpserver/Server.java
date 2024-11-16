package by.agat.server_uvs.tcpserver;

import by.agat.server_uvs.exceptions.tcpserver.ClientCommunicationException;
import by.agat.server_uvs.exceptions.tcpserver.TcpServerException;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class Server {

    private static final byte[] EMPTY_ARRAY = new byte[0];
    private static final int TIME_OUT_MS = 100;

    private final int port;
    private final InetAddress addr;

    private final Map<SocketAddress, SocketChannel> clients = new HashMap<>(); // не канкарент тк будем работать в одном потоке
    private final Queue<SocketAddress> connectedClientsEvents = new ConcurrentLinkedQueue<>();
    private final Queue<SocketAddress> disConnectedClientsEvents = new ConcurrentLinkedQueue<>();
    private final Queue<AddressMessage> messagesForClients = new ArrayBlockingQueue<>(1000);
    private final Queue<AddressMessage> messagesFromClients = new ArrayBlockingQueue<>(1000);

    public Server(int port) {
        this(null, port);
    }

    public Server(InetAddress addr, int port) {
        try {
            Constructor<Unsafe> unsafeConstructor = Unsafe.class.getDeclaredConstructor();
            unsafeConstructor.setAccessible(true);
            unsafe = unsafeConstructor.newInstance();
        } catch (Exception ex) {
            throw new TcpServerException(ex);
        }

        log.debug("addr:{}, port:{}", addr, port);
        this.addr = addr;
        this.port = port;
    }

    private volatile boolean run = true;
    public void start() {
        try {
            try (var serverSocketChannel = ServerSocketChannel.open()) { // открываем соккетЧенел
                serverSocketChannel.configureBlocking(false); // будет работать в неблокирующем режиме
                var serverSocket = serverSocketChannel.socket();
                serverSocket.bind(new InetSocketAddress(addr, port)); // вешаем на адрес и порт
                try (var selector = Selector.open()) { // создаем селектор(мультиплексор)
                    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // интересуют события подключения (селектор подписываем на событие подключения)
                    log.info("server started. addr:{}, port:{}", addr, port);
                    while (run) { // запускаем ивент луп (event loop)
                        handleSelector(selector); // обрабатываем события от селектора
                    }
                    log.info("server stopped. addr:{}, port:{}", addr, port);
                }
            }
        } catch (Exception ex) {
            log.error("error. addr:{}, port:{}", addr, port, ex);
            throw new TcpServerException(ex);
        }
    }

    public void stop() {
        log.info("stop command received. addr:{}, port:{}", addr, port);
        run = false;
    }

    public Queue<SocketAddress> getConnectedClientsEvents() {
        return connectedClientsEvents;
    }

    public Queue<SocketAddress> getDisConnectedClientsEvents() {
        return disConnectedClientsEvents;
    }

    public Queue<AddressMessage> getMessagesFromClients() {
        return messagesFromClients;
    }

    public boolean send(SocketAddress clientAddress, byte[] data) { //метод отправки сообщений клиенту
        var result = messagesForClients.offer(new AddressMessage(clientAddress, data)); // чтобы не заблокировать поток, складываем сообщения в очередь
        log.debug("Scheduled for sending to the client:{}, result:{}", clientAddress, result); //запланировано к отправке
        return result;
    }

    private void handleSelector(Selector selector) {
        try {
            selector.select(this::performIO, TIME_OUT_MS); // ждем когда придет событие (метод блокирующий на этот таймаут т.е сработает либо на событие, либо по прошествию таймаута)
            sendMessagesToClients(); // обрабатываем данные которые необходимо клиенту отправить
        }  catch (ClientCommunicationException ex) {
            var clientAddress = getSocketAddress(ex.getSocketChannel());
            log.error("error in client communication:{}", clientAddress, ex);
            disconnect(clientAddress);
        } catch (IOException ex) {
            log.error("unexpected error:{}", ex.getMessage(), ex);
        }
    }

    private void performIO(SelectionKey selectedKey) {
        if (selectedKey.isAcceptable()) { // или кто-то хочет подключиться
            acceptConnection(selectedKey);
        } else if (selectedKey.isReadable()) {
            readFromClient(selectedKey); // или канал готов, что бы читать из него данные
        }
    }

    private void acceptConnection(SelectionKey key) { // событие подключения
        var serverSocketChannel = (ServerSocketChannel) key.channel(); // на селекторе есть какое-то движение
        try {
            var clientSocketChannel = serverSocketChannel.accept();
            var selector = key.selector();
            log.debug(
                    "accept client connection, key:{}, selector:{}, clientSocketChannel:{}",
                    key,
                    selector,
                    clientSocketChannel);

            clientSocketChannel.configureBlocking(false); // устанавливаем подключение в неблокирующем режиме
            clientSocketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE); // и подписываемся на события чтения и записи

            var remoteAddress = clientSocketChannel.getRemoteAddress();
            clients.put(remoteAddress, clientSocketChannel); // записываем в карту новых клиентов
            connectedClientsEvents.add(remoteAddress); // добавляем в очередь событий, событие подключения нового клиента
        } catch (Exception ex) {
            log.error("can't accept new client on:{}", key);
        }
    }


    private void disconnect(SocketAddress clientAddress) {
        var clientChannel = clients.remove(clientAddress);
        if (clientChannel != null) {
            try {
                clientChannel.close();
            } catch (IOException e) {
                log.error("clientChannel:{}, closing error:{}", clientAddress, e.getMessage(), e);
            }
        }
        disConnectedClientsEvents.add(clientAddress);
    }

    private void readFromClient(SelectionKey selectionKey) { // запускаем обработчик данных
        var socketChannel = (SocketChannel) selectionKey.channel();
        log.debug("{}. read from client", socketChannel);

        var data = readRequest(socketChannel); // получаем данные
        if (data.length == 0) {
            disconnect(getSocketAddress(socketChannel)); // если результат нулевой, то разрываем соединение
        } else {
            messagesFromClients.add(new AddressMessage(getSocketAddress(socketChannel), data)); // если ненулевой, то складываем в событие получения данных
        }
    }

    private SocketAddress getSocketAddress(SocketChannel socketChannel) {
        try {
            return socketChannel.getRemoteAddress();
        } catch (Exception ex) {
            throw new ClientCommunicationException("get RemoteAddress error", ex, socketChannel);
        }
    }

    private static final int RESULT_LIMIT = 102400;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024); // (байтБуффер - дискретная передача данных) создали буфер в хипе // для того чтобы его каждый раз не пересоздавать
    private final List<ByteBuffer> parts = new ArrayList<>();
    private final Unsafe unsafe;

    private byte[] readRequest(SocketChannel socketChannel) {
        try {
            int usedIdx = 0; // индекс используемых элементов
            int readBytesTotal = 0;
            int readBytes;
            while (readBytesTotal < RESULT_LIMIT && (readBytes = socketChannel.read(buffer)) > 0) { // читаем из буфера по 1024 байта - кладем в ридБайтс
                buffer.flip(); // переворот буфера (перевернули ведро)
                if (usedIdx >= parts.size()) {
                    parts.add(ByteBuffer.allocateDirect(readBytes)); // Выделяется массив размером равный количеству прочитанных байтов. Также этот буфер создается(аллоцируется) вне хипа
                }

                if (parts.get(usedIdx).capacity() < readBytes) { // извлекли буфер
                    unsafe.invokeCleaner(parts.get(usedIdx)); //очистка временного буфера
                    parts.add(usedIdx, ByteBuffer.allocateDirect(readBytes));
                }

                parts.get(usedIdx).put(buffer);
                buffer.flip(); // чтобы записать снова что-то из буфера (прочитать соккетЧеннел) надо его снова перевернуть
                readBytesTotal += readBytes;
                usedIdx++;
            }
            log.debug("read bytes:{}, usedIdx:{}", readBytesTotal, usedIdx);

            if (readBytesTotal == 0) { // если прочитали ноль байтов, то значит соккетоное соединение разорвалось и работу надо завершать
                return EMPTY_ARRAY;
            }
            var result = new byte[readBytesTotal];
            var resultIdx = 0;

            for (var idx = 0; idx < usedIdx; idx++) { // если что-то получили, то это что-то складываем в единое целое и результат возвращаем
                var part = parts.get(idx);
                part.flip();
                part.get(result, resultIdx, part.limit());
                resultIdx += part.limit();
                part.flip();
            }
            return result;
        } catch (Exception ex) {
            throw new ClientCommunicationException("Reading error", ex, socketChannel);
        }
    }

    private void sendMessagesToClients() {
        AddressMessage msg;
        while ((msg = messagesForClients.poll()) != null) { // обращаемся к очереди
            var client = clients.get(msg.clientAddress()); // если есть что отправить, то мы берем это
            if (client == null) {
                log.error("client {} not found", msg.clientAddress());
            } else {
                write(client, msg.message()); // и отправляем это клиенту
            }
        }
    }

    private void  write(SocketChannel clientChannel, byte[] data) {
        log.debug("write to client:{}, data.length:{}", clientChannel, data.length);
        var buffer = ByteBuffer.allocate(data.length); // Создаем буфер для отправки нужного размера, т.к. знаем размер того что мы должны отправить
        buffer.put(data); // заполняем буфер
        buffer.flip(); // переворачиваем буфер
        try {
            clientChannel.write(buffer); // извлекаем данные и отправляем
        } catch (Exception ex) {
            throw new ClientCommunicationException("Write to the client error", ex, clientChannel);
        }
    }
}
