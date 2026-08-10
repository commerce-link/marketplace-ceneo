package pl.commercelink.marketplace.ceneo;

import java.util.Arrays;

enum CeneoParcelCarrier {

    INPOST("INPOST", 1),
    DHL("DHL", 2),
    DPD("DPD", 3),
    POCZTA_POLSKA("POCZTA_POLSKA", 4),
    ORLEN_PACZKA("ORLEN", 5),
    GLS("GLS", 6),
    DB_SCHENKER("DB_SCHENKER", 7),
    FEDEX("FEDEX", 8),
    RABEN("RABEN", 15),
    UPS("UPS", 18);

    private final String carrier;
    private final int id;

    CeneoParcelCarrier(String carrier, int id) {
        this.carrier = carrier;
        this.id = id;
    }

    static Integer idFor(String carrier) {
        return Arrays.stream(values())
                .filter(candidate -> candidate.carrier.equalsIgnoreCase(carrier))
                .map(candidate -> candidate.id)
                .findFirst()
                .orElse(null);
    }
}
