package de.openfabtwin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("authorization")
    private Authorization authorization;

    public List<String> getProjectActions() {
        return authorization != null ? authorization.getProjectActions() : null;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Authorization {
        @JsonProperty("project_actions")
        private List<String> projectActions;
    }
}
