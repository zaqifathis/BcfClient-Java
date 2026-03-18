package de.openfabtwin.client;

import de.openfabtwin.BcfException;
import de.openfabtwin.generated.api.CommentsApi;
import de.openfabtwin.generated.invoker.ApiClient;
import de.openfabtwin.generated.invoker.ApiException;
import de.openfabtwin.generated.model.CommentGET;
import de.openfabtwin.generated.model.CommentPOST;
import de.openfabtwin.generated.model.CommentPUT;

import java.util.List;

public class CommentClient {

    private final CommentsApi api;
    private final String      version;

    public CommentClient(ApiClient apiClient, String version) {
        this.api     = new CommentsApi(apiClient);
        this.version = version;
    }

    public CommentGET createComment(String projectId, String topicId, CommentPOST payload) {
        try {
            return api.createComment(version, projectId, topicId, payload);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public void deleteComment(String projectId, String topicId, String commentId) {
        try {
            api.deleteComment(version, projectId, topicId, commentId);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public CommentGET getCommentById(String projectId, String topicId, String commentId) {
        try {
            return api.getCommentById(version, projectId, topicId, commentId);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public List<CommentGET> getTopicComment(String projectId, String topicId, ODataQuery query) {
        try {
            return api.getTopicComment(version, projectId, topicId, query.$filter, query.$orderby);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

    public CommentGET updateComment(String projectId, String topicId, String commentId, CommentPUT payload) {
        try {
            return api.updateComment(version, projectId, topicId, commentId, payload);
        } catch (ApiException e) {throw new BcfException(e.getCode(), e.getMessage());}
    }

}
