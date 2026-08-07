package pl.commercelink.marketplace.ceneo;

import pl.commercelink.marketplace.api.AliasedCarrier;

import java.util.ArrayList;
import java.util.List;

enum CeneoParcelCarrier implements AliasedCarrier {

    INPOST(1, "InPost", List.of("Paczkomat", "Paczkomaty", "InPost Paczkomaty", "InPost Kurier")),
    DHL(2, "DHL", List.of()),
    DPD(3, "DPD", List.of()),
    POCZTA_POLSKA(4, "PocztaPolska/Pocztex", List.of("Poczta Polska", "Pocztex", "PocztaPolska", "Poczta")),
    ORLEN_PACZKA(5, "OrlenPaczka", List.of("Orlen Paczka", "Orlen", "RUCH")),
    GLS(6, "GLS", List.of()),
    DB_SCHENKER(7, "DB Schenker", List.of("Schenker", "DBSchenker")),
    FEDEX(8, "FedEx", List.of("Fedex")),
    RABEN(15, "Raben", List.of()),
    UPS(18, "UPS", List.of());

    private final int id;
    private final String officialName;
    private final List<String> aliases;

    CeneoParcelCarrier(int id, String officialName, List<String> aliases) {
        this.id = id;
        this.officialName = officialName;
        this.aliases = aliases;
    }

    int getId() {
        return id;
    }

    @Override
    public List<String> aliases() {
        List<String> all = new ArrayList<>(aliases);
        all.add(officialName);
        return all;
    }

    static CeneoParcelCarrier fromCarrierName(String carrierName) {
        return AliasedCarrier.deserialize(values(), carrierName);
    }
}
