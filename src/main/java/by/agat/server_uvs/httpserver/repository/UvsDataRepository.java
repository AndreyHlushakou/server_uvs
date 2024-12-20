package by.agat.server_uvs.httpserver.repository;

import by.agat.server_uvs.httpserver.entities.UvsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UvsDataRepository extends JpaRepository<UvsData, /*UUID*/Long> {
    @Query("SELECT DISTINCT a.VIN FROM UvsData a")
    List<String> findDistinctVIN();

    @Query("SELECT a FROM UvsData a WHERE a.VIN=:vin")
    List<UvsData> findAllByVIN(String vin);

    @Query("SELECT a FROM UvsData a WHERE a.typeMessage=:typeMessage")
    List<UvsData> findAllByTypeMessage(String typeMessage);

    @Query("SELECT a FROM UvsData a WHERE a.VIN=:vin AND a.typeMessage=:typeMessage")
    List<UvsData> findAllByVINAndTypeMessage(String vin, String typeMessage);


}
