package pl.commercelink.marketplace.ceneo;

import pl.commercelink.marketplace.api.InvoiceUpdate;
import pl.commercelink.marketplace.api.ShipmentUpdate;

import java.util.Map;

class CeneoOrderLifecycleEventHandler {

    private static final String PENDING_CONFIRMATION_ORDERS =
            "/BasketService.svc/OrderStates(" + CeneoOrderState.READY_FOR_SHOP_CONFIRMATION.getId() + ")/Orders";

    private final CeneoTokenAuthClient httpClient;

    CeneoOrderLifecycleEventHandler(CeneoTokenAuthClient httpClient) {
        this.httpClient = httpClient;
    }

    void acceptOrder(String externalOrderId) {
        if (!isAwaitingShopConfirmation(externalOrderId)) {
            return;
        }
        getById("/BasketService.svc/ConfirmOrder", externalOrderId);
    }

    private boolean isAwaitingShopConfirmation(String externalOrderId) {
        CeneoODataResponse<CeneoOrder> response = httpClient.getJson(
                PENDING_CONFIRMATION_ORDERS,
                Map.of("$format", "json"),
                httpClient.parametricType(CeneoODataResponse.class, CeneoOrder.class)
        );

        return response.getResults().stream()
                .anyMatch(order -> externalOrderId.equals(order.getId()));
    }

    void shipOrder(String externalOrderId, ShipmentUpdate update) {
        CeneoParcelCarrier carrier = CeneoParcelCarrier.fromCarrierName(update.carrier());
        if (carrier != null) {
            Map<String, String> shipmentParams = Map.of(
                    "orderId", externalOrderId,
                    "trackingNumber", update.trackingNo(),
                    "carrierId", String.valueOf(carrier.getId())
            );
            httpClient.getJson("/BasketService.svc/SetOrderShipment", shipmentParams, Void.class);
        }

        getById("/BasketService.svc/SendOrder", externalOrderId);
    }

    private void getById(String path, String externalOrderId) {
        httpClient.getJson(path, Map.of("id", externalOrderId), Void.class);
    }

    void cancelOrder(String externalOrderId) {
        // Ceneo Merchant API exposes no confirmed seller-side cancel operation;
        // verify BasketService.svc metadata before implementing.
    }

    void updateInvoice(String externalOrderId, InvoiceUpdate update) {
    }
}
