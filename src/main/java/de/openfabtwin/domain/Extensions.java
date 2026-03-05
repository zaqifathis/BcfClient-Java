package de.openfabtwin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Extensions {
    @JsonProperty("topic_type")
    private List<String> topicType;

    @JsonProperty("topic_status")
    private List<String> topicStatus;

    @JsonProperty("topic_label")
    private List<String> topicLabel;

    @JsonProperty("snippet_type")
    private List<String> snippetType;

    @JsonProperty("priority")
    private List<String> priority;

    @JsonProperty("users")
    private List<String> users;

    @JsonProperty("stage")
    private List<String> stage;

    @JsonProperty("project_actions")
    private List<String> projectActions;

    @JsonProperty("topic_actions")
    private List<String> topicActions;

    @JsonProperty("comment_actions")
    private List<String> commentActions;
}
