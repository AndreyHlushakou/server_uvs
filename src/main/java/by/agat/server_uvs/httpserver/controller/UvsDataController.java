package by.agat.server_uvs.httpserver.controller;

import by.agat.server_uvs.httpserver.controller.utils.Paths;
import by.agat.server_uvs.httpserver.dto.data_message.UvsDataDTO;
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

    @GetMapping("")
    public List<UvsDataDTO> getAllUvsData() {
        return uvsDataService.getAllDTOByVinAndTypeMessage(null, null);
    }

    @GetMapping("/{vin}")
    public List<UvsDataDTO> getPositionDataByVin(@PathVariable String vin) {
        return uvsDataService.getAllDTOByVinAndTypeMessage(vin, null);
    }

    @GetMapping("/t/{typeMessage}")
    public List<UvsDataDTO> getPositionDataByTypeMessage(@PathVariable String typeMessage) {
        return uvsDataService.getAllDTOByVinAndTypeMessage(null, typeMessage);
    }

    @GetMapping("/{vin}/{typeMessage}")
    public List<UvsDataDTO> getPositionDataByVinAndTypeMessage(@PathVariable String vin, @PathVariable String typeMessage) {
        return uvsDataService.getAllDTOByVinAndTypeMessage(vin, typeMessage);
    }

    @GetMapping("/vin")
    public List<String> getListVin() {
        return uvsDataService.getListDistinctVin();
    }
}
