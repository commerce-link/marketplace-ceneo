package pl.commercelink.marketplace.ceneo;

import pl.commercelink.marketplace.api.InvoiceUpdate;
import pl.commercelink.marketplace.api.ShipmentUpdate;

import java.util.Map;

class CeneoOrderLifecycleEventHandler {

    private final CeneoTokenAuthClient httpClient;

    CeneoOrderLifecycleEventHandler(CeneoTokenAuthClient httpClient) {
        this.httpClient = httpClient;
    }

    void acceptOrder(String externalOrderId) {
        getById("/BasketService.svc/ConfirmOrder", externalOrderId);
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
