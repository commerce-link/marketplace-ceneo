package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.databind.JavaType;

import java.util.Map;
import java.util.function.Function;
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
        return withTokenRefresh(authHeader -> httpClient.getJson(path, params, responseType, authHeader));
    }

    <T> T getJson(String path, Map<String, String> params, JavaType responseType) {
        return withTokenRefresh(authHeader -> httpClient.getJson(path, params, responseType, authHeader));
    }

    JavaType parametricType(Class<?> parametrized, Class<?>... typeArguments) {
        return httpClient.parametricType(parametrized, typeArguments);
    }

    private <T> T withTokenRefresh(Function<String, T> call) {
        try {
            return call.apply("Bearer " + token);
        } catch (CeneoHttpException e) {
            if (e.getStatusCode() == 401) {
                this.token = tokenSupplier.get();
                return call.apply("Bearer " + token);
            }
            throw e;
        }
    }
}
