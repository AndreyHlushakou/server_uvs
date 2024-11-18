package by.agat.server_uvs.httpserver.utils;

import by.agat.server_uvs.httpserver.dto.DM1;
import by.agat.server_uvs.httpserver.dto.DataMessageActiveTroubles;
import by.agat.server_uvs.httpserver.dto.DataMessageCoord;
import by.agat.server_uvs.httpserver.dto.UvsDataDTO;
import by.agat.server_uvs.httpserver.entities.UvsData;
import by.agat.server_uvs.tcpserver.packed_maz.Packed;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
            case 0x0101 -> uvsDataDTO.setDataMessage(getDataMessageCoord(dataMessage));
            case 0x0102 -> uvsDataDTO.setDataMessage(getDataMessageActiveError(dataMessage));
            default -> uvsDataDTO;
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
//        double lat6 = (double) lat5 / 10_000_000;
//        double lon6 = (double) lon5 / 10_000_000;
//
//        double latitude  = lat6 + 210;
//        double longitude = lon6 + 210;
//
//        System.out.println();
//        System.out.printf("1: lat:0x%X lon:0x%X\n", lat1, lon1);
//        System.out.printf("2: lat:0x%X lon:0x%X\n", lat2, lon2);
//        System.out.printf("3: lat:0x%X lon:0x%X\n", lat3, lon3);
//        System.out.printf("4: lat:0x%X lon:0x%X\n", lat4, lon4);
//        System.out.printf("5: lat:0x%X lon:0x%X\n", lat5, lon5);
//        System.out.printf("6: lat:%f lon:%f\n", lat6, lon6);
//        System.out.printf("0: lat:%f lon:%f", latitude, longitude);

        double latitude  = (double) (((long) (dataMessage[3] & 0xFF) << 24) | ((dataMessage[2] & 0xFF) << 16) | ((dataMessage[1] & 0xFF) << 8) | (dataMessage[0] & 0xFF)) / 10_000_000;
        double longitude = (double) (((long) (dataMessage[7] & 0xFF) << 24) | ((dataMessage[6] & 0xFF) << 16) | ((dataMessage[5] & 0xFF) << 8) | (dataMessage[4] & 0xFF)) / 10_000_000;

        return new DataMessageCoord()
                .setLatitude(latitude)
                .setLongitude(longitude);
    }

    private DataMessageActiveTroubles getDataMessageActiveError(byte[] dataMessage) {
        List<DM1> dm1List = new ArrayList<>();
        for (int i = 0; i < dataMessage.length; i += 4) {
            int spn = ((dataMessage[i] & 0xFF) << 8) | (dataMessage[i+1] & 0xFF);
            int fmi = dataMessage[i+2] & 0xFF;
            int cm_oc = dataMessage[i+3] & 0xFF;

            dm1List.add(
                    new DM1()
                    .setSPN(spn)
                    .setFMI(fmi)
                    .setCM_OC(cm_oc)
            );
        }
        return new DataMessageActiveTroubles()
                .setActiveTroubles(dm1List);

        //spn 16-5655
    }


}
