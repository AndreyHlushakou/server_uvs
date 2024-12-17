package by.agat.server_uvs.httpserver.controller;

import by.agat.server_uvs.httpserver.controller.utils.Paths;
import by.agat.server_uvs.httpserver.dto.data_message.MazParamsDTO;
import by.agat.server_uvs.httpserver.utils.csv.MazParams;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static by.agat.server_uvs.httpserver.utils.csv.DataFromCsv.mazParamsList;

@Tag(name = "MazCsvController", description = "MazCsv APIs")
@CrossOrigin
@RestController
@RequestMapping(Paths.MAZ_CSV_URL)
@RequiredArgsConstructor
public class MazCsvController {
    @GetMapping("")
    public List<MazParamsDTO> getMazCavData() {
        List<MazParamsDTO> mazParamsDTOList = new ArrayList<>();
        for (MazParams mazParams : mazParamsList) {
            mazParamsDTOList.add(
                    new MazParamsDTO()
                            .setCAN_ID(String.format("%08X", mazParams.getCAN_ID()))
                            .setByte_(mazParams.getByte_())
                            .setBit(mazParams.getBit())
                            .setSize(mazParams.getSize())
                            .setOut_byte(mazParams.getOut_byte())
                            .setOut_bit(mazParams.getOut_bit())
            );
        }
        return mazParamsDTOList;
    }
}
