package by.agat.server_uvs.httpserver.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "log_tcp")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LogTcpEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private /*UUID*/Long  id;

    @Column(name="date_time")
    private Date dateTime;

    @Column(length = 1000)
    private String message;

    @Column(name="device_id")
    private String deviceId;

    @Column(name="serial_number")
    private Integer serialNumber;
}
