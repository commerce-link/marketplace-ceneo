package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class CeneoPickupPoint {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("StreetAddress")
    private String streetAddress;

    @JsonProperty("City")
    private String city;

    @JsonProperty("PostCode")
    private String postCode;

    String getCode() {
        return name;
    }

    String getStreetAddress() {
        return streetAddress;
    }

    String getCity() {
        return city;
    }

    String getPostCode() {
        return postCode;
    }
}
