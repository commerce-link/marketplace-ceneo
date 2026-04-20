package pl.commercelink.marketplace.ceneo;

class CeneoHttpException extends RuntimeException {

    private final int statusCode;

    CeneoHttpException(int statusCode, String body) {
        super("Ceneo HTTP " + statusCode + ": " + body);
        this.statusCode = statusCode;
    }

    int getStatusCode() {
        return statusCode;
    }
}
