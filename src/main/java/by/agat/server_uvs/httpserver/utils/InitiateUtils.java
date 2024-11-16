package by.agat.server_uvs.httpserver.utils;//package org.example.httpserver.utils;
//
//import lombok.RequiredArgsConstructor;
//import org.example.httpserver.entities.DataFromDevice;
//import org.example.httpserver.entities.DeviceOrderInformation;
//import org.example.httpserver.entities.SourceOfPositionT;
//import org.example.httpserver.service.DataFromDeviceService;
//import org.example.httpserver.service.DeviceOrderInformationService;
//import org.example.tcpserver.HandlerServer;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class InitiateUtils implements CommandLineRunner {
//    private final DataFromDeviceService dataFromDeviceService;
//    private final DeviceOrderInformationService deviceOrderInformationService;
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        new HandlerServer().start(dataFromDeviceService);
//
////        List<DataFromDevice> dataFromDeviceList = Arrays.asList(
////                new DataFromDevice()
////                        .setDeviceID("000000232323").setLat(51.000).setLng(51.00).setAzimuth(150).setSpeed(10).setBattery(50).setStatuses(List.of("1", "2")).setTs(new Date()).setBatteryVoltage(4.1).setLocationNetworkType("4g")
////                        .setSourceOfPositionT(new SourceOfPositionT().setGpsLocated(true).setGpsSat(11).setBaseStation(true).setLocationCopied(true))
////                ,
////                new DataFromDevice()
////                        .setDeviceID("000000232323").setLat(52.000).setLng(52.00).setAzimuth(150).setSpeed(10).setBattery(50).setStatuses(List.of("1", "2")).setTs(new Date()).setBatteryVoltage(4.1).setLocationNetworkType("4g")
////                        .setSourceOfPositionT(new SourceOfPositionT().setGpsLocated(true).setGpsSat(12).setBaseStation(true).setLocationCopied(true))
////                ,
////                new DataFromDevice()
////                        .setDeviceID("000000232324").setLat(53.000).setLng(53.00).setAzimuth(150).setSpeed(10).setBattery(50).setStatuses(List.of("1", "2")).setTs(new Date()).setBatteryVoltage(4.1).setLocationNetworkType("4g")
////                        .setSourceOfPositionT(new SourceOfPositionT().setGpsLocated(true).setGpsSat(12).setBaseStation(true).setLocationCopied(true))
////                ,
////                new DataFromDevice()
////                        .setDeviceID("000000232324").setLat(54.000).setLng(54.00).setAzimuth(150).setSpeed(10).setBattery(50).setStatuses(List.of("1", "2")).setTs(new Date()).setBatteryVoltage(4.1).setLocationNetworkType("4g")
////                        .setSourceOfPositionT(new SourceOfPositionT().setGpsLocated(true).setGpsSat(13).setBaseStation(true).setLocationCopied(true))
////                );
////        dataFromDeviceService.saveList(dataFromDeviceList);
////
////        List<DeviceOrderInformation> deviceOrderInformationList = Arrays.asList(
////                new DeviceOrderInformation().setDeviceID("000000232323").setShipmentId(123L).setTransportRegistrationNumber(String.valueOf(456)).setTransportTypeCode(String.valueOf(304)),
////                new DeviceOrderInformation().setDeviceID("000000232324").setShipmentId(123L).setTransportRegistrationNumber(String.valueOf(456)).setTransportTypeCode(String.valueOf(305))
////        );
////        deviceOrderInformationService.saveList(deviceOrderInformationList);
//
//    }
//}
//
//
