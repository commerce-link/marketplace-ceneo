package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class CeneoOrder {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("DisplayedOrderId")
    private String displayedOrderId;

    @JsonProperty("PaymentTypeId")
    private Integer paymentTypeId;

    @JsonProperty("DeliveryCost")
    private BigDecimal deliveryCost;

    @JsonProperty("OrderItems")
    private CeneoODataList<CeneoOrderItem> orderItems;

    @JsonProperty("ShippingData")
    private CeneoODataList<CeneoShippingData> shippingData;

    @JsonProperty("InvoiceData")
    private CeneoODataList<CeneoInvoiceData> invoiceData;

    @JsonProperty("PickupPoint")
    private CeneoODataList<CeneoPickupPoint> pickupPoint;

    String getId() {
        return id;
    }

    String getDisplayedOrderId() {
        return displayedOrderId;
    }

    Integer getPaymentTypeId() {
        return paymentTypeId;
    }

    BigDecimal getDeliveryCost() {
        return deliveryCost;
    }

    List<CeneoOrderItem> getOrderItems() {
        return orderItems == null ? List.of() : orderItems.getResults();
    }

    List<CeneoShippingData> getShippingData() {
        return shippingData == null ? List.of() : shippingData.getResults();
    }

    List<CeneoInvoiceData> getInvoiceData() {
        return invoiceData == null ? List.of() : invoiceData.getResults();
    }

    CeneoPickupPoint getPickupPoint() {
        if (pickupPoint == null || pickupPoint.getResults().isEmpty()) {
            return null;
        }
        return pickupPoint.getResults().get(0);
    }
}
