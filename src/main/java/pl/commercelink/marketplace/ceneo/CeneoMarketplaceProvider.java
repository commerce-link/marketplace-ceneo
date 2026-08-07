package pl.commercelink.marketplace.ceneo;

import pl.commercelink.marketplace.api.InvoiceUpdate;
import pl.commercelink.marketplace.api.MarketplaceOffer;
import pl.commercelink.marketplace.api.MarketplaceOrder;
import pl.commercelink.marketplace.api.MarketplaceProvider;
import pl.commercelink.marketplace.api.ShipmentUpdate;

import java.util.List;

class CeneoMarketplaceProvider implements MarketplaceProvider<CeneoParcelCarrier> {

    private final CeneoOrdersImport ordersImport;
    private final CeneoOrderLifecycleEventHandler lifecycleHandler;
    private final CeneoOfferExport offerExport;

    CeneoMarketplaceProvider(CeneoTokenAuthClient tokenAuthClient) {
        this.ordersImport = new CeneoOrdersImport(tokenAuthClient);
        this.lifecycleHandler = new CeneoOrderLifecycleEventHandler(tokenAuthClient);
        this.offerExport = new CeneoOfferExport();
    }

    @Override
    public List<MarketplaceOrder<CeneoParcelCarrier>> fetchOrders() {
        return ordersImport.fetchOrders();
    }

    @Override
    public void exportOffers(List<MarketplaceOffer> toPublish, List<MarketplaceOffer> toRemove) {
        offerExport.export(toPublish, toRemove);
    }

    @Override
    public void acceptOrder(String externalOrderId) {
        lifecycleHandler.acceptOrder(externalOrderId);
    }

    @Override
    public void shipOrder(String externalOrderId, ShipmentUpdate update) {
        lifecycleHandler.shipOrder(externalOrderId, update);
    }

    @Override
    public void cancelOrder(String externalOrderId) {
        lifecycleHandler.cancelOrder(externalOrderId);
    }

    @Override
    public void updateInvoice(String externalOrderId, InvoiceUpdate update) {
        lifecycleHandler.updateInvoice(externalOrderId, update);
    }
}
