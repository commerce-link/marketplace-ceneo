package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class CeneoODataResponse<T> {

    @JsonProperty("d")
    private ODataBody<T> body;

    public List<T> getResults() {
        return body == null ? List.of() : body.results;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ODataBody<T> {

        @JsonProperty("results")
        public List<T> results;
    }
}
