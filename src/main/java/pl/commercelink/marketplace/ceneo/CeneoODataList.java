package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class CeneoODataList<T> {

    @JsonProperty("results")
    private List<T> results;

    List<T> getResults() {
        return results == null ? List.of() : results;
    }
}
