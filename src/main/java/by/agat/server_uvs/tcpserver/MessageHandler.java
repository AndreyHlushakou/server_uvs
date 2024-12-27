package by.agat.server_uvs.tcpserver;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static by.agat.server_uvs.exceptions.tcpserver.IncorrectMessageLog.incorrectMessageLogging;


@NoArgsConstructor
@Getter
public class MessageHandler {
    private boolean res;

    private byte[] data;

    private String errorMessage;

    private final static Set<Integer> typeMessageSet = new HashSet<>(List.of(0x0101, 0x0102, 0x0103, 0x0201));

    public void handling(byte[] requestMessage) {
        this.data = requestMessage;
        res = checkData(); // проверяем правильность пришедшего сообщения
    }

    private boolean checkData() {
        int checkByteFromMessage = getUint_16Number(data[data.length-1], data[data.length-2]); // контрольная сумма из пакета

        int checkByteCalculated =  Calc_Data_CS(data, data.length-2); // вычисляем контрольную сумму
        StringBuilder stringBuilder = new StringBuilder();

        if (checkByteCalculated == checkByteFromMessage ) {
            int typeMessageFromMessage = ((data[9] & 0xFF) << 8) | data[10];

            if (typeMessageSet.contains(typeMessageFromMessage)) {
                int lengthMessageCalculated = data.length - 32;
                int lengthMessageFromMessage = ((data[11] & 0xFF) << 8) | (data[12] & 0xFF);

                if (lengthMessageCalculated == lengthMessageFromMessage) {
                    return true;
                }
                else {
                    errorMessage = stringBuilder.append("MESSAGE ERROR - too small:")
                            .append("\ndata:").append(printData(data))
                            .append("\nlengthMessageCalculated != LengthMessageFromMessage: ")
                            .append(lengthMessageCalculated).append(" != ").append(lengthMessageFromMessage)
                            .toString();
                    incorrectMessageLogging(errorMessage);
                    return false;
                }
            } else {
                errorMessage = stringBuilder.append("MESSAGE ERROR - type not fount:")
                        .append("\ndata:").append(printData(data))
                        .append("\ntypeMessageFromMessage: ")
                        .append(typeMessageFromMessage)
                        .toString();

                        incorrectMessageLogging(errorMessage);
                return false;
            }
        }
        else {
            errorMessage = stringBuilder.append("MESSAGE ERROR - checkByte:")
                    .append("\ndata:").append(printData(data))
                    .append("\ncheckByteCalculated != checkByteFromMessage: ")
                    .append(String.format("0x%04X",checkByteCalculated))
                    .append(" != ")
                    .append(String.format("0x%04X",checkByteFromMessage))
                    .toString();

            incorrectMessageLogging(errorMessage);
            return false;
        }
    }

    int Calc_Data_CS(byte[] data, int len) {
        char crc = 0xFFFF;
        int p = 0;

        while (len-- > 0) {
            crc = (char) ((crc << 8) ^ Crc16Table[(crc >> 8) ^ (data[p++] & 0xFF)]);
        }
        return crc;
    }

