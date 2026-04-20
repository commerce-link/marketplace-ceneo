package pl.commercelink.marketplace.ceneo;

import java.util.Map;
import java.util.function.Supplier;

class CeneoTokenAuthClient {

    private final CeneoHttpClient httpClient;
    private final Supplier<String> tokenSupplier;
    private volatile String token;

    CeneoTokenAuthClient(CeneoHttpClient httpClient, Supplier<String> tokenSupplier) {
        this.httpClient = httpClient;
        this.tokenSupplier = tokenSupplier;
        this.token = tokenSupplier.get();
    }

    <T> T getJson(String path, Map<String, String> params, Class<T> responseType) {
        try {
            return httpClient.getJson(path, params, responseType, "Bearer " + token);
        } catch (CeneoHttpException e) {
            if (e.getStatusCode() == 401) {
                this.token = tokenSupplier.get();
                return httpClient.getJson(path, params, responseType, "Bearer " + token);
            }
            throw e;
        }
    }
}
