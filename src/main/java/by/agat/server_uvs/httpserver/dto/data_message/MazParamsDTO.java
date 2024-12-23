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
public class MazParamsDTO{
    private String CAN_ID;
    private int byte_;
    private int bit;
    private int size;
    private int out_byte;
    private int out_bit;
}
