package de.openfabtwin.domain;

import lombok.Data;

import java.util.List;

@Data
public class Project {
    private String projectId;
    private String name;
    private List<String> projectActions;
}
