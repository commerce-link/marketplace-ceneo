package pl.commercelink.marketplace.ceneo;

import java.util.List;

enum CeneoParcelCarrier {

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

    static CeneoParcelCarrier fromCarrierName(String carrierName) {
        if (carrierName == null || carrierName.isBlank()) return null;
        String normalized = carrierName.trim().toUpperCase();
        for (CeneoParcelCarrier c : values()) {
            if (c.officialName.toUpperCase().equals(normalized)) return c;
            for (String alias : c.aliases) {
                if (alias.toUpperCase().equals(normalized)) return c;
            }
        }
        for (CeneoParcelCarrier c : values()) {
            if (normalized.contains(c.name())) return c;
            for (String alias : c.aliases) {
                if (normalized.contains(alias.toUpperCase())) return c;
            }
        }
        return null;
    }
}
