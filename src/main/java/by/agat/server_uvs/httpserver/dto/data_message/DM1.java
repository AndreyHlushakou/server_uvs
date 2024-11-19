package by.agat.server_uvs.httpserver.dto.data_message;

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
public class DM1 {
    private Integer SPN;
    private Integer FMI;
    private Integer CM_OC;
}
