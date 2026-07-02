package pl.commercelink.marketplace.ceneo;

import pl.commercelink.marketplace.api.InvoiceUpdate;
import pl.commercelink.marketplace.api.ShipmentUpdate;

import java.util.HashMap;
import java.util.Map;

class CeneoOrderLifecycleEventHandler {

    private final CeneoTokenAuthClient httpClient;

    CeneoOrderLifecycleEventHandler(CeneoTokenAuthClient httpClient) {
        this.httpClient = httpClient;
    }

    void acceptOrder(String externalOrderId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", externalOrderId);
        httpClient.getJson("/BasketService.svc/ConfirmOrder", params, Void.class);
    }

    void shipOrder(String externalOrderId, ShipmentUpdate update) {
        CeneoParcelCarrier carrier = CeneoParcelCarrier.fromCarrierName(update.carrier());
        if (carrier != null) {
            Map<String, String> shipmentParams = new HashMap<>();
            shipmentParams.put("orderId", externalOrderId);
            shipmentParams.put("trackingNumber", update.trackingNo());
            shipmentParams.put("carrierId", String.valueOf(carrier.getId()));
            httpClient.getJson("/BasketService.svc/SetOrderShipment", shipmentParams, Void.class);
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", externalOrderId);
        httpClient.getJson("/BasketService.svc/SendOrder", params, Void.class);
    }

    void cancelOrder(String externalOrderId) {
        // Ceneo Merchant API exposes no confirmed seller-side cancel operation;
        // verify BasketService.svc metadata before implementing.
    }

    void updateInvoice(String externalOrderId, InvoiceUpdate update) {
    }
}
