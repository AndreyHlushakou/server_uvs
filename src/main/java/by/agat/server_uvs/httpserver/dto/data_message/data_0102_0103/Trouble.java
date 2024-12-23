package by.agat.server_uvs.httpserver.dto.data_message.data_0102_0103;

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
public class Trouble {
    private int err_cnt;
    private int maz_source_address;
    private int lamps_status;
    private List<DM> listDM;
}
