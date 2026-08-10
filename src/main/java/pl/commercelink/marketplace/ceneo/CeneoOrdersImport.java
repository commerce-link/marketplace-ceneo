package pl.commercelink.marketplace.ceneo;

import pl.commercelink.marketplace.api.MarketplaceCustomer;
import pl.commercelink.marketplace.api.MarketplaceOrder;
import pl.commercelink.marketplace.api.MarketplaceProduct;
import pl.commercelink.marketplace.api.PickupPoint;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class CeneoOrdersImport {

    private final CeneoTokenAuthClient httpClient;

    CeneoOrdersImport(CeneoTokenAuthClient httpClient) {
        this.httpClient = httpClient;
    }

    List<MarketplaceOrder> fetchOrders() {
        Map<String, String> params = new HashMap<>();
        params.put("$format", "json");
        params.put("$orderby", "CreatedDate desc");
        params.put("$expand", "OrderItems,ShippingData,InvoiceData,PickupPoint");

        String endpoint = "/BasketService.svc/OrderStates(" + CeneoOrderState.READY_FOR_SHOP_CONFIRMATION.getId() + ")/Orders";

        CeneoODataResponse<CeneoOrder> response = httpClient.getJson(
                endpoint,
                params,
                httpClient.parametricType(CeneoODataResponse.class, CeneoOrder.class)
        );

        return response.getResults().stream()
                .map(this::toMarketplaceOrder)
                .collect(Collectors.toList());
    }

    private MarketplaceOrder toMarketplaceOrder(CeneoOrder order) {
        CeneoShippingData shipping = firstOrNull(order.getShippingData());
        CeneoInvoiceData invoice = firstOrNull(order.getInvoiceData());

        MarketplaceCustomer customer = buildCustomer(shipping, invoice, order.getPickupPoint());

        List<MarketplaceProduct> products = order.getOrderItems() == null
                ? List.of()
                : order.getOrderItems().stream()
                        .map(item -> new MarketplaceProduct(
                                item.getName(),
                                item.getShopProductId(),
                                item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO,
                                item.getCount() != null ? item.getCount() : 0L,
                                BigDecimal.ZERO
                        ))
                        .collect(Collectors.toList());

        return new MarketplaceOrder(
                order.getId(),
                customer,
                products,
                order.getDeliveryCost() != null ? order.getDeliveryCost() : BigDecimal.ZERO,
                resolvePaymentType(order.getPaymentTypeId()),
                order.getDisplayedOrderId(),
                null,
                toPickupPoint(order.getPickupPoint())
        );
    }

    private MarketplaceCustomer buildCustomer(CeneoShippingData shipping, CeneoInvoiceData invoice, CeneoPickupPoint point) {
        MarketplaceCustomer.CustomerType type = invoice != null && invoice.isCompany()
                ? MarketplaceCustomer.CustomerType.COMPANY
                : MarketplaceCustomer.CustomerType.INDIVIDUAL;

        String name = shipping != null ? shipping.fullName() : null;
        String company = invoice != null ? invoice.getInvoiceCompanyName() : null;
        String email = shipping != null ? shipping.getEmail() : null;
        String phone = shipping != null ? shipping.getPhoneNumber() : null;
        String taxId = invoice != null ? invoice.getInvoiceNIP() : null;

        MarketplaceCustomer.Address billingAddress = invoice != null
                ? invoice.toAddress()
                : shipping != null ? shipping.toAddress() : null;

        MarketplaceCustomer.Address shippingAddress = toShippingAddress(shipping, point);

        return new MarketplaceCustomer(type, name, company, email, phone, taxId, billingAddress, shippingAddress);
    }

    private MarketplaceCustomer.Address toShippingAddress(CeneoShippingData shipping, CeneoPickupPoint point) {
        if (shipping == null) {
            return null;
        }
        if (point == null || point.getCode() == null || point.getCode().isBlank()) {
            return shipping.toAddress();
        }
        MarketplaceCustomer.Address address = shipping.toAddress();
        return new MarketplaceCustomer.Address(
                address.name(),
                address.phone(),
                point.getStreetAddress() != null ? point.getStreetAddress() : address.street(),
                point.getPostCode() != null ? point.getPostCode() : address.postalCode(),
                point.getCity() != null ? point.getCity() : address.city(),
                address.country());
    }

    private PickupPoint toPickupPoint(CeneoPickupPoint point) {
        if (point == null || point.getCode() == null || point.getCode().isBlank()) {
            return null;
        }
        return new PickupPoint(point.getCode());
    }

    private String resolvePaymentType(Integer paymentTypeId) {
        if (paymentTypeId == null) return "DirectDebit";
        CeneoPaymentType type = CeneoPaymentType.fromId(paymentTypeId);
        if (type == null) return "DirectDebit";
        return switch (type) {
            case CASH_ON_DELIVERY -> "CashOnDelivery";
            case INSTALLMENTS -> "Installments";
            case SHOP_DIRECT_PAYMENT -> "DirectDebit";
            case FAST_PAYMENT, CREDIT_CARD, SPLIT_PAYMENT -> "OnlinePayment";
        };
    }

    private static <T> T firstOrNull(List<T> list) {
        return list == null || list.isEmpty() ? null : list.get(0);
    }
}
