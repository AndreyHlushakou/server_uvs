package by.agat.server_uvs.httpserver.repository;

import by.agat.server_uvs.httpserver.entities.LogTcpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface LogTcpRepository extends JpaRepository<LogTcpEntity, Long> {

    @Query("SELECT l FROM LogTcpEntity l WHERE l.dateTime BETWEEN :dateFrom AND :dateTo") //(:dateFrom < pd.dateTime AND pd.dateTime < :dateTo)
    List<LogTcpEntity> findByDateTimeBetween(Date dateFrom, Date dateTo);

    @Query("SELECT l FROM LogTcpEntity l WHERE :dateFrom <= l.dateTime ")
    List<LogTcpEntity> findByDateTimeAfter(Date dateFrom);

    @Query("SELECT l FROM LogTcpEntity l WHERE l.dateTime <= :dateTo")
    List<LogTcpEntity> findByDateTimeBefore(Date dateTo);



    @Query("SELECT l FROM LogTcpEntity l WHERE (l.dateTime BETWEEN :dateFrom AND :dateTo) AND l.deviceId=:deviceId") //(:dateFrom < pd.dateTime AND pd.dateTime < :dateTo)
    List<LogTcpEntity> findByDeviceIdAndDateTimeBetween(String deviceId, Date dateFrom, Date dateTo);

    @Query("SELECT l FROM LogTcpEntity l WHERE :dateFrom <= l.dateTime AND l.deviceId=:deviceId")
    List<LogTcpEntity> findByDeviceIdAndDateTimeAfter(String deviceId, Date dateFrom);

    @Query("SELECT l FROM LogTcpEntity l WHERE l.dateTime <= :dateTo AND l.deviceId=:deviceId")
    List<LogTcpEntity> findByDeviceIdAndDateTimeBefore(String deviceId, Date dateTo);

    @Query("SELECT l FROM LogTcpEntity l WHERE l.deviceId=:deviceId")
    List<LogTcpEntity> findByDeviceId(String deviceId);



    @Query("SELECT l FROM LogTcpEntity l WHERE (l.dateTime BETWEEN :dateFrom AND :dateTo) AND l.deviceId=:deviceId AND l.serialNumber=:serialNumber") //(:dateFrom < pd.dateTime AND pd.dateTime < :dateTo)
    List<LogTcpEntity> findByDeviceIdAndDateTimeBetweenAndSerialNumber(String deviceId, Date dateFrom, Date dateTo, Integer serialNumber);

    @Query("SELECT l FROM LogTcpEntity l WHERE :dateFrom <= l.dateTime AND l.deviceId=:deviceId AND l.serialNumber=:serialNumber")
    List<LogTcpEntity> findByDeviceIdAndDateTimeAfterAndSerialNumber(String deviceId, Date dateFrom, Integer serialNumber);

    @Query("SELECT l FROM LogTcpEntity l WHERE l.dateTime <= :dateTo AND l.deviceId=:deviceId AND l.serialNumber=:serialNumber")
    List<LogTcpEntity> findByDeviceIdAndDateTimeBeforeAndSerialNumber(String deviceId, Date dateTo, Integer serialNumber);

    @Query("SELECT l FROM LogTcpEntity l WHERE l.deviceId=:deviceId AND l.serialNumber=:serialNumber")
    List<LogTcpEntity> findByDeviceIdAndSerialNumber(String deviceId, Integer serialNumber);
}
