package by.agat.server_uvs.httpserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PairDateRequest {
    private Date dateFrom;
    private Date dateTo;
}
