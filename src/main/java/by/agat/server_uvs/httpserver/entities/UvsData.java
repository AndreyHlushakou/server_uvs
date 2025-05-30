package by.agat.server_uvs.httpserver.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Entity
@Table(name = "uvs_data")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class UvsData {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;

    @Column(name = "vin")
    private String VIN;

    @Column(name = "dateTime")
    private Date dateTime;

    @Column(name = "typeMessage")
    private String typeMessage;

    @Column(name = "sizeMessage")
    private Integer sizeMessage;

    @Lob
    @Column(name = "dataMessage")
    private byte[] dataMessage; //base64
}
