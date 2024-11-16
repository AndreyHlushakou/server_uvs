package by.agat.server_uvs.httpserver.utils;

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

        

        switch (positionData.getTypeMessage()) {
            case 0x0101: {
               return uvsDataDTO.setDataMessage(getDataMessageCoord());
            }
        }

    }

    private DataMessageCoord getDataMessageCoord(String dataMessage) {
        byte
        return new DataMessageCoord()
                .setLatitude()
                .setLongitude();
    }

}
