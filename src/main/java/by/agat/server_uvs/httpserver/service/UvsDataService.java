package by.agat.server_uvs.httpserver.service;

import by.agat.server_uvs.httpserver.dto.data_message.UvsDataDTO;
import by.agat.server_uvs.httpserver.entities.UvsData;
import by.agat.server_uvs.httpserver.repository.UvsDataRepository;
import  by.agat.server_uvs.httpserver.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class UvsDataService {

    private final UvsDataRepository uvsDataRepository;
    private final MappingUtils mappingUtils;

    public List<UvsDataDTO> getAllDTOByVinAndTypeMessage(String vin, String typeMessage) {
        List<UvsData> uvsDataList;
        if (typeMessage == null && vin == null) {
            uvsDataList = getList();
        } else if (typeMessage == null) {
            uvsDataList = getListByVin(vin);
        } else if (vin == null) {
            uvsDataList = getListByTypeMessage(typeMessage);
        } else {
            uvsDataList = getListByVinAndTypeMessage(vin, typeMessage);
        }
        return mappingUtils.mapToListUvsDataDTO(uvsDataList);
    }

    public List<UvsData> getList() {
        return uvsDataRepository.findAll();
    }

    public List<UvsData> getListByVin(String vin) {
        return uvsDataRepository.findAllByVIN(vin);
    }

    public List<UvsData> getListByTypeMessage(String typeMessage) {
        return uvsDataRepository.findAllByTypeMessage(typeMessage);
    }

    public List<UvsData> getListByVinAndTypeMessage(String vin, String typeMessage) {
        return uvsDataRepository.findAllByVINAndTypeMessage(vin, typeMessage);
    }

    public void saveInDB(UvsData uvsData) {
        uvsDataRepository.save(uvsData);
    }

    public List<String> getListDistinctVin() {
        return uvsDataRepository.findDistinctVIN();
    }
}
