package de.openfabtwin.client;

import de.openfabtwin.BcfException;
import de.openfabtwin.generated.api.EventsApi;
import de.openfabtwin.generated.invoker.ApiClient;
import de.openfabtwin.generated.invoker.ApiException;
import de.openfabtwin.generated.model.CommentEventGET;
import de.openfabtwin.generated.model.TopicEventGET;

import java.util.List;

public class EventClient {

    private final EventsApi api;
    private final String version;

    public EventClient(ApiClient apiClient, String version){
        this.api     = new EventsApi(apiClient);
        this.version = version;
    }

    // Comment events
    public List<CommentEventGET> getCommentEvent(String projectId, String topicId, String commentId, String $top, String $skip, String $filter, String $orderby) {
        try {
            return api.getCommentEvent(version, projectId,topicId,commentId, $top, $skip, $filter, $orderby);
        } catch (ApiException e) { throw new BcfException(e.getCode(), e.getMessage());}
    }

    public List<CommentEventGET> getCommentEvents(String projectId, String $top, String $skip, String $filter, String $orderby) {
        try {
            return api.getCommentEvents(version, projectId, $top, $skip, $filter, $orderby);
        } catch (ApiException e) { throw new BcfException(e.getCode(), e.getMessage());}
    }

    // Topic events
    public List<TopicEventGET> getEvents(String projectId, String $top, String $skip, String $filter, String $orderby) {
        try {
            return api.getEvents(version, projectId, $top, $skip, $filter, $orderby);
        } catch (ApiException e) { throw new BcfException(e.getCode(), e.getMessage());}
    }

    public List<TopicEventGET> getTopicEvents(String projectId, String topicId, String $top, String $skip, String $filter, String $orderby){
        try {
            return api.getTopicEvents(version, projectId, topicId, $top, $skip, $filter, $orderby);
        } catch (ApiException e) { throw new BcfException(e.getCode(), e.getMessage());}
    }

}
