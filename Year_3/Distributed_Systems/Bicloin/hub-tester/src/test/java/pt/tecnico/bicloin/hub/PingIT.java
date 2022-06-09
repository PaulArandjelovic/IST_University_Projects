package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.Hub.PingRequest;
import pt.tecnico.bicloin.hub.grpc.Hub.PingResponse;


public class PingIT extends BaseIT {
    @Test
    public void testPing() {
        PingRequest request = PingRequest.newBuilder().build();
        PingResponse response = frontend.ping(request);
        assertEquals("OK", response.getOutputText());
    }
}