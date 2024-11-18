package by.agat.server_uvs.httpserver.service;

import by.agat.server_uvs.httpserver.dto.UvsDataDTO;
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

    public List<List<UvsDataDTO>> getAllDTO() {
        List<List<UvsDataDTO>> lists = new ArrayList<>();
        List<String> listDistinctVin = getListDistinctVin();
        for (String vin : listDistinctVin) {
            List<UvsDataDTO> list = getAllDTOByVin(vin);
            lists.add(list);
        }
        return lists;
    }

    public List<UvsDataDTO> getAllDTOByVin(String vin) {
        List<UvsData> uvsDataList = getListByVin(vin);
        return mappingUtils.mapToListUvsDataDTO(uvsDataList);
    }
    public List<UvsData> getListByVin(String vin) {
        return uvsDataRepository.findAllByVIN(vin);
    }

    public void saveInDB(UvsData uvsData) {
        uvsDataRepository.save(uvsData);
    }

    public List<String> getListDistinctVin() {
        return uvsDataRepository.findDistinctByVIN();
    }
}
