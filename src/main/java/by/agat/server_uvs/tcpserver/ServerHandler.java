package by.agat.server_uvs.tcpserver;

import by.agat.server_uvs.httpserver.entities.UvsData;
import by.agat.server_uvs.httpserver.service.UvsDataService;
import by.agat.server_uvs.httpserver.utils.MappingUtils;
import by.agat.server_uvs.tcpserver.packed_maz.Packed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

import static by.agat.server_uvs.exceptions.tcpserver.IncorrectMessageLog.incorrectMessageLogging;

@Slf4j
@Component
public class ServerHandler implements Runnable
{
//    public static void main(String[] args) {
//        new ServerHandler().run();
//    }
//
//    public ServerHandler() {
//        this.port = 700;
//        this.mappingUtils = new MappingUtils();
//    }

    private final int port;
    private final UvsDataService uvsDataService;
    private final Map<SocketAddress, List<byte[]>> mapClientsMessage = new ConcurrentHashMap<>();
    private final Map<SocketAddress, List<byte[]>> mapServerMessage = new ConcurrentHashMap<>();
    private final Queue<byte[]> mapClientsMessagePositionData = new ConcurrentLinkedQueue<>();
    private final MappingUtils mappingUtils;

    @Autowired
    public ServerHandler(@Value("${tspServer.port}") int port, UvsDataService uvsDataService, MappingUtils mappingUtils) {
        this.port = port;
        this.uvsDataService = uvsDataService;
        this.mappingUtils = mappingUtils;
        new Thread(this).start();
    }


    @Override
    public void run() {

        Server server = new Server(port); // создаем сервер с портом

        try {// Создаем потоки для: 1) сервера, 2) бизнес логики (2-подключение/отключение Клиентов), 3) принятие сообщений, 4) обработка сообщений
            var executor = Executors.newFixedThreadPool(4);
            executor.submit(server::start); //запускаем сервер

            executor.submit(() -> { // запускаем "бизнес логику"
                log.info("handleConnectedClientsEvents and handleDisConnectedClientsEvents started");
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        handleConnectedClientsEvents(server); // обрабатываем события подключения клиентов
                        handleDisConnectedClientsEvents(server); // обрабатываем события отключения клиентов
                    } catch (Exception ex) {
                        log.error("error. connected/disconnected exception:{}, stackTrace:\n{}", ex.getMessage(), ex.getStackTrace());
                        ex.printStackTrace();
                    }
                }
            });

            executor.submit(() -> { // обрабатываем пришедшие сообщения в отдельном потоке
                log.info("handleClientsMessages started");
                while (!Thread.currentThread().isInterrupted()) { // обрабатываем сообщения от клиентов
                    try {
                        handleClientsMessages(server);
                    }catch (Exception ex) {
                        log.error("error. message exception:{}, stackTrace:\n{}", ex.getMessage(), ex.getStackTrace());
                        ex.printStackTrace();
                    }
                }
            });

            executor.submit(() -> { // обрабатываем данные о местоположении в отдельном потоке
                log.info("handleMessagePositionData started");
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        handleMessagePositionData();
                    }catch (Exception ex) {
                        log.error("error. MessagePositionData exception:{}, stackTrace:\n{}", ex.getMessage(), ex.getStackTrace());
                        ex.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleConnectedClientsEvents(Server server) {
        var newClient = server.getConnectedClientsEvents().poll(); // обращаемся к внутренней очереди сервера подключения клиентов
        if (newClient != null) { // проверяем есть ли событие подключения
//            var res = server.send(newClient, "hello".getBytes(StandardCharsets.UTF_8)); // отправляем приветственное сообщение подключившемуся клиенту
//            logger.info("sending result:{}", res); // выводим сообщение о подключении

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            log.info("new client:{}", newClient); // выводим сообщение о подключении
            mapClientsMessage.put(newClient, new ArrayList<>()); // добавляем в карты со входящими и ответными массивами байт этого клиента
            mapServerMessage.put(newClient, new ArrayList<>());

        }
    }

    private void handleDisConnectedClientsEvents(Server server) {
        var disconnectedClient = server.getDisConnectedClientsEvents().poll(); // обращаемся к внутренней очереди сервера отключения клиентов
        if (disconnectedClient != null) {
            log.info("disconnectedClient:{}", disconnectedClient); // выводим сообщение об отключении

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            mapClientsMessage.remove(disconnectedClient); // удаляем этого клиента
            mapServerMessage.remove(disconnectedClient);
        }
    }


    private void handleClientsMessages(Server server) {
        AddressMessage addressMessage = server.getMessagesFromClients().poll(); // обращаемся к внутренней очереди сервера сообщений от клиентов
        if (addressMessage != null) {
            var clientAddress = addressMessage.clientAddress();
            var requestMessage = addressMessage.message(); // получаем сообщения от клиента из очереди
            log.info("from:{}, messageFromClient.length:{}", clientAddress, requestMessage.length); // выводим длину этого сообщения

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////

            if (true
//                    isCheckSum(requestMessage)
            ) { // проверка чексуммы
                mapClientsMessagePositionData.offer(requestMessage);
            }

            mapClientsMessage.get(clientAddress).add(requestMessage); // добавляем сообщения в карты
            mapServerMessage.get(clientAddress).add(new byte[]{});

            //В ОТВЕТ КЛИЕНТУ НИЧЕГО НЕ ОТПРАВЛЯЕМ!!!!!!!!!!

            StringBuilder report = new StringBuilder("\nClientsMessage:\n");
            for (byte datum : requestMessage) {
                report.append(String.format("%02X ", datum));
            }

            log.info(report.toString()); // выводим сообщения принятые и отправленные

        }
    }

    private boolean isCheckSum(byte[] data) {
        short sizeMessage = getShortNumber(data[11], data[12]);
        short checkByteFromMessage = getShortNumber(data[29+sizeMessage+1], data[29+sizeMessage+2]); // контрольная сумма из пакета

        //???????????????????????????????????? как они ее считают...
        short checkByteCalculated = (short) (data[1] & 0xFF); // вычисляем контрольную сумму
        for (int i = 2; i < data.length-2; i++) {
            checkByteCalculated ^= (short) (data[i] & 0xFF);
        }
        //???????????????????????????????????? как они ее считают...

        if (checkByteCalculated == data[data.length - 2] ) {
            return true;
        }
        else {
            incorrectMessageLogging(
                    String.format("MESSAGE ERROR - checkByte:" +
                                    "\nCheckByteCalculated != CheckByteFromMessage: %d != %d",
                            checkByteCalculated, checkByteFromMessage
                    )
            );
            return false;
        }
    }

    private static short getShortNumber(byte ... data) {
        return (short) (((data[1] & 0xFF) << 8) | (data[0] & 0xFF));
    }



    private void handleMessagePositionData() {
        if (mapClientsMessagePositionData.peek() != null) { // проверяем очередь на наличие новых сообщений
            var data = mapClientsMessagePositionData.poll(); // берем из очереди сообщение
            Packed packed = new Packed(data); // сохраняем байты в удобный вид
            UvsData uvsData = mappingUtils.mapToUvsData(packed); // создаем обьект для сохранения данных в сущности бд
            uvsDataService.saveInDB(uvsData); // сохраняем в бд
            log.info("PositionData - SAVE in DB");
        }

    }

}


