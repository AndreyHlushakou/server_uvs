package by.agat.server_uvs.exceptions.tcpserver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IncorrectMessageLog {
    public static void incorrectMessageLogging(String message) {
        log.info(message);
    }
}
