package by.agat.server_uvs.httpserver.utils;

import by.agat.server_uvs.httpserver.dto.*;
import by.agat.server_uvs.httpserver.dto.data_message.*;
import by.agat.server_uvs.httpserver.dto.data_message.data_0101.DataMessageCoord;
import by.agat.server_uvs.httpserver.dto.data_message.data_0102_0103.DM;
import by.agat.server_uvs.httpserver.dto.data_message.data_0102_0103.DataMessageActiveTroubles;
import by.agat.server_uvs.httpserver.dto.data_message.data_0201.DataMessageParams;
import by.agat.server_uvs.httpserver.dto.data_message.data_0201.Params;
import by.agat.server_uvs.httpserver.entities.LogTcpEntity;
import by.agat.server_uvs.httpserver.entities.UvsData;
import by.agat.server_uvs.httpserver.utils.csv.MazParams;
import by.agat.server_uvs.tcpserver.packed_maz.Packed;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static by.agat.server_uvs.httpserver.utils.csv.DataFromCsv.mazParamsList;

@Service
public class MappingUtils {

    //Packed to PositionData////////////////////////////////////////////////////////////////////////////////////////////
    public UvsData mapToUvsData(Packed packed) {
        return new UvsData()
                .setVIN(packed.getVIN())
                .setDateTime(packed.getDateTime())
                .setTypeMessage(String.format("%04X", packed.getTypeMessage()))
                .setSizeMessage(packed.getSizeMessage())
                .setDataMessage(packed.getDataMessage())
                ;
    }

    public List<UvsDataDTO> mapToListUvsDataDTO(List<UvsData> uvsDataList) {
        List<UvsDataDTO> uvsDataDTOList = new ArrayList<>();
        for (UvsData uvsData : uvsDataList) {
            uvsDataDTOList.add(
                    mapToUvsDataDTO(uvsData)
            );
        }
        return uvsDataDTOList;
    }

    private UvsDataDTO mapToUvsDataDTO(UvsData uvsData) {
        UvsDataDTO uvsDataDTO = new UvsDataDTO()
                .setVIN(uvsData.getVIN())
                .setDateTime(uvsData.getDateTime())
                .setTypeMessage(uvsData.getTypeMessage())
                .setSizeMessage(uvsData.getSizeMessage())
                ;

        byte[] dataMessage = uvsData.getDataMessage();

        return switch (uvsData.getTypeMessage()) {
            case "0101" ->         uvsDataDTO.setDataMessage(getDataMessageCoord(dataMessage));
            case "0102", "0103" -> uvsDataDTO.setDataMessage(getDataMessageActiveAndPassiveError(dataMessage));
            case "0201" ->         uvsDataDTO.setDataMessage(getDataMessageParams(dataMessage));
            default ->             uvsDataDTO;
        };

    }

    private DataMessageCoord getDataMessageCoord(byte[] dataMessage) {

        //test coord
//        long lat1 = dataMessage[0] & 0xFF;
//        long lon1 = dataMessage[4] & 0xFF;
//
//        long lat2 = (dataMessage[1] & 0xFF) << 8;
//        long lon2 = (dataMessage[5] & 0xFF) << 8;
//
//        long lat3 = (dataMessage[2] & 0xFF) << 16;
//        long lon3 = (dataMessage[6] & 0xFF) << 16;
//
//        long lat4 = (long) (dataMessage[3] & 0xFF) << 24;
//        long lon4 = (long) (dataMessage[7] & 0xFF) << 24;
//
//        long lat5 = lat4 | lat3 | lat2 | lat1;
//        long lon5 = lon4 | lon3 | lon2 | lon1;
//
//        long lat6  = lat5 - 2100_000_000;
//        long lon6 = lon5 - 2100_000_000;
//
//        double latitude = (double) lat6 / 10_000_000;
//        double longitude = (double) lon6 / 10_000_000;
//
//        System.out.println();
//        System.out.printf("1: latH:0x%X lonH:0x%X latD:%d lonH:%d\n", lat1, lon1, lat1, lon1);
//        System.out.printf("2: latH:0x%X lonH:0x%X latD:%d lonH:%d\n", lat2, lon2, lat2, lon2);
//        System.out.printf("3: latH:0x%X lonH:0x%X latD:%d lonH:%d\n", lat3, lon3, lat3, lon3);
//        System.out.printf("4: latH:0x%X lonH:0x%X latD:%d lonH:%d\n", lat4, lon4, lat4, lon4);
//        System.out.printf("5: latH:0x%X lonH:0x%X latD:%d lonH:%d\n", lat5, lon5, lat5, lon5);
//        System.out.printf("6: latH:0x%X lonH:0x%X latD:%d lonH:%d\n", lat6, lon6, lat6, lon6);
//        System.out.printf("0: lat:%f lon:%f", latitude, longitude);

        double latitude  = (double) ((((long) (dataMessage[3] & 0xFF) << 24) | ((dataMessage[2] & 0xFF) << 16) | ((dataMessage[1] & 0xFF) << 8) | (dataMessage[0] & 0xFF)) - 2100_000_000) / 10_000_000;
        double longitude = (double) ((((long) (dataMessage[7] & 0xFF) << 24) | ((dataMessage[6] & 0xFF) << 16) | ((dataMessage[5] & 0xFF) << 8) | (dataMessage[4] & 0xFF)) - 2100_000_000) / 10_000_000;
        double speed     = (double) (((dataMessage[9] & 0xFF) << 9) | (dataMessage[8] & 0xFF)) / 256;
        return new DataMessageCoord()
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setSpeed(speed);
    }

