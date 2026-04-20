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
    private List<CeneoOrderItem> orderItems;

    @JsonProperty("ShippingData")
    private List<CeneoShippingData> shippingData;

    @JsonProperty("InvoiceData")
    private List<CeneoInvoiceData> invoiceData;

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
        return orderItems;
    }

    List<CeneoShippingData> getShippingData() {
        return shippingData;
    }

    List<CeneoInvoiceData> getInvoiceData() {
        return invoiceData;
    }
}
