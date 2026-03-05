package de.openfabtwin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

    @JsonProperty("guid")
    private String guid;

    @JsonProperty("date")
    private String date;

    @JsonProperty("author")
    private String author;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("topic_guid")
    private String topicGuid;

    @JsonProperty("viewpoint_guid")
    private String viewpointGuid;

    @JsonProperty("reply_to_comment_guid")
    private String replyToCommentGuid;

    @JsonProperty("modified_date")
    private String modifiedDate;

    @JsonProperty("modified_author")
    private String modifiedAuthor;

    @JsonProperty("authorization")
    private Authorization authorization;

    public List<String> getCommentActions() {
        return authorization != null ? authorization.getCommentActions() : null;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Authorization {
        @JsonProperty("comment_actions")
        private List<String> commentActions;
    }
}
