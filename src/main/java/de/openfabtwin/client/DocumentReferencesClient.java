package de.openfabtwin.client;

import de.openfabtwin.BcfException;
import de.openfabtwin.generated.api.DocumentReferencesApi;
import de.openfabtwin.generated.invoker.ApiClient;
import de.openfabtwin.generated.invoker.ApiException;
import de.openfabtwin.generated.model.DocumentReferenceGET;
import de.openfabtwin.generated.model.DocumentReferencePOST;
import de.openfabtwin.generated.model.DocumentReferencePUT;

import java.util.List;

public class DocumentReferencesClient {

    private final DocumentReferencesApi api;
    private final String version;

    public DocumentReferencesClient(ApiClient apiClient, String version){
        this.api     = new DocumentReferencesApi(apiClient);
        this.version = version;
    }

    public DocumentReferenceGET createDocumentReference(String projectId, String topicId, DocumentReferencePOST payload) {
        try {
            return api.createDocumentReference(version, projectId, topicId, payload);
        } catch (ApiException e) {throw new BcfException(e.getCode(),e.getMessage());}
    }

    public List<DocumentReferenceGET> getDocumentReferences(String projectId, String topicId){
        try {
            return api.getDocumentReferences(version, projectId, topicId);
        } catch (ApiException e) {throw new BcfException(e.getCode(),e.getMessage());}
    }

    public DocumentReferenceGET updateDocumentReference(String projectId, String topicId, String documentReferenceId, DocumentReferencePUT payload) {
        try {
            return api.updateDocumentReference(version, projectId, topicId, documentReferenceId, payload);
        } catch (ApiException e) {throw new BcfException(e.getCode(),e.getMessage());}
    }



}
