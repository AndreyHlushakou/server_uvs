package by.agat.server_uvs.httpserver.controller;

import by.agat.server_uvs.httpserver.controller.utils.Paths;
import by.agat.server_uvs.httpserver.dto.LogTcpDTO;
import by.agat.server_uvs.httpserver.dto.PairDateRequest;
import by.agat.server_uvs.httpserver.service.LogTcpService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "LogTcpController", description = "LogTcp APIs")
@CrossOrigin
@RestController
@RequestMapping(Paths.LOG_URL)
@RequiredArgsConstructor
public class LogTcpController {

    private final LogTcpService logTcpService;

    @GetMapping("/json")
    public List<LogTcpDTO> getLogJSON() {
        return logTcpService.getLogDTO(null, new PairDateRequest(), null);
    }

    @GetMapping("/json/date")
    public List<LogTcpDTO> getLogJSONByDate(@RequestBody PairDateRequest pairDateRequest) {
        return logTcpService.getLogDTO(null, pairDateRequest, null);
    }


    @GetMapping("/json/{deviceId}")
    public List<LogTcpDTO> getLogJSONByDeviceId(@PathVariable String deviceId) {
        return logTcpService.getLogDTO(deviceId, new PairDateRequest(), null);
    }
    @GetMapping("/json/date/{deviceId}")
    public List<LogTcpDTO> getLogJSONByDateByDeviceId(@RequestBody PairDateRequest pairDateRequest, @PathVariable String deviceId) {
        return logTcpService.getLogDTO(deviceId, pairDateRequest, null);
    }


    @GetMapping("/json/{deviceId}/{serialNumber}")
    public List<LogTcpDTO> getLogJSONByDeviceIdAndSerialNumber(@PathVariable String deviceId, @PathVariable int serialNumber) {
        return logTcpService.getLogDTO(deviceId, new PairDateRequest(), serialNumber);
    }
    @GetMapping("/json/date/{deviceId}/{serialNumber}")
    public List<LogTcpDTO> getLogJSONByDateByDeviceIdAndSerialNumber(@RequestBody PairDateRequest pairDateRequest, @PathVariable String deviceId, @PathVariable int serialNumber) {
        return logTcpService.getLogDTO(deviceId, pairDateRequest, serialNumber);
    }
}
