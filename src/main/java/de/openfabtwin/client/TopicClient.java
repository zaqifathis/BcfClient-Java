package de.openfabtwin.client;

import de.openfabtwin.BcfException;
import de.openfabtwin.generated.api.TopicsApi;
import de.openfabtwin.generated.invoker.ApiClient;
import de.openfabtwin.generated.invoker.ApiException;
import de.openfabtwin.generated.model.TopicGET;
import de.openfabtwin.generated.model.TopicPOST;
import de.openfabtwin.generated.model.TopicPUT;

import java.util.List;

public class TopicClient {

    private final TopicsApi api;
    private final String version;

    public TopicClient(ApiClient apiClient, String version) {
        this.api     = new TopicsApi(apiClient);
        this.version = version;
    }

    public TopicGET createTopic(String projectId, TopicPOST payload) {
        try {
            return api.createTopic(version, projectId, payload);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public void deleteTopic(String projectId, String topicId) {
        try {
            api.deleteTopic(version, projectId, topicId);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public TopicGET getTopicById(String projectId, String topicId) {
        try {
            return api.getTopicById(version, projectId, topicId);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public List<TopicGET> getTopics(String projectId, String $filter, String $orderby, String $top, String $skip) {
        try {
            return api.getTopics(version, projectId, $filter, $orderby, $top, $skip);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public TopicGET updateTopic(String projectId, String topicId, TopicPUT payload) {
        try {
            return api.updateTopic(version, projectId, topicId, payload);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

}
