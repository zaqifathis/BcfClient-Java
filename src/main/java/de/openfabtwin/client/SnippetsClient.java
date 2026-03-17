package de.openfabtwin.client;

import de.openfabtwin.BcfException;
import de.openfabtwin.generated.api.SnippetsApi;
import de.openfabtwin.generated.invoker.ApiClient;
import de.openfabtwin.generated.invoker.ApiException;

import java.io.File;

public class SnippetsClient {

    private final SnippetsApi api;
    private final String version;

    public SnippetsClient(ApiClient apiClient, String version){
        this.api = new SnippetsApi(apiClient);
        this.version = version;
    }

    public File getTopicSnippet(String projectId, String topicId) {
        try {
            return api.getTopicSnippet(version, projectId, topicId);
        } catch (ApiException e){
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

    public void updateTopicSnippet(String projectId, String topicId, File payload) {
        try {
            api.updateTopicSnippet(version, projectId, topicId, payload);
        } catch (ApiException e){
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

}
