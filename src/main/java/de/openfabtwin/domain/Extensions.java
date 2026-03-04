package de.openfabtwin.domain;

import lombok.Data;

import java.util.List;

@Data
public class Extensions {
    private List<String> topicType;
    private List<String> topicStatus;
    private List<String> topicLabel;
    private List<String> snippetType;
    private List<String> priority;
    private List<String> users;
    private List<String> stage;
    private List<String> projectActions;
    private List<String> topicActions;
    private List<String> commentActions;
}
