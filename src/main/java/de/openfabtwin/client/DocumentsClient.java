package de.openfabtwin.client;

import de.openfabtwin.BcfException;
import de.openfabtwin.generated.api.DocumentsApi;
import de.openfabtwin.generated.invoker.ApiClient;
import de.openfabtwin.generated.invoker.ApiException;
import de.openfabtwin.generated.model.DocumentGET;
import de.openfabtwin.generated.model.DocumentReferenceGET;
import de.openfabtwin.generated.model.DocumentReferencePOST;

import java.io.File;
import java.util.List;

public class DocumentsClient {

    private final DocumentsApi api;
    private final String version;

    public DocumentsClient(ApiClient apiClient, String version){
        this.api     = new DocumentsApi(apiClient);
        this.version = version;
    }

    public DocumentGET createDocument(String projectId, String topicId, File payload) {
        try {
            return api.createDocument(version, projectId, topicId, payload);
        } catch (ApiException e) {throw new BcfException(e.getCode(),e.getMessage());}
    }

    public List<DocumentGET> getDocument(String projectId, String topicId){
        try {
            return api.getDocument(version, projectId, topicId);
        } catch (ApiException e) {throw new BcfException(e.getCode(),e.getMessage());}
    }

    public File getDocumentById(String projectId, String documentId) {
        try {
            return api.getDocumentById(version, projectId, documentId);
        } catch (ApiException e) {throw new BcfException(e.getCode(),e.getMessage());}
    }



}
