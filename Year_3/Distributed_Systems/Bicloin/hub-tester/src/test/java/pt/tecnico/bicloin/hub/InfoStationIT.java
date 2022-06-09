package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.Hub.InfoStationRequest;
import pt.tecnico.bicloin.hub.grpc.Hub.InfoStationResponse;

import pt.tecnico.bicloin.hub.grpc.Hub.BikeUpRequest;
import pt.tecnico.bicloin.hub.grpc.Hub.BikeDownRequest;

import pt.tecnico.bicloin.hub.grpc.Hub.TopUpRequest;

public class InfoStationIT extends BaseIT {
    final String STATION_ID = "jero";
    final String STATION_NAME = "Jerónimos";
    final double STATION_LATITUDE = 38.6972;
    final double STATION_LONGITUDE = -9.2064;
    final int STATION_CAPACITY = 30;
    final int STATION_PRIZE = 3;

    final String USER_1 = "eva";
    final String USER_2 = "alice";
    final String PHONE_USER_1 = "+155509080706";
    final String PHONE_USER_2 = "+35191102030";

    @Test
    public void testInfoStationNoWithdrawalsNoDepositsSuccess() {
        InfoStationRequest request = InfoStationRequest.newBuilder().setStationID(STATION_ID).build();
        InfoStationResponse response = frontend.infoStation(request);

        assertEquals(STATION_NAME, response.getName());
        assertEquals(STATION_LATITUDE, response.getLatitude());
        assertEquals(STATION_LONGITUDE, response.getLongitude());
        assertEquals(STATION_CAPACITY, response.getCapacity());
        assertEquals(20, response.getNumOfAvailableBikes());
        assertEquals(STATION_PRIZE, response.getPrize());
        assertEquals(0, response.getWithdrawals());
        assertEquals(0, response.getDeposits());
    }

    @Test
    public void testInfoStationTwoWithdrawalsOneDepositSuccess() {
        TopUpRequest topUpRequest1 = TopUpRequest.newBuilder()
                .setUsername(USER_1)
                .setAmount(100)
                .setPhone(PHONE_USER_1)
                .build();
        TopUpRequest topUpRequest2 = TopUpRequest.newBuilder()
                .setUsername(USER_2)
                .setAmount(100)
                .setPhone(PHONE_USER_2)
                .build();

        frontend.topUp(topUpRequest1);
        frontend.topUp(topUpRequest2);


        BikeUpRequest bikeUpRequest1 = BikeUpRequest.newBuilder()
                .setUsername(USER_1)
                .setStationID(STATION_ID)
                .setLatitude(STATION_LATITUDE)
                .setLongitude(STATION_LONGITUDE)
                .build();
        BikeUpRequest bikeUpRequest2 = BikeUpRequest.newBuilder()
                .setUsername(USER_2)
                .setStationID(STATION_ID)
                .setLatitude(STATION_LATITUDE)
                .setLongitude(STATION_LONGITUDE)
                .build();

        frontend.bikeUp(bikeUpRequest1);
        frontend.bikeUp(bikeUpRequest2);

        BikeDownRequest bikeDownRequest = BikeDownRequest.newBuilder()
                .setUsername(USER_2)
                .setStationID(STATION_ID)
                .setLatitude(STATION_LATITUDE)
                .setLongitude(STATION_LONGITUDE)
                .build();

        frontend.bikeDown(bikeDownRequest);

        InfoStationRequest request = InfoStationRequest.newBuilder().setStationID(STATION_ID).build();
        InfoStationResponse response = frontend.infoStation(request);

        assertEquals(STATION_NAME, response.getName());
        assertEquals(STATION_LATITUDE, response.getLatitude());
        assertEquals(STATION_LONGITUDE, response.getLongitude());
        assertEquals(STATION_CAPACITY, response.getCapacity());
        assertEquals(19, response.getNumOfAvailableBikes());
        assertEquals(STATION_PRIZE, response.getPrize());
        assertEquals(2, response.getWithdrawals());
        assertEquals(1, response.getDeposits());
    }
}