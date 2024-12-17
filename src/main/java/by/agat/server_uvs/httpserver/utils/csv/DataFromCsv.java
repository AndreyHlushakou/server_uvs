package by.agat.server_uvs.httpserver.utils.csv;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class DataFromCsv {
    public static List<MazParams> mazParamsList;
}
