package by.agat.server_uvs.httpserver.dto.data_message.data_0101;

import by.agat.server_uvs.httpserver.dto.data_message.DataMessage;
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
public class DataMessageCoord implements DataMessage {
    private Double latitude;
    private Double longitude;
    private Double speed;
}
