package pl.commercelink.marketplace.ceneo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.commercelink.marketplace.api.ShipmentUpdate;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class CeneoOrderLifecycleEventHandlerTest {

    @Mock
    private CeneoTokenAuthClient httpClient;

    @InjectMocks
    private CeneoOrderLifecycleEventHandler handler;

    @Test
    void acceptOrderConfirmsOrder() {
        // when
        handler.acceptOrder("ORDER-1");

        // then
        verify(httpClient).getJson("/BasketService.svc/ConfirmOrder", Map.of("id", "ORDER-1"), Void.class);
    }

    @Test
    void shipOrderSetsShipmentAndSendsOrderForMappedCarrier() {
        // when
        handler.shipOrder("ORDER-1", new ShipmentUpdate("TRACK-9", "DPD", "https://track.example/TRACK-9"));

        // then
        InOrder order = inOrder(httpClient);
        order.verify(httpClient).getJson(
                "/BasketService.svc/SetOrderShipment",
                Map.of("orderId", "ORDER-1", "trackingNumber", "TRACK-9", "carrierId", "3"),
                Void.class
        );
        order.verify(httpClient).getJson("/BasketService.svc/SendOrder", Map.of("id", "ORDER-1"), Void.class);
    }

    @Test
    void shipOrderSendsOrderEvenWhenCarrierIsUnknown() {
        // when
        handler.shipOrder("ORDER-1", new ShipmentUpdate("TRACK-9", "Some Local Courier", null));

        // then
        verify(httpClient, never()).getJson(eq("/BasketService.svc/SetOrderShipment"), anyMap(), eq(Void.class));
        verify(httpClient).getJson("/BasketService.svc/SendOrder", Map.of("id", "ORDER-1"), Void.class);
    }

    @Test
    void cancelOrderDoesNotCallCeneo() {
        // when
        handler.cancelOrder("ORDER-1");

        // then
        verifyNoInteractions(httpClient);
    }
}
