package pl.commercelink.marketplace.ceneo;

import pl.commercelink.marketplace.api.InvoiceUpdate;
import pl.commercelink.marketplace.api.MarketplaceOrderStatus;
import pl.commercelink.marketplace.api.ShipmentUpdate;

import java.util.HashMap;
import java.util.Map;

class CeneoOrderLifecycleEventHandler {

    private final CeneoTokenAuthClient httpClient;

    CeneoOrderLifecycleEventHandler(CeneoTokenAuthClient httpClient) {
        this.httpClient = httpClient;
    }

    void updateOrderStatus(String externalOrderId, MarketplaceOrderStatus status) {
        switch (status) {
            case InProgress -> confirmOrder(externalOrderId);
            case Shipping -> sendOrder(externalOrderId);
            case Delivered, Completed -> {
            }
        }
    }

    void updateShipment(String externalOrderId, ShipmentUpdate update) {
        CeneoParcelCarrier carrier = CeneoParcelCarrier.fromCarrierName(update.carrier());
        if (carrier == null) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("orderId", externalOrderId);
        params.put("trackingNumber", update.trackingNo());
        params.put("carrierId", String.valueOf(carrier.getId()));

        httpClient.getJson("/BasketService.svc/SetOrderShipment", params, Void.class);
    }

    void updateInvoice(String externalOrderId, InvoiceUpdate update) {
    }

    private void confirmOrder(String orderId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", orderId);
        httpClient.getJson("/BasketService.svc/ConfirmOrder", params, Void.class);
    }

    private void sendOrder(String orderId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", orderId);
        httpClient.getJson("/BasketService.svc/SendOrder", params, Void.class);
    }
}
