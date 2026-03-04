package de.openfabtwin.mapper;

import de.openfabtwin.generated.model.ProjectGET;
import de.openfabtwin.domain.Project;

import java.util.Arrays;
import java.util.List;

import static de.openfabtwin.mapper.MapperUtils.enumToStrings;

public class ProjectMapper {

    private ProjectMapper(){}

    public static Project toDomain(ProjectGET raw) {
        if (raw == null) return null;

        Project project = new Project();
        project.setProjectId(raw.getProjectId());
        project.setName(raw.getName());
        if (raw.getAuthorization() != null && raw.getAuthorization().getProjectActions() != null) {
            List<String> actions = enumToStrings(raw.getAuthorization().getProjectActions());
            project.setProjectActions(actions);
        }
        return project;
    }

    public static List<Project> toDomainList(ProjectGET[] raw) {
        if (raw == null) return List.of();
        return Arrays.stream(raw)
                .map(ProjectMapper::toDomain)
                .toList();
    }
}
