package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

class CeneoHttpClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper jsonMapper;
    private final String baseUrl;

    CeneoHttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.jsonMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    <T> T getJson(String path, Map<String, String> params, Class<T> responseType, String authorizationHeader) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(buildUrl(path, params)))
                .header("Accept", "application/json")
                .header("Authorization", authorizationHeader)
                .GET()
                .build();

        HttpResponse<String> response = send(request);

        if (response.statusCode() / 100 != 2) {
            throw new CeneoHttpException(response.statusCode(), response.body());
        }

        if (responseType == Void.class) {
            return null;
        }
        try {
            return jsonMapper.readValue(response.body(), responseType);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse Ceneo response: " + e.getMessage(), e);
        }
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Ceneo HTTP call failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Ceneo HTTP call interrupted", e);
        }
    }

    private String buildUrl(String path, Map<String, String> params) {
        String url = baseUrl + path;
        if (params == null || params.isEmpty()) {
            return url;
        }
        String query = params.entrySet().stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));
        return url + (url.contains("?") ? "&" : "?") + query;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
