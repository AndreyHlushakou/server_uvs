package by.agat.server_uvs.httpserver.repository;

import by.agat.server_uvs.httpserver.entities.UvsData;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UvsDataRepository extends JpaRepository<UvsData, Long> {
    List<UvsData> findAllByVIN(String vin);

//    @Modifying
//    @Transactional
//    @Query("DELETE FROM UvsData pd WHERE pd.deviceId=:deviceId")
//    void deleteByDeviceId(@Param("deviceId")String deviceId);

    @Query("SELECT DISTINCT a.VIN FROM UvsData a")
    List<String> findDistinctByVIN();
}
