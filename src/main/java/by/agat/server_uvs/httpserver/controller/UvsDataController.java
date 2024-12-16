package by.agat.server_uvs.httpserver.controller;

import by.agat.server_uvs.httpserver.controller.utils.Paths;
import by.agat.server_uvs.httpserver.dto.UvsDataDTO;
import by.agat.server_uvs.httpserver.service.UvsDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "UvsDataController", description = "UvsData APIs")
@CrossOrigin
@RestController
@RequestMapping(Paths.POSITION_DATA_URL)
@RequiredArgsConstructor
public class UvsDataController {

    private final UvsDataService uvsDataService;

    @GetMapping("/data")
    public List<List<UvsDataDTO>> getAllUvsData() {
        return uvsDataService.getAllDTO();
    }

    @GetMapping("/data/{vin}")
    public List<UvsDataDTO> getPositionDataByVin(@PathVariable String vin) {
        return uvsDataService.getAllDTOByVin(vin);
    }

    @GetMapping("/vin")
    public List<String> getListVin() {
        return uvsDataService.getListDistinctVin();
    }
}
