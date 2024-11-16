package by.agat.server_uvs.exceptions.httpserver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AppError {
    private int statusCode;
    private String message;
}
