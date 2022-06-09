package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.Hub.LocateStationRequest;
import pt.tecnico.bicloin.hub.grpc.Hub.LocateStationResponse;


public class LocateStationIT extends BaseIT {
    @Test
    public void testLocateStationLowNumberSuccess() {
        LocateStationRequest request = LocateStationRequest.newBuilder()
                .setLatitude(38.7759)
                .setLongitude(-9.1433)
                .setNumOfStations(1)
                .build();
        LocateStationResponse response = frontend.locateStation(request);

        assertEquals(1, response.getStationIDsCount());
        assertEquals("aero", response.getStationIDs(0));
    }

    @Test
    public void testLocateStationAverageNumberSuccess() {
        LocateStationRequest request = LocateStationRequest.newBuilder()
                .setLatitude(38.7759)
                .setLongitude(-9.1433)
                .setNumOfStations(3)
                .build();
        LocateStationResponse response = frontend.locateStation(request);

        assertEquals(3, response.getStationIDsCount());
        assertEquals("aero", response.getStationIDs(0));
        assertEquals("gulb", response.getStationIDs(1));
        assertEquals("ista", response.getStationIDs(2));
    }

    @Test
    public void testLocateStationTooManyStationsRequested() {
        LocateStationRequest request = LocateStationRequest.newBuilder()
                .setLatitude(38.7759)
                .setLongitude(-9.1433)
                .setNumOfStations(1000)
                .build();

        Exception exception = assertThrows(io.grpc.StatusRuntimeException.class, () ->
                frontend.locateStation(request));
        assertEquals("INVALID_ARGUMENT: There are not enough stations", exception.getMessage());
    }
}