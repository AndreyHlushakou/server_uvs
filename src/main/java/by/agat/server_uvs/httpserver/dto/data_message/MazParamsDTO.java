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
    String CAN_ID;
    int byte_;
    int bit;
    int size;
    int out_byte;
    int out_bit;
}
