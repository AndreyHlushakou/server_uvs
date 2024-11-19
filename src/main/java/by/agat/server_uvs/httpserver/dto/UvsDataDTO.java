package by.agat.server_uvs.httpserver.dto;

import by.agat.server_uvs.httpserver.dto.data_message.DataMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class UvsDataDTO {
    private String VIN;
    private Date dateTime;
    private String typeMessage;
    private DataMessage dataMessage;
}
