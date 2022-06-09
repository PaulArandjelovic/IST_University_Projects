package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.Hub.BalanceRequest;
import pt.tecnico.bicloin.hub.grpc.Hub.BalanceResponse;

public class BalanceIT extends BaseIT {
    final String USER = "bruno";

    @Test
    public void testBalance() {
        BalanceRequest request = BalanceRequest.newBuilder().setUsername(USER).build();
        BalanceResponse response = frontend.balance(request);
        assertEquals(0, response.getBalance());
    }
}