package cloudflaredtos;

import org.testcontainers.shaded.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.testcontainers.shaded.com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateZoneRecordResponse {
    @JsonProperty
    private IdResult result;

    public IdResult getResult() {
        return result;
    }
}
