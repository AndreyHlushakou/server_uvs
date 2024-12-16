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
import java.util.Base64;
import java.util.List;

import static by.agat.server_uvs.httpserver.utils.csv.DataFromCsv.mazParamsList;

@Service
public class MappingUtils {

    //Packed to PositionData////////////////////////////////////////////////////////////////////////////////////////////
    public UvsData mapToUvsData(Packed packed) {
        return new UvsData()
                .setVIN(packed.getVIN())
                .setDateTime(packed.getDateTime())
                .setTypeMessage(packed.getTypeMessage())
                .setDataMessage(
                        Base64.getEncoder().encodeToString(packed.getDataMessage())
                );
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
                .setTypeMessage(
                        String.format("%04X", uvsData.getTypeMessage())
                );

        byte[] dataMessage = Base64.getDecoder().decode(uvsData.getDataMessage());

        return switch (uvsData.getTypeMessage()) {
            case 0x0101 ->         uvsDataDTO.setDataMessage(getDataMessageCoord(dataMessage));
            case 0x0102, 0x0103 -> uvsDataDTO.setDataMessage(getDataMessageActiveAndPassiveError(dataMessage));
            case 0x0201 ->         uvsDataDTO.setDataMessage(getDataMessageParams(dataMessage));
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
        return new DataMessageActiveTroubles()
                .setActiveTroubles(dmList);

        //spn 16-5655
    }

    private DataMessageParams getDataMessageParams(byte[] dataMessage) {
        List<Params> paramsList = new ArrayList<>();
        for (MazParams mazParams : mazParamsList) {
            int i = mazParams.getOut_byte();
            if (i < dataMessage.length) {

                int bit = mazParams.getBit();
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