    final char[] Crc16Table = new char[]{
            0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50A5, 0x60C6, 0x70E7,
            0x8108, 0x9129, 0Xa14A, 0Xb16B, 0Xc18C, 0Xd1AD, 0Xe1CE, 0Xf1EF,
            0x1231, 0x0210, 0x3273, 0x2252, 0x52B5, 0x4294, 0x72F7, 0x62D6,
            0x9339, 0x8318, 0Xb37B, 0Xa35A, 0Xd3BD, 0Xc39C, 0Xf3FF, 0Xe3DE,
            0x2462, 0x3443, 0x0420, 0x1401, 0x64E6, 0x74C7, 0x44A4, 0x5485,
            0Xa56A, 0Xb54B, 0x8528, 0x9509, 0Xe5EE, 0Xf5CF, 0Xc5AC, 0Xd58D,
            0x3653, 0x2672, 0x1611, 0x0630, 0x76D7, 0x66F6, 0x5695, 0x46B4,
            0Xb75B, 0Xa77A, 0x9719, 0x8738, 0Xf7DF, 0Xe7FE, 0Xd79D, 0Xc7BC,
            0x48C4, 0x58E5, 0x6886, 0x78A7, 0x0840, 0x1861, 0x2802, 0x3823,
            0Xc9CC, 0Xd9ED, 0Xe98E, 0Xf9AF, 0x8948, 0x9969, 0Xa90A, 0Xb92B,
            0x5AF5, 0x4AD4, 0x7AB7, 0x6A96, 0x1A71, 0x0A50, 0x3A33, 0x2A12,
            0Xdbfd, 0Xcbdc, 0Xfbbf, 0Xeb9E, 0x9B79, 0x8B58, 0Xbb3B, 0Xab1A,
            0x6CA6, 0x7C87, 0x4CE4, 0x5CC5, 0x2C22, 0x3C03, 0x0C60, 0x1C41,
            0Xedae, 0Xfd8F, 0Xcdec, 0Xddcd, 0Xad2A, 0Xbd0B, 0x8D68, 0x9D49,
            0x7E97, 0x6EB6, 0x5ED5, 0x4EF4, 0x3E13, 0x2E32, 0x1E51, 0x0E70,
            0Xff9F, 0Xefbe, 0Xdfdd, 0Xcffc, 0Xbf1B, 0Xaf3A, 0x9F59, 0x8F78,
            0x9188, 0x81A9, 0Xb1CA, 0Xa1EB, 0Xd10C, 0Xc12D, 0Xf14E, 0Xe16F,
            0x1080, 0x00A1, 0x30C2, 0x20E3, 0x5004, 0x4025, 0x7046, 0x6067,
            0x83B9, 0x9398, 0Xa3FB, 0Xb3DA, 0Xc33D, 0Xd31C, 0Xe37F, 0Xf35E,
            0x02B1, 0x1290, 0x22F3, 0x32D2, 0x4235, 0x5214, 0x6277, 0x7256,
            0Xb5EA, 0Xa5CB, 0x95A8, 0x8589, 0Xf56E, 0Xe54F, 0Xd52C, 0Xc50D,
            0x34E2, 0x24C3, 0x14A0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
            0Xa7DB, 0Xb7FA, 0x8799, 0x97B8, 0Xe75F, 0Xf77E, 0Xc71D, 0Xd73C,
            0x26D3, 0x36F2, 0x0691, 0x16B0, 0x6657, 0x7676, 0x4615, 0x5634,
            0Xd94C, 0Xc96D, 0Xf90E, 0Xe92F, 0x99C8, 0x89E9, 0Xb98A, 0Xa9AB,
            0x5844, 0x4865, 0x7806, 0x6827, 0x18C0, 0x08E1, 0x3882, 0x28A3,
            0Xcb7D, 0Xdb5C, 0Xeb3F, 0Xfb1E, 0x8BF9, 0x9BD8, 0Xabbb, 0Xbb9A,
            0x4A75, 0x5A54, 0x6A37, 0x7A16, 0x0AF1, 0x1AD0, 0x2AB3, 0x3A92,
            0Xfd2E, 0Xed0F, 0Xdd6C, 0Xcd4D, 0Xbdaa, 0Xad8B, 0x9DE8, 0x8DC9,
            0x7C26, 0x6C07, 0x5C64, 0x4C45, 0x3CA2, 0x2C83, 0x1CE0, 0x0CC1,
            0Xef1F, 0Xff3E, 0Xcf5D, 0Xdf7C, 0Xaf9B, 0Xbfba, 0x8FD9, 0x9FF8,
            0x6E17, 0x7E36, 0x4E55, 0x5E74, 0x2E93, 0x3EB2, 0x0ED1, 0x1EF0
    };


    private static int getUint_16Number(byte ... data) {
        return ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);
    }


    private String printData(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte datum : data) {
            stringBuilder.append(String.format("%02X ", datum));
        }
        return stringBuilder.toString();
//        return IntStream.range(0, data.length).mapToObj(n -> data[n]).map(m -> String.format("%02X", m)).toList().toString();
    }


    public Date getDateTime() {
        Calendar calendar = new GregorianCalendar(
                (2000 + byteToIntDate(data[3])),
                (byteToIntDate(data[4])-1), // offset: 1...12 -> 0...11
                byteToIntDate(data[5]),
                byteToIntDate(data[6]),
                byteToIntDate(data[7]),
                byteToIntDate(data[8])
        );
        return calendar.getTime();
    }

    private static int byteToIntDate(byte data) {
        return Integer.parseInt(String.format("%02X", data), 16);
    }

    public String getType() {
        return String.format("%04X", ((data[9] & 0xFF) << 8) | data[10]);
    }

    public int getSizeData() {
        return ((data[11] & 0xFF) << 8) | (data[12] & 0xFF);
    }

    public String getVIN() {
        byte[] vin = Arrays.copyOfRange(data, 13, 29);
        return new String(vin, StandardCharsets.US_ASCII);
    }
}
