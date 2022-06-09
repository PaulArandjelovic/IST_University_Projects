package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.Hub.TopUpRequest;
import pt.tecnico.bicloin.hub.grpc.Hub.BikeUpRequest;

public class BikeUpIT extends BaseIT {
    final static String STATION_1_ID = "gulb";
    final String STATION_1_NAME = "Gulbenkian";
    final static double STATION_1_LATITUDE = 38.7376;
    final static double STATION_1_LONGITUDE = -9.1545;

    final String STATION_2_ID = "aero";
    final String STATION_2_NAME = "Aeroporto";
    final double STATION_2_LATITUDE = 38.7759;
    final double STATION_2_LONGITUDE = -9.1433;

    final static String USER_1 = "eva";
    final static String PHONE_1 = "+155509080706";

    final String USER_2 = "carlos";
    final String PHONE_2 = "+34203040";

    final String USER_3 = "joao";
    final String PHONE_3 = "+442233112";

    final static String USER_4 = "joaquim";
    final static String PHONE_4 = "+6435234";

    final static String USER_5 = "paul";
    final static String PHONE_5 = "+9876543";

    // one-time initialization and clean-up
    @BeforeAll
    public static void oneTimeSetUp() {
        TopUpRequest topUpRequest1 = TopUpRequest.newBuilder()
                .setUsername(USER_1)
                .setAmount(100)
                .setPhone(PHONE_1)
                .build();
        frontend.topUp(topUpRequest1);

        TopUpRequest topUpRequest2 = TopUpRequest.newBuilder()
                .setUsername(USER_4)
                .setAmount(100)
                .setPhone(PHONE_4)
                .build();
        frontend.topUp(topUpRequest2);

        TopUpRequest topUpRequest3 = TopUpRequest.newBuilder()
                .setUsername(USER_5)
                .setAmount(100)
                .setPhone(PHONE_5)
                .build();
        frontend.topUp(topUpRequest3);
    }

    @Test
    public void testBikeUpOutOfRange() {
        BikeUpRequest bikeUpRequest = BikeUpRequest.newBuilder()
                .setUsername(USER_1)
                .setStationID(STATION_1_ID)
                .setLatitude(STATION_2_LATITUDE)
                .setLongitude(STATION_2_LONGITUDE)
                .build();

        Exception exception = assertThrows(io.grpc.StatusRuntimeException.class, () ->
                frontend.bikeUp(bikeUpRequest));
        assertEquals("INVALID_ARGUMENT: Too far away", exception.getMessage());
    }

    @Test
    public void testBikeUpNoBalance() {
        BikeUpRequest bikeUpRequest = BikeUpRequest.newBuilder()
                .setUsername(USER_2)
                .setStationID(STATION_1_ID)
                .setLatitude(STATION_1_LATITUDE)
                .setLongitude(STATION_1_LONGITUDE)
                .build();

        Exception exception = assertThrows(io.grpc.StatusRuntimeException.class, () ->
                frontend.bikeUp(bikeUpRequest));
        assertEquals("UNKNOWN: Not enough balance", exception.getMessage());
    }

    @Test
    public void testBikeUpSuccess() {
        BikeUpRequest bikeUpRequest = BikeUpRequest.newBuilder()
                .setUsername(USER_4)
                .setStationID(STATION_1_ID)
                .setLatitude(STATION_1_LATITUDE)
                .setLongitude(STATION_1_LONGITUDE)
                .build();
        frontend.bikeUp(bikeUpRequest);
    }

    @Test
    public void testBikeUpWithBike() {
        BikeUpRequest bikeUpRequest1 = BikeUpRequest.newBuilder()
                .setUsername(USER_5)
                .setStationID(STATION_1_ID)
                .setLatitude(STATION_1_LATITUDE)
                .setLongitude(STATION_1_LONGITUDE)
                .build();
        frontend.bikeUp(bikeUpRequest1);

        BikeUpRequest bikeUpRequest2 = BikeUpRequest.newBuilder()
                .setUsername(USER_5)
                .setStationID(STATION_1_ID)
                .setLatitude(STATION_1_LATITUDE)
                .setLongitude(STATION_1_LONGITUDE)
                .build();

        Exception exception = assertThrows(io.grpc.StatusRuntimeException.class, () ->
                frontend.bikeUp(bikeUpRequest2));
        assertEquals("UNKNOWN: Already using a bike", exception.getMessage());
    }

    @Test
    public void testBikeUpStationNoBikes() {
        TopUpRequest topUpRequest = TopUpRequest.newBuilder()
                .setUsername(USER_3)
                .setAmount(100)
                .setPhone(PHONE_3)
                .build();
        frontend.topUp(topUpRequest);

        BikeUpRequest bikeUpRequest = BikeUpRequest.newBuilder()
                .setUsername(USER_3)
                .setStationID(STATION_2_ID)
                .setLatitude(STATION_2_LATITUDE)
                .setLongitude(STATION_2_LONGITUDE)
                .build();

        Exception exception = assertThrows(io.grpc.StatusRuntimeException.class, () ->
                frontend.bikeUp(bikeUpRequest));
        assertEquals("UNKNOWN: No bikes", exception.getMessage());
    }
}