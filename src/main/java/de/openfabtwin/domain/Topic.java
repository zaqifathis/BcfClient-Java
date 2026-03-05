package de.openfabtwin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Topic {

    @JsonProperty("guid")
    private String guid;

    @JsonProperty("server_assigend_id")
    private String serverAssignedId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("topic_type")
    private String topicType;

    @JsonProperty("topic_status")
    private String topicStatus;

    @JsonProperty("priority")
    private String priority;

    @JsonProperty("index")
    private Integer index;

    @JsonProperty("labels")
    private List<String> labels;

    @JsonProperty("creation_date")
    private String creationDate;

    @JsonProperty("creation_author")
    private String creationAuthor;

    @JsonProperty("modified_date")
    private String modifiedDate;

    @JsonProperty("modified_author")
    private String modifiedAuthor;

    @JsonProperty("assigned_to")
    private String assignedTo;

    @JsonProperty("stage")
    private String stage;

    @JsonProperty("description")
    private String description;

    @JsonProperty("due_date")
    private String dueDate;

    @JsonProperty("reference_links")
    private List<String> referenceLinks;

    // flattened from authorization.topic_actions
    @JsonProperty("authorization")
    private Authorization authorization;

    public List<String> getTopicActions() {
        return authorization != null ? authorization.getTopicActions() : null;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Authorization {
        @JsonProperty("topic_actions")
        private List<String> topicActions;
    }
}