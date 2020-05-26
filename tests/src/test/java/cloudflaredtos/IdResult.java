package cloudflaredtos;

import org.testcontainers.shaded.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.testcontainers.shaded.com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdResult {
    @JsonProperty
    private String id;

    public String getId() {
        return id;
    }
}
