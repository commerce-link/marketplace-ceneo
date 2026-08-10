package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.commercelink.marketplace.api.InvoiceUpdate;
import pl.commercelink.marketplace.api.ShipmentUpdate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CeneoOrderLifecycleEventHandlerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String PENDING_CONFIRMATION_ORDERS = "/BasketService.svc/OrderStates(30)/Orders";

    @Mock
    private CeneoTokenAuthClient httpClient;

    @InjectMocks
    private CeneoOrderLifecycleEventHandler handler;

    @Test
    void acceptOrderConfirmsOrderStillAwaitingShopConfirmation() throws Exception {
        // given
        givenOrdersAwaitingConfirmation("ORDER-1", "ORDER-2");

        // when
        handler.acceptOrder("ORDER-1");

        // then
        verify(httpClient).getJson("/BasketService.svc/ConfirmOrder", Map.of("id", "ORDER-1"), Void.class);
    }

    @Test
    void acceptOrderSkipsConfirmationWhenOrderIsNoLongerAwaitingIt() throws Exception {
        // given
        givenOrdersAwaitingConfirmation("ORDER-2");

        // when
        handler.acceptOrder("ORDER-1");

        // then
        verify(httpClient, never()).getJson(eq("/BasketService.svc/ConfirmOrder"), anyMap(), eq(Void.class));
    }

    @Test
    void acceptOrderSkipsConfirmationWhenNoOrderAwaitsIt() throws Exception {
        // given
        givenOrdersAwaitingConfirmation();

        // when
        handler.acceptOrder("ORDER-1");

        // then
        verify(httpClient, never()).getJson(eq("/BasketService.svc/ConfirmOrder"), anyMap(), eq(Void.class));
    }

    @Test
    void shipOrderSetsShipmentAndSendsOrderForMappedCarrier() {
        // when
        handler.shipOrder("ORDER-1", new ShipmentUpdate("TRACK-9", "DPD", "DPD", "https://track.example/TRACK-9"));

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
    void shipOrderFailsLoudlyWhenTrackedCarrierIsUnknown() {
        // when / then
        assertThrows(IllegalStateException.class, () ->
                handler.shipOrder("ORDER-1", new ShipmentUpdate("TRACK-9", null, "Some Local Courier", null)));
        verify(httpClient, never()).getJson(eq("/BasketService.svc/SetOrderShipment"), anyMap(), eq(Void.class));
        verify(httpClient, never()).getJson(eq("/BasketService.svc/SendOrder"), anyMap(), eq(Void.class));
    }

    @Test
    void shipOrderSendsOrderWhenShipmentHasNoTracking() {
        // when
        handler.shipOrder("ORDER-1", new ShipmentUpdate(null, null, null, null));

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

    @Test
    void updateInvoiceDoesNotCallCeneo() {
        // when
        handler.updateInvoice("ORDER-1", new InvoiceUpdate("FV-1", "https://x"));

        // then
        verifyNoInteractions(httpClient);
    }

    private void givenOrdersAwaitingConfirmation(String... orderIds) throws Exception {
        String results = String.join(",", java.util.Arrays.stream(orderIds)
                .map("{\"Id\":\"%s\"}"::formatted)
                .toList());
        JavaType responseType = MAPPER.getTypeFactory()
                .constructParametricType(CeneoODataResponse.class, CeneoOrder.class);
        CeneoODataResponse<CeneoOrder> response = MAPPER.readValue(
                "{\"d\":{\"results\":[%s]}}".formatted(results),
                responseType
        );

        when(httpClient.parametricType(CeneoODataResponse.class, CeneoOrder.class)).thenReturn(responseType);
        when(httpClient.getJson(eq(PENDING_CONFIRMATION_ORDERS), anyMap(), eq(responseType))).thenReturn(response);
    }
}
