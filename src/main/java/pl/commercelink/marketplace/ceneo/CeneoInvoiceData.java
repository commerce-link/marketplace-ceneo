package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.commercelink.marketplace.api.MarketplaceCustomer;

@JsonIgnoreProperties(ignoreUnknown = true)
class CeneoInvoiceData {

    @JsonProperty("InvoiceFirstName")
    private String invoiceFirstName;

    @JsonProperty("InvoiceLastName")
    private String invoiceLastName;

    @JsonProperty("InvoiceCompanyName")
    private String invoiceCompanyName;

    @JsonProperty("InvoiceNIP")
    private String invoiceNIP;

    @JsonProperty("InvoiceAddress")
    private String address;

    @JsonProperty("InvoicePostCode")
    private String postCode;

    @JsonProperty("InvoiceCity")
    private String city;

    @JsonProperty("InvoiceCountry")
    private String country;

    String getInvoiceCompanyName() {
        return invoiceCompanyName;
    }

    String getInvoiceNIP() {
        return invoiceNIP;
    }

    boolean isCompany() {
        return invoiceCompanyName != null && !invoiceCompanyName.isBlank();
    }

    String fullName() {
        StringBuilder sb = new StringBuilder();
        if (invoiceFirstName != null) sb.append(invoiceFirstName);
        if (invoiceLastName != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(invoiceLastName);
        }
        return sb.toString();
    }

    MarketplaceCustomer.Address toAddress() {
        return new MarketplaceCustomer.Address(
                fullName(),
                null,
                address,
                postCode,
                city,
                country
        );
    }
}
