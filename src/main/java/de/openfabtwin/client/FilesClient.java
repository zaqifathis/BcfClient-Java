package de.openfabtwin.client;

import de.openfabtwin.BcfException;
import de.openfabtwin.generated.api.FilesApi;
import de.openfabtwin.generated.invoker.ApiClient;
import de.openfabtwin.generated.invoker.ApiException;
import de.openfabtwin.generated.model.FileGET;
import de.openfabtwin.generated.model.FilePUT;
import de.openfabtwin.generated.model.ProjectFileInformation;

import java.util.List;

public class FilesClient {

    private final FilesApi api;
    private final String version;

    public FilesClient(ApiClient apiClient, String version){
        this.api     = new FilesApi(apiClient);
        this.version = version;
    }

    public List<FileGET> getFiles(String projectId, String topicId) {
        try {
            return api.getFiles(version, projectId, topicId);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public List<ProjectFileInformation> getProjectFilesInformation(String projectId) {
        try {
            return api.getProjectFilesInformation(version, projectId);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public List<FileGET> updateTopicFile(String projectId, String topicId, List<FilePUT> payload) {
        try {
            return api.updateTopicFile(version, projectId, topicId, payload);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

}
