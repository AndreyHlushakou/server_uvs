package by.agat.server_uvs.httpserver.controller;

import by.agat.server_uvs.httpserver.controller.utils.Paths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MainController", description = "MainController test heartbeat application")
@CrossOrigin
@RestController
@RequestMapping(Paths.BASE_URL)
public class MainController {

    @Operation(
            summary = "test heartbeat our application with parametr",
            description = "Get test response.",
            tags = { "MainController", "getEcho with parametr" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "text/html;charset=UTF-8")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})})
    @GetMapping("/echo/{echo}")
    public <T> T getEcho(@PathVariable T echo) {
        return echo;
    }

    @Operation(
            summary = "test heartbeat our application without parametr",
            description = "Get test response.",
            tags = { "MainController", "getEcho without parametr" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = String.class), mediaType = "text/html;charset=UTF-8")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})})
    @GetMapping("/echo")
    public String getEcho() {
        return "echo";
    }

}
