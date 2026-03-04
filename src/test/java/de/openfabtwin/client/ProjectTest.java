package de.openfabtwin.client;

import de.openfabtwin.domain.Project;
import de.openfabtwin.BcfApiException;
import de.openfabtwin.generated.model.ProjectPUT;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectTest extends BcfClientTestBase{

    // -------------------------------------------------------------------------
    // getProjects()
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnListOfProjects() throws Exception {
        stubResponse(200, """
            [
              {
                "project_id": "1507329",
                "name": "Project 1507329",
                "authorization": {
                  "project_actions": ["createTopic", "createDocument"]
                }
              },
              {
                "project_id": "1807388",
                "name": "Project 1807388",
                "authorization": {
                  "project_actions": ["createTopic", "createDocument"]
                }
              }
            ]
            """);

        List<Project> projects = bcfClient.getProjects();

        assertEquals(2,                 projects.size());
        assertEquals("1507329",         projects.get(0).getProjectId());
        assertEquals("Project 1507329", projects.get(0).getName());
        assertTrue(projects.get(0).getProjectActions().contains("createTopic"));
        assertTrue(projects.get(0).getProjectActions().contains("createDocument"));
        assertEquals("1807388",         projects.get(1).getProjectId());
        assertEquals("Project 1807388", projects.get(1).getName());
    }

    @Test
    void shouldReturnEmptyListWhenNoProjects() throws Exception {
        stubResponse(200, "[]");

        List<Project> projects = bcfClient.getProjects();

        assertTrue(projects.isEmpty());
    }

    @Test
    void shouldThrowWhenGetProjectsFails() throws Exception {
        stubResponse(500, "");

        BcfApiException ex = assertThrows(BcfApiException.class,
                () -> bcfClient.getProjects());

        assertEquals(500, ex.getStatusCode());
    }

    // -------------------------------------------------------------------------
    // getProject()
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnSingleProject() throws Exception {
        stubResponse(200, """
            {
              "project_id": "1507329",
              "name": "Project 1507329",
              "authorization": {
                "project_actions": ["createTopic", "createDocument"]
              }
            }
            """);

        Project project = bcfClient.getProject("1507329");

        assertEquals("1507329",         project.getProjectId());
        assertEquals("Project 1507329", project.getName());
        assertEquals(2,                 project.getProjectActions().size());
        assertTrue(project.getProjectActions().contains("createTopic"));
        assertTrue(project.getProjectActions().contains("createDocument"));
    }

    @Test
    void shouldThrowWhenProjectNotFound() throws Exception {
        stubResponse(404, "");

        BcfApiException ex = assertThrows(BcfApiException.class,
                () -> bcfClient.getProject("nonexistent"));

        assertEquals(404, ex.getStatusCode());
    }

    // -------------------------------------------------------------------------
    // updateProject()
    // -------------------------------------------------------------------------

    @Test
    void shouldUpdateAndReturnProject() throws Exception {
        stubResponse(200, """
            {
              "project_id": "1507329",
              "name": "Updated Project Name",
              "authorization": {
                "project_actions": ["update"]
              }
            }
            """);

        ProjectPUT payload = new ProjectPUT();
        payload.setName("Updated Project Name");

        Project updated = bcfClient.updateProject("1507329", payload);

        assertEquals("1507329",              updated.getProjectId());
        assertEquals("Updated Project Name", updated.getName());
        assertTrue(updated.getProjectActions().contains("update"));
    }
}
