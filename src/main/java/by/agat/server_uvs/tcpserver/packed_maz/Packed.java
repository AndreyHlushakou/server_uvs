package by.agat.server_uvs.tcpserver.packed_maz;

import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Getter
public class Packed {

//    private final String head;
    private final Date dateTime;
    private final String typeMessage;
    private final int sizeMessage;
    private final String VIN;
    private final byte[] dataMessage;
//    private final int checkout;

    public Packed(byte[] data){
        this(
//                toUS_ASCII(data[0], data[1], data[2]), //head
                byteToDateTime(data[3], data[4], data[5], data[6], data[7], data[8]), //dateTime
                getUint_16Number(data[9], data[10]), //typeMessage
                getUint_16Number(data[12], data[11]), //sizeMessage
                toUS_ASCII(data[13], data[14], data[15], data[16], data[17], data[18],data[19], data[20], data[21],data[22], data[23], data[24],data[25], data[26], data[27], data[28], data[29]), // VIN 17 bytes
                data
//                , getUint_16Number(data[], data[]) //checkout
        );
    }

    public Packed(Date dateTime, int typeMessage, int sizeMessage, String VIN, byte[] data) {
//        this.head = head;
        this.dateTime = dateTime;
        this.typeMessage = String.format("%04X", typeMessage);
        this.sizeMessage = sizeMessage;
        this.VIN = VIN;
//        this.checkout = getShortNumber(data[29+sizeMessage+1], data[29+sizeMessage+2]);

        if (sizeMessage != 0) {
            this.dataMessage = Arrays.copyOfRange(data, 30, (30 + sizeMessage));
        }
        else {
            this.dataMessage = new byte[0];
        }
    }

    private static String toUS_ASCII(byte ... data){
        return new String(data, StandardCharsets.US_ASCII);
    }

    private static Date byteToDateTime(byte year, byte month, byte day, byte hour, byte minute, byte second) {
        Calendar calendar = new GregorianCalendar(
                (2000 + year),
                ((month) - 1), // month in GregorianCalendar: 1...12 -> 0...11
                ((day)),
                (hour),
                (minute),
                (second));
        return calendar.getTime();
    }

    private static int getUint_16Number(byte ... data) {
        return ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);
    }

}
