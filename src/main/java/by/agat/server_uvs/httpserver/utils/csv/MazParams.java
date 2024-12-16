package by.agat.server_uvs.httpserver.utils.csv;

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
public class MazParams {
    long CAN_ID;
    int byte_;
    int bit;
    int size;
    int out_byte;
    int out_bit;
}
