package de.openfabtwin.client;

import de.openfabtwin.BcfException;
import de.openfabtwin.generated.api.RelatedTopicsApi;
import de.openfabtwin.generated.invoker.ApiClient;
import de.openfabtwin.generated.invoker.ApiException;
import de.openfabtwin.generated.model.RelatedTopicGET;
import de.openfabtwin.generated.model.RelatedTopicPUT;

import java.util.List;

public class RelatedTopicsClient {

    private final RelatedTopicsApi api;
    private final String version;

    public RelatedTopicsClient(ApiClient apiClient, String version){
        this.api = new RelatedTopicsApi(apiClient);
        this.version = version;
    }

    public List<RelatedTopicGET> getRelatedTopics(String projectId, String topicId) {
        try {
            return api.getRelatedTopics(version, projectId, topicId);
        } catch (ApiException e) {
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

    public List<RelatedTopicGET> updateRelatedTopics(String version, String projectId, String topicId, List<RelatedTopicPUT> payload) {
        try {
            return api.updateRelatedTopics(version, projectId, topicId, payload);
        } catch (ApiException e) {
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

}
