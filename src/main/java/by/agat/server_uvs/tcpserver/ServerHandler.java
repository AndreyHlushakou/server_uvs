package by.agat.server_uvs.tcpserver;

import by.agat.server_uvs.httpserver.entities.LogTcpEntity;
import by.agat.server_uvs.httpserver.entities.UvsData;
import by.agat.server_uvs.httpserver.service.LogTcpService;
import by.agat.server_uvs.httpserver.service.UvsDataService;
import by.agat.server_uvs.httpserver.utils.MappingUtils;
import by.agat.server_uvs.tcpserver.packed_maz.Packed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class ServerHandler implements Runnable
{
    private final int port;
    private final UvsDataService uvsDataService;
    private final LogTcpService logTcpService;
    private final Queue<byte[]> queueUvsData = new ConcurrentLinkedQueue<>();
    private final Queue<LogTcpEntity> queueLogTcp = new ConcurrentLinkedQueue<>();
    private final MessageHandler messageHandler = new MessageHandler();
    private final MappingUtils mappingUtils;

    @Autowired
    public ServerHandler(@Value("${tcpServer.port}") int port, UvsDataService uvsDataService, MappingUtils mappingUtils, LogTcpService logTcpService) {
        this.port = port;
        this.uvsDataService = uvsDataService;
        this.mappingUtils = mappingUtils;
        this.logTcpService = logTcpService;
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
                        handleMessageUvsData();
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
            String message = "new client:" + newClient;
            addLodInDB(new LogTcpEntity(), message);
            log.info(message); // выводим сообщение о подключении
        }
    }

    private void handleDisConnectedClientsEvents(Server server) {
        var disconnectedClient = server.getDisConnectedClientsEvents().poll(); // обращаемся к внутренней очереди сервера отключения клиентов
        if (disconnectedClient != null) {

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            String message = "disconnectedClient:" + disconnectedClient;
            addLodInDB(new LogTcpEntity(), message);
            log.info(message); // выводим сообщение об отключении
        }
    }

    /****/
    private void handleClientsMessages(Server server) {
        AddressMessage addressMessage = server.getMessagesFromClients().poll(); // обращаемся к внутренней очереди сервера сообщений от клиентов
        if (addressMessage != null) {
            var clientAddress = addressMessage.clientAddress();
            var requestMessage = addressMessage.message(); // получаем сообщения от клиента из очереди

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            String message = "from:" + clientAddress + ", messageFromClient.length:" + requestMessage.length;
            addLodInDB(new LogTcpEntity(), message);
            log.info(message); // выводим длину этого сообщения

            messageHandler.handling(requestMessage); // обрабатываем сообщение на правильность
            boolean res = messageHandler.isRes(); // правильное ли пришло сообщение
            if (res) {
                queueUvsData.offer(requestMessage);
            }
            //В ОТВЕТ КЛИЕНТУ НИЧЕГО НЕ ОТПРАВЛЯЕМ!!!!!!!!!!

            message = getReportAboutMessage(messageHandler);
            addLodInDB(new LogTcpEntity(), message);
            log.info(message); // выводим сообщения принятые

            if (!messageHandler.isRes()) {
                addLodInDB(new LogTcpEntity(), messageHandler.getErrorMessage());
            }
        }
    }

    private String getReportAboutMessage(MessageHandler messageHandler) {
        StringBuilder report = new StringBuilder()
                .append("\nVIN              :").append(messageHandler.getVIN())
                .append("\ntype message     :").append(messageHandler.getType())
                .append("\nsize data message:").append(messageHandler.getSizeData())
                .append("\nDateTimeClient   :").append(new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(messageHandler.getDateTime()))
//                .append("\nClientMessageStr :\n").append(new String(messageHandler.getData(), StandardCharsets.US_ASCII))
                .append("\nClientMessageByte:\n");
        for (byte datum : messageHandler.getData()) {
            report.append(String.format("%02X ", datum));
        }
        return report.toString();
    }

    private void handleMessageUvsData() {
        if (queueUvsData.peek() != null) { // проверяем очередь на наличие новых сообщений
            var data = queueUvsData.poll(); // берем из очереди сообщение
            Packed packed = new Packed(data); // сохраняем байты в удобный вид
            UvsData uvsData = mappingUtils.mapToUvsData(packed); // создаем объект для сохранения данных в сущности бд
            uvsDataService.saveInDB(uvsData); // сохраняем в бд
            log.info("PositionData - SAVE in DB");
        }
        if (queueLogTcp.peek() != null) {
            var data = queueLogTcp.poll();
            logTcpService.saveInDB(data);
            log.info("LogTcp - SAVE in DB");
        }

    }


    public void addLodInDB(LogTcpEntity logTcpEntity, String message, String deviceId) {
        addLodInDB(logTcpEntity, message, deviceId, null);
    }

    public void addLodInDB(LogTcpEntity logTcpEntity, String message, String deviceId, Integer serialNumber) {
        logTcpEntity.setSerialNumber(serialNumber);
        logTcpEntity.setDeviceId(deviceId);
        addLodInDB(logTcpEntity, message);
    }

    public void addLodInDB(LogTcpEntity logTcpEntity, String message) {
        logTcpEntity.setDateTime(new Date());
        logTcpEntity.setMessage(message);
        queueLogTcp.add(logTcpEntity);
    }
}


