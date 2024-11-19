package by.agat.server_uvs.httpserver.dto.data_message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class DataMessageActiveTroubles extends DataMessage{
    private List<DM1> ActiveTroubles;
}
