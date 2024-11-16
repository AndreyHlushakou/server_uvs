package by.agat.server_uvs.tcpserver.packed_maz;

import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Getter
public class Packed {

    private final String head;
    private final Date dateTime;
    private final short typeMessage;
    private final short sizeMessage;
    private final String VIN;
    private final byte[] dataMessage;
//    private final short checkout;

    public Packed(byte[] data){
        this(
                getString(data[0], data[1], data[2]), //head
                byteToDateTime(data[3], data[4], data[5], data[6], data[7], data[8]), //dateTime
                getShortNumber(data[9], data[10]), //typeMessage
                getShortNumber(data[11], data[12]), //sizeMessage
                getString(data[13], data[14], data[15], data[16], data[17], data[18],data[19], data[20], data[21],data[22], data[23], data[24],data[25], data[26], data[27], data[28], data[29]), // VIN 17 bytes
                data
        );
    }

    public Packed(String head, Date dateTime, short typeMessage, short sizeMessage, String VIN, byte[] data) {
        this.head = head;
        this.dateTime = dateTime;
        this.typeMessage = typeMessage;
        this.sizeMessage = sizeMessage;
        this.VIN = VIN;
//        this.checkout = getShortNumber(data[29+sizeMessage+1], data[29+sizeMessage+2]);

        this.dataMessage = Arrays.copyOfRange(data, 30, (30 + sizeMessage));
    }

    private static String getString(byte ... data){
        return new String(data, StandardCharsets.UTF_8);
    }

    private static Date byteToDateTime(byte year, byte month, byte day, byte hour, byte minute, byte second) {
        Calendar calendar = new GregorianCalendar(
                (2000 + byteToIntDate(year)),
                (byteToIntDate(month) - 1), // 1...12 -> 0...11
                (byteToIntDate(day)),
                byteToIntDate(hour),
                byteToIntDate(minute),
                byteToIntDate(second));
        return calendar.getTime();
    }

    private static int byteToIntDate(byte data) {
        return Integer.parseInt(String.format("%02X", data));
    }

    private static short getShortNumber(byte ... data) {
        return (short) (((data[1] & 0xFF) << 8) | (data[0] & 0xFF));
    }

}
