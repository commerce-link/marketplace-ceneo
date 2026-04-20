package pl.commercelink.marketplace.ceneo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

class CeneoAuth {

    private static final String TOKEN_PATH = "/AuthorizationService.svc/GetToken";
    private static final String TOKEN_HEADER = "access_token";

    private final String apiKey;
    private final String baseUrl;
    private final HttpClient httpClient;

    CeneoAuth(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    String fetchAccessToken() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + TOKEN_PATH))
                .header("Authorization", "Basic " + apiKey)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() / 100 != 2) {
                throw new CeneoHttpException(response.statusCode(), response.body());
            }
            return response.headers().firstValue(TOKEN_HEADER)
                    .orElseThrow(() -> new IllegalStateException(
                            "Ceneo GetToken response missing '" + TOKEN_HEADER + "' header"));
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Ceneo GetToken call failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Ceneo GetToken call interrupted", e);
        }
    }
}