    private DataMessageActiveTroubles getDataMessageActiveAndPassiveError(byte[] dataMessage) {
        List<DM> dmList = new ArrayList<>();
        try {
            for (int i = 0; i < dataMessage.length; i += 4) {
                int spn = ((dataMessage[i] & 0xFF) << 8) | (dataMessage[i+1] & 0xFF);
                int fmi = dataMessage[i+2] & 0xFF;
                int cm_oc = dataMessage[i+3] & 0xFF;

                dmList.add(
                        new DM()
                                .setSPN(spn)
                                .setFMI(fmi)
                                .setCM_OC(cm_oc)
                );
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return new DataMessageActiveTroubles()
                .setActiveTroubles(dmList);

        //spn 16-5655
    }

    private DataMessageParams getDataMessageParams(byte[] dataMessage) {
        List<Params> paramsList = new ArrayList<>();
        for (MazParams mazParams : mazParamsList) {
            int byte_ = mazParams.getOut_byte();
            if (byte_ < dataMessage.length) {
                int bit = mazParams.getBit();
                int size = mazParams.getSize();
                int mask_head = switch (bit) {
                    case 1 -> 0x7F; //0b01111111
                    case 2 -> 0x3F; //0b00111111
                    case 3 -> 0x1F; //0b00011111
                    case 4 -> 0x0F; //0b00001111
                    case 5 -> 0x07; //0b00000111
                    case 6 -> 0x03; //0b00000011
                    case 7 -> 0x01; //0b00000001
                    default -> 0xFF; //0b11111111
                };
                int mask_tail = switch (size) {
                    case 1 -> 0xFE; //0b11111110
                    case 2 -> 0xFC; //0b11111100
                    case 3 -> 0xF8; //0b11111000
                    case 4 -> 0xF0; //0b11110000
                    case 5 -> 0xE0; //0b11100000
                    case 6 -> 0xC0; //0b11000000
                    case 7 -> 0x80; //0b10000000
                    default -> 0xFF; //0b11111111
                };
                long value = (dataMessage[byte_] & mask_head) & mask_tail;
                paramsList.add(
                        new Params()
                                .setBit(bit)
                                .setByte_(byte_)
                                .setSize(size)
                                .setValue(value)
                );
            }

        }


        return new DataMessageParams()
                .setParamsList(paramsList);
    }


    //Log to LogDTO/////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<LogTcpDTO> mapToListLogDTO(List<LogTcpEntity> logTcpList) {
        List<LogTcpDTO> logTcpDTOList = new ArrayList<>();
        for (LogTcpEntity logTcp : logTcpList) {
            LogTcpDTO logTcpDTO = mapToLogDTO(logTcp);
            logTcpDTOList.add(logTcpDTO);
        }
        return logTcpDTOList;
    }

    private LogTcpDTO mapToLogDTO(LogTcpEntity logTcp) {
        return new LogTcpDTO()
                .setDateTime(logTcp.getDateTime())
                .setMessage(List.of(logTcp.getMessage().split("\n")))
                ;
    }

}
