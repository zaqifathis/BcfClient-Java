package de.openfabtwin.client;

import de.openfabtwin.BcfException;
import de.openfabtwin.generated.api.ViewpointsApi;
import de.openfabtwin.generated.invoker.ApiClient;
import de.openfabtwin.generated.invoker.ApiException;
import de.openfabtwin.generated.model.*;

import java.io.File;
import java.util.List;

public class ViewpointClient {

    private final ViewpointsApi api;
    private final String version;

    public ViewpointClient(ApiClient apiClient, String version) {
        this.api     = new ViewpointsApi(apiClient);
        this.version = version;
    }

    public ViewpointGET createViewpoints(String projectId, String topicId, ViewpointPOST payload) {
        try {
            return api.createViewpoints(version, projectId, topicId, payload);
        } catch (ApiException e) {
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

    public void deleteViewpointById(String projectId, String topicId, String viewpointId) {
        try {
            api.deleteViewpointById(version, projectId, topicId, viewpointId);
        } catch (ApiException e) {
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

    public File getBitmap(String projectId, String topicId, String viewpointId, String bitmapId) {
        try {
            return api.getBitmap(version, projectId, topicId, viewpointId, bitmapId);
        } catch (ApiException e) {
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

    public ColoringGET getColoring(String projectId, String topicId, String viewpointId){
        try {
            return api.getColoring(version, projectId, topicId, viewpointId);
        } catch (ApiException e) {
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

    public SelectionGET getSelection(String projectId, String topicId, String viewpointId) {
        try {
            return api.getSelection(version, projectId, topicId, viewpointId);
        } catch (ApiException e) {
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

    public File getSnapshot(String projectId, String topicId, String viewpointId) {
        try {
            return api.getSnapshot(version, projectId, topicId, viewpointId);
        } catch (ApiException e) {
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

    public ViewpointGET getViewpointById(String projectId, String topicId, String viewpointId) {
        try {
            return api.getViewpointById(version, projectId, topicId, viewpointId);
        } catch (ApiException e) {
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

    public List<ViewpointGET> getViewpoints(String projectId, String topicId) {
        try {
            return api.getViewpoints(version, projectId, topicId);
        } catch (ApiException e) {
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

    public VisibilityGET getVisibility(String projectId, String topicId, String viewpointId) {
        try {
            return api.getVisibility(version, projectId, topicId, viewpointId);
        } catch (ApiException e) {
            throw new BcfException(e.getCode(), e.getMessage());
        }
    }

}
