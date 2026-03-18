package de.openfabtwin;

import de.openfabtwin.auth.FoundationClient;
import de.openfabtwin.client.*;
import de.openfabtwin.generated.invoker.ApiClient;
import de.openfabtwin.generated.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BcfClient {

    private static final String VERSIONS_ENDPOINT = "foundation/versions";
    private static final String TARGET_API_ID     = "bcf";

    private final FoundationClient foundation;
    private final String targetVersion;
    private final HttpClient plainHttpClient;

    public ProjectClient project;
    public TopicClient topic;
    public CommentClient comment;
    public ViewpointClient viewpoint;
    public DocumentReferencesClient documentReferences;
    public DocumentsClient documents;
    public EventClient events;
    public FilesClient files;
    public RelatedTopicsClient relatedTopics;
    public SnippetsClient snippets;

    public BcfClient(FoundationClient foundation, String targetVersion) {
        this.foundation      = foundation;
        this.targetVersion   = targetVersion;
        this.plainHttpClient = HttpClient.newHttpClient();
    }

    public void resolveVersion() {
        VersionsGET versionsGET = getVersions();

        VersionsGETVersionsInner bcf = versionsGET.getVersions().stream()
                .filter(v -> TARGET_API_ID.equals(v.getApiId())
                        && targetVersion.equals(v.getVersionId()))
                .findFirst()
                .orElseThrow(() -> new BcfException(
                        "Server does not support BCF " + targetVersion
                                + ". Available: " + versionsGET.getVersions().stream()
                                .map(v -> v.getApiId() + " " + v.getVersionId())
                                .toList()
                ));

        ApiClient apiClient = GeneratedApiClientFactory.create(foundation);
        this.project   = new ProjectClient(apiClient, targetVersion);
        this.topic     = new TopicClient(apiClient, targetVersion);
        this.comment   = new CommentClient(apiClient, targetVersion);
        this.viewpoint = new ViewpointClient(apiClient, targetVersion);
        this.documentReferences = new DocumentReferencesClient(apiClient, targetVersion);
        this.documents = new DocumentsClient(apiClient, targetVersion);
        this.events = new EventClient(apiClient, targetVersion);
        this.files = new FilesClient(apiClient, targetVersion);
        this.relatedTopics = new RelatedTopicsClient(apiClient, targetVersion);
        this.snippets = new SnippetsClient(apiClient, targetVersion);
    }

    public VersionsGET getVersions() {
        String url = foundation.getBaseUrl() + VERSIONS_ENDPOINT;
        try {
            HttpResponse<String> response = plainHttpClient.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(url)).GET()
                            .header("Accept", "application/json")
                            .build(),
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new BcfException(response.statusCode(),
                        "Failed to fetch versions — HTTP " + response.statusCode());

            return ApiClient.createDefaultObjectMapper()
                    .readValue(response.body(), VersionsGET.class);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BcfException("Failed to fetch versions: " + e.getMessage());
        }
    }

}
