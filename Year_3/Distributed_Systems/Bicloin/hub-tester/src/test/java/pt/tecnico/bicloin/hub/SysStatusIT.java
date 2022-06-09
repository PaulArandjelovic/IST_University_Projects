package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.Hub.SysStatusRequest;
import pt.tecnico.bicloin.hub.grpc.Hub.SysStatusResponse;

public class SysStatusIT extends BaseIT {
    @Test
    public void testSysStatus() {
        SysStatusRequest request = SysStatusRequest.newBuilder().build();
        SysStatusResponse response = frontend.sysStatus(request);

        for (int i = 0; i < response.getResultsCount(); i++) {
            assertEquals(true, response.getResults(i).getIsUp());
        }
    }
}