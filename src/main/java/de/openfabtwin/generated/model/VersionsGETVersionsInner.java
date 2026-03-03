package de.openfabtwin.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VersionsGETVersionsInner {
    @JsonProperty("api_id")
    private String apiId;

    @JsonProperty("version_id")
    private String versionId;

    @JsonProperty("detailed_version")
    private String detailedVersion;

    @JsonProperty("api_base_url")
    private String apiBaseUrl;
}