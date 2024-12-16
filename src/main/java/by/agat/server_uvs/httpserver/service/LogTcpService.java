package by.agat.server_uvs.httpserver.service;

import by.agat.server_uvs.httpserver.dto.LogTcpDTO;
import by.agat.server_uvs.httpserver.dto.PairDateRequest;
import by.agat.server_uvs.httpserver.entities.LogTcpEntity;
import by.agat.server_uvs.httpserver.repository.LogTcpRepository;
import by.agat.server_uvs.httpserver.utils.MappingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogTcpService {

    private final LogTcpRepository logTcpRepository;
    private final MappingUtils mappingUtils;


    public List<LogTcpDTO> getLogDTO(String deviceId, PairDateRequest pairDateRequest, Integer serialNumber){
        List<LogTcpEntity> logTcpList = getListByDeviceId(deviceId, pairDateRequest, serialNumber);
        return mappingUtils.mapToListLogDTO(logTcpList);
    }


    private List<LogTcpEntity> getListByDeviceId(String deviceId, PairDateRequest pairDateRequest, Integer serialNumber) {
        Date dateFrom = pairDateRequest.getDateFrom();
        Date dateTo = pairDateRequest.getDateTo();

        int typeDate = 0;
        if (dateFrom != null && dateTo != null) {
            typeDate = 1;
        }
        else if (dateFrom != null) {
            typeDate = 2;
        }
        else if (dateTo != null) {
            typeDate = 3;
        }

        if (deviceId != null) {
            if (serialNumber == null) {
                return switch (typeDate) {
                    case 1 -> logTcpRepository.findByDeviceIdAndDateTimeBetween(deviceId, dateFrom, dateTo);
                    case 2 -> logTcpRepository.findByDeviceIdAndDateTimeAfter(deviceId, dateFrom);
                    case 3 -> logTcpRepository.findByDeviceIdAndDateTimeBefore(deviceId, dateTo);
                    default-> logTcpRepository.findByDeviceId(deviceId);
                };
            } else {
                return switch (typeDate) {
                    case 1 -> logTcpRepository.findByDeviceIdAndDateTimeBetweenAndSerialNumber(deviceId, dateFrom, dateTo, serialNumber);
                    case 2 -> logTcpRepository.findByDeviceIdAndDateTimeAfterAndSerialNumber(deviceId, dateFrom, serialNumber);
                    case 3 -> logTcpRepository.findByDeviceIdAndDateTimeBeforeAndSerialNumber(deviceId, dateTo, serialNumber);
                    default-> logTcpRepository.findByDeviceIdAndSerialNumber(deviceId, serialNumber);
                };
            }
        } else {
            return switch (typeDate) {
                case 1 -> logTcpRepository.findByDateTimeBetween(dateFrom, dateTo);
                case 2 -> logTcpRepository.findByDateTimeAfter(dateFrom);
                case 3 -> logTcpRepository.findByDateTimeBefore(dateTo);
                default-> logTcpRepository.findAll();
            };
        }
    }

    public void saveInDB(LogTcpEntity logTcp) {
        logTcpRepository.save(logTcp);
    }
}
