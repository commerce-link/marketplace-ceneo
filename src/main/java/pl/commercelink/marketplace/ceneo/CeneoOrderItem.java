package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
class CeneoOrderItem {

    @JsonProperty("ShopProductId")
    private String shopProductId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Price")
    private BigDecimal price;

    @JsonProperty("Count")
    private Long count;

    String getShopProductId() {
        return shopProductId;
    }

    String getName() {
        return name;
    }

    BigDecimal getPrice() {
        return price;
    }

    Long getCount() {
        return count;
    }
}
