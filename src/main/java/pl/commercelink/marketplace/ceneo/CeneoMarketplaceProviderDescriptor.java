package pl.commercelink.marketplace.ceneo;

import pl.commercelink.marketplace.api.MarketplaceProvider;
import pl.commercelink.marketplace.api.MarketplaceProviderDescriptor;
import pl.commercelink.provider.api.ProviderField;

import java.util.List;
import java.util.Map;

public class CeneoMarketplaceProviderDescriptor implements MarketplaceProviderDescriptor {

    @Override
    public String name() {
        return "Ceneo";
    }

    @Override
    public String displayName() {
        return "Ceneo";
    }

    @Override
    public List<ProviderField> configurationFields() {
        return List.of(
                new ProviderField("apiKey", "API Key", ProviderField.FieldType.PASSWORD, true, "******")
        );
    }

    @Override
    public MarketplaceProvider<?> create(Map<String, String> configuration) {
        String apiKey = configuration.get("apiKey");
        String apiUrl = metadata().get("apiUrl");

        CeneoHttpClient httpClient = new CeneoHttpClient(apiUrl);
        CeneoAuth auth = new CeneoAuth(apiKey, apiUrl);
        CeneoTokenAuthClient tokenAuthClient = new CeneoTokenAuthClient(httpClient, auth::fetchAccessToken);

        return new CeneoMarketplaceProvider(tokenAuthClient);
    }

    @Override
    public Map<String, String> metadata() {
        return Map.of(
                "apiUrl", "https://developers.ceneo.pl"
        );
    }
}
