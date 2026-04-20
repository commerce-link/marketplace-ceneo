package pl.commercelink.marketplace.ceneo;

enum CeneoOrderState {

    WAITING_FOR_PLATNOSCIPL_CONFIRMATION(10),
    UNCONFIRMED_BY_PLATNOSCI(15),
    WAITING_FOR_PAYMENT(20),
    READY_FOR_SHOP_CONFIRMATION(30),
    CONFIRMED_BY_SHOP(40),
    SHIPPED(50),
    CANCELED(60);

    private final int id;

    CeneoOrderState(int id) {
        this.id = id;
    }

    int getId() {
        return id;
    }
}
