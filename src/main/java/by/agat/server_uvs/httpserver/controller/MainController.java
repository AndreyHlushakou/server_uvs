package by.agat.server_uvs.httpserver.controller;

import by.agat.server_uvs.httpserver.dto.UvsDataDTO;
import by.agat.server_uvs.httpserver.service.UvsDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/serverUVS")
@RequiredArgsConstructor
public class MainController {

    private final UvsDataService uvsDataService;

    //echo//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/echo/{echo}")
    public <T> T getEcho(@PathVariable T echo) {
        return echo;
    }
    @GetMapping("/echo")
    public String getEcho() {
        return "echo";
    }




    //data//////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
