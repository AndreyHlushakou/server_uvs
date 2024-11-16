package by.agat.server_uvs.httpserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class DataMessageCoord extends DataMessage{
    private Integer latitude;
    private Integer longitude;
}
