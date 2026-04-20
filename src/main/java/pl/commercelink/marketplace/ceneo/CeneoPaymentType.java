package pl.commercelink.marketplace.ceneo;

enum CeneoPaymentType {

    FAST_PAYMENT(1),
    CASH_ON_DELIVERY(2),
    CREDIT_CARD(3),
    INSTALLMENTS(4),
    SHOP_DIRECT_PAYMENT(5),
    SPLIT_PAYMENT(6);

    private final int id;

    CeneoPaymentType(int id) {
        this.id = id;
    }

    static CeneoPaymentType fromId(int id) {
        for (CeneoPaymentType type : values()) {
            if (type.id == id) return type;
        }
        return null;
    }
}
