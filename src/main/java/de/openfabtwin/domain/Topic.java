package de.openfabtwin.domain;

import lombok.Data;

import java.util.List;

@Data
public class Topic {
    private String guid;
    private String title;
    private String creationDate;
    private String creationAuthor;

    private String serverAssignedId;
    private String topicType;
    private String topicStatus;
    private String priority;
    private String assignedTo;
    private String stage;
    private String description;
    private String modifiedDate;
    private String modifiedAuthor;
    private String dueDate;
    private Integer index;
    private List<String> labels;
    private List<String> referenceLinks;

    private List<String> topicActions;
}
