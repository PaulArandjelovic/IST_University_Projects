package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.Hub.*;

public class BikeDownIT extends BaseIT {
    final String STATION_1_ID = "hosp";
    final String STATION_1_NAME = "Hospital";
    final double STATION_1_LATITUDE = 38.7279;
    final double STATION_1_LONGITUDE = -9.1391;

    final static String STATION_2_ID = "gulb";
    final String STATION_2_NAME = "Gulbenkian";
    final static double STATION_2_LATITUDE = 38.7376;
    final static double STATION_2_LONGITUDE = -9.1545;

    final static String USER_1 = "ernesto";

    final static String USER_2 = "simao";
    final static String PHONE_2 = "+3621423";

    final static String USER_3 = "bob";
    final static String PHONE_3 = "+769563";


    @BeforeAll
    public static void oneTimeSetUp() {
        TopUpRequest topUpRequest = TopUpRequest.newBuilder()
                .setUsername(USER_3)
                .setAmount(100)
                .setPhone(PHONE_3)
                .build();
        BikeUpRequest bikeUpRequest = BikeUpRequest.newBuilder()
                .setUsername(USER_3)
                .setStationID(STATION_2_ID)
                .setLatitude(STATION_2_LATITUDE)
                .setLongitude(STATION_2_LONGITUDE)
                .build();
        frontend.topUp(topUpRequest);
        frontend.bikeUp(bikeUpRequest);
    }

    @Test
    public void testBikeDownNoBike() {
        BikeDownRequest bikeDownRequest = BikeDownRequest.newBuilder()
                .setUsername(USER_1)
                .setStationID(STATION_2_ID)
                .setLatitude(STATION_2_LATITUDE)
                .setLongitude(STATION_2_LONGITUDE)
                .build();

        Exception exception = assertThrows(io.grpc.StatusRuntimeException.class, () ->
                frontend.bikeDown(bikeDownRequest));
        assertEquals("UNKNOWN: Not using a bike", exception.getMessage());
    }

    @Test
    public void testBikeDownSuccess() {
        TopUpRequest topUpRequest = TopUpRequest.newBuilder()
                .setUsername(USER_2)
                .setAmount(100)
                .setPhone(PHONE_2)
                .build();
        BikeUpRequest bikeUpRequest = BikeUpRequest.newBuilder()
                .setUsername(USER_2)
                .setStationID(STATION_2_ID)
                .setLatitude(STATION_2_LATITUDE)
                .setLongitude(STATION_2_LONGITUDE)
                .build();
        BikeDownRequest bikeDownRequest = BikeDownRequest.newBuilder()
                .setUsername(USER_2)
                .setStationID(STATION_2_ID)
                .setLatitude(STATION_2_LATITUDE)
                .setLongitude(STATION_2_LONGITUDE)
                .build();
        frontend.topUp(topUpRequest);
        frontend.bikeUp(bikeUpRequest);
        frontend.bikeDown(bikeDownRequest);

        BalanceRequest balanceRequest = BalanceRequest.newBuilder().setUsername(USER_2).build();
        BalanceResponse response = frontend.balance(balanceRequest);

        assertEquals(92, response.getBalance());
    }


    @Test
    public void testBikeDownOutOfRange() {
        BikeDownRequest bikeDownRequest = BikeDownRequest.newBuilder()
            .setUsername(USER_3)
            .setStationID(STATION_2_ID)
            .setLatitude(STATION_1_LATITUDE)
            .setLongitude(STATION_1_LONGITUDE)
            .build();

        Exception exception = assertThrows(io.grpc.StatusRuntimeException.class, () ->
                frontend.bikeDown(bikeDownRequest));
        assertEquals("INVALID_ARGUMENT: Too far away", exception.getMessage());
    }

    @Test
    public void testBikeDownStationNoCapacity() {
        BikeDownRequest bikeDownRequest = BikeDownRequest.newBuilder()
                .setUsername(USER_3)
                .setStationID(STATION_1_ID)
                .setLatitude(STATION_1_LATITUDE)
                .setLongitude(STATION_1_LONGITUDE)
                .build();

        Exception exception = assertThrows(io.grpc.StatusRuntimeException.class, () ->
                frontend.bikeDown(bikeDownRequest));
        assertEquals("UNKNOWN: No docks", exception.getMessage());
    }
}