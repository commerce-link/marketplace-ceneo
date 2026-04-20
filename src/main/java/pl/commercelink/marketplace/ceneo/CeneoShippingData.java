package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.commercelink.marketplace.api.MarketplaceCustomer;

@JsonIgnoreProperties(ignoreUnknown = true)
class CeneoShippingData {

    @JsonProperty("Email")
    private String email;

    @JsonProperty("PhoneNumber")
    private String phoneNumber;

    @JsonProperty("ShippingFirstName")
    private String shippingFirstName;

    @JsonProperty("ShippingLastName")
    private String shippingLastName;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("PostCode")
    private String postCode;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Country")
    private String country;

    String getEmail() {
        return email;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    String fullName() {
        StringBuilder sb = new StringBuilder();
        if (shippingFirstName != null) sb.append(shippingFirstName);
        if (shippingLastName != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(shippingLastName);
        }
        return sb.toString();
    }

    MarketplaceCustomer.Address toAddress() {
        return new MarketplaceCustomer.Address(
                fullName(),
                phoneNumber,
                address,
                postCode,
                city,
                country
        );
    }
}
