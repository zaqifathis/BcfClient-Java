package de.openfabtwin.client;

import de.openfabtwin.BcfException;
import de.openfabtwin.generated.api.ProjectApi;
import de.openfabtwin.generated.invoker.ApiClient;
import de.openfabtwin.generated.invoker.ApiException;
import de.openfabtwin.generated.model.ExtensionsGET;
import de.openfabtwin.generated.model.ProjectGET;
import de.openfabtwin.generated.model.ProjectPUT;

import java.util.List;

public class ProjectClient {

    private final ProjectApi api;
    private final String version;

    public ProjectClient(ApiClient apiClient, String version) {
        this.api     = new ProjectApi(apiClient);
        this.version = version;
    }

    public List<ProjectGET> getAllProjects(){
        try {
            return api.getAllProjects(version);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public ProjectGET getProjectById(String projectId){
        try {
            return api.getProjectById(version, projectId);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public ExtensionsGET getProjectExtension(String projectId) {
        try {
            return api.getProjectExtension(version, projectId);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public ProjectGET updateProjectById(String projectId, ProjectPUT payload){
        try {
            return api.updateProjectById(version,projectId, payload);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

}
