package by.agat.server_uvs.httpserver.utils;

import by.agat.server_uvs.httpserver.dto.DataMessageActiveError;
import by.agat.server_uvs.httpserver.dto.DataMessageCoord;
import by.agat.server_uvs.httpserver.dto.UvsDataDTO;
import by.agat.server_uvs.httpserver.entities.UvsData;
import by.agat.server_uvs.tcpserver.packed_maz.Packed;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.IntStream;

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

    public List<UvsDataDTO> mapToListPositionDataDTO(List<UvsData> uvsDataList) {
        List<UvsDataDTO> positionDataDTOList = new ArrayList<>();
        for (UvsData positionData : uvsDataList) {
            positionDataDTOList.add(
                    mapToPositionDataDTO(positionData)
            );
        }
        return positionDataDTOList;
    }

    private UvsDataDTO mapToPositionDataDTO(UvsData positionData) {
        UvsDataDTO uvsDataDTO = new UvsDataDTO()
                .setVIN(positionData.getVIN())
                .setDateTime(positionData.getDateTime())
                .setTypeMessage(positionData.getTypeMessage());


        return switch (positionData.getTypeMessage()) {
            case 0x0101 -> uvsDataDTO.setDataMessage(getDataMessageCoord(positionData.getDataMessage()));
            case 0x0102 -> uvsDataDTO.setDataMessage(getDataMessageActiveError(positionData.getDataMessage()));
            default -> uvsDataDTO;
        };

    }

    private DataMessageCoord getDataMessageCoord(String dataMessageS) {
        byte[] dataMessage = Base64.getDecoder().decode(dataMessageS);
        int latitude  = ((dataMessage[0] & 0xFF) <<24) | ((dataMessage[1] & 0xFF) <<16) | ((dataMessage[2] & 0xFF) <<8) | ((dataMessage[3] & 0xFF));
        int longitude = ((dataMessage[4] & 0xFF) <<24) | ((dataMessage[5] & 0xFF) <<16) | ((dataMessage[6] & 0xFF) <<8) | ((dataMessage[7] & 0xFF));
        return new DataMessageCoord()
                .setLatitude(latitude)
                .setLongitude(longitude);
    }

    private DataMessageActiveError getDataMessageActiveError(String dataMessageS) {
        byte[] bytes = Base64.getDecoder().decode(dataMessageS);
        Byte[] dataMessage = IntStream.range(0, bytes.length)
                .mapToObj(i -> bytes[i])
                .toArray(Byte[]::new);
        return new DataMessageActiveError()
                .setActiveErrors(dataMessage);
    }

}
