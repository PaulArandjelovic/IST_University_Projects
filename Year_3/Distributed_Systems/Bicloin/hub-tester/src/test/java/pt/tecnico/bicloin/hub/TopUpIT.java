package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pt.tecnico.bicloin.hub.grpc.Hub.TopUpRequest;
import pt.tecnico.bicloin.hub.grpc.Hub.BalanceResponse;

public class TopUpIT extends BaseIT {
    final String USER = "diana";
    final String USER_NUMBER = "+34010203";
    final String INCORRECT_NUMBER = "+34010204";

    @Test
    public void testTopUpSuccess() {
        TopUpRequest request = TopUpRequest.newBuilder()
                .setUsername(USER)
                .setAmount(10)
                .setPhone(USER_NUMBER)
                .build();
        BalanceResponse response = frontend.topUp(request);
        assertEquals(10, response.getBalance());
    }

    @Test
    public void testTopUpIncorrectPhoneNumber() {
        TopUpRequest request = TopUpRequest.newBuilder()
                .setUsername(USER)
                .setAmount(10)
                .setPhone(INCORRECT_NUMBER)
                .build();
        Exception exception = assertThrows(io.grpc.StatusRuntimeException.class, () ->
                frontend.topUp(request));
        assertEquals("INVALID_ARGUMENT: Phone number does not match username", exception.getMessage());
    }

    @Test
    public void testTopUpNegativeAmount() {
        TopUpRequest request = TopUpRequest.newBuilder()
                .setUsername(USER)
                .setAmount(-10)
                .setPhone(USER_NUMBER)
                .build();
        Exception exception = assertThrows(io.grpc.StatusRuntimeException.class, () ->
                frontend.topUp(request));
        assertEquals("INVALID_ARGUMENT: Invalid top-up value", exception.getMessage());
    }

}