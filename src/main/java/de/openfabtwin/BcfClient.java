package de.openfabtwin;

import de.openfabtwin.auth.FoundationClient;
import de.openfabtwin.domain.*;
import de.openfabtwin.generated.model.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

/**
 * Usage:
 *   FoundationClient foundation = new FoundationClient(baseUrl, clientId, clientSecret);
 *   foundation.login();
 *   BcfClient bcf = new BcfClient(foundation);
 *   bcf.resolveVersion();
 */

public class BcfClient {

    private static final String VERSIONS_ENDPOINT = "foundation/versions";
    private static final String TARGET_API_ID     = "bcf";

    private final FoundationClient foundation;
    private final String targetVersion;
    private final BcfHttpClient    http;
    private final HttpClient plainHttpClient; // no auth — for versions only

    @Getter
    @Setter
    private String bcfBaseUrl;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /** Production */
    public BcfClient(FoundationClient foundation, String targetVersion) {
        this.foundation = foundation;
        this.targetVersion = targetVersion;
        this.http = new BcfHttpClient(foundation);
        this.plainHttpClient = HttpClient.newHttpClient();
    }

    /** Test — inject BcfHttpClient */
    public BcfClient(FoundationClient foundation, String targetVersion, BcfHttpClient http) {
        this.foundation      = foundation;
        this.targetVersion   = targetVersion;
        this.http            = http;
        this.plainHttpClient = HttpClient.newHttpClient();
    }

    // -------------------------------------------------------------------------
    // Version
    // -------------------------------------------------------------------------

    public void resolveVersion() {
        VersionsGET versionsGET = getVersions();

        VersionsGETVersionsInner bcf = versionsGET.getVersions().stream()
                .filter(v -> TARGET_API_ID.equals(v.getApiId())
                        && targetVersion.equals(v.getVersionId()))
                .findFirst()
                .orElseThrow(() -> new BcfApiException(
                        "Server does not support BCF " + targetVersion
                                + ". Available: " + versionsGET.getVersions().stream()
                                .map(v -> v.getApiId() + " " + v.getVersionId())
                                .toList()
                ));

        this.bcfBaseUrl = bcf.getApiBaseUrl();
    }

    public VersionsGET getVersions() {
        String url = foundation.getBaseUrl() + VERSIONS_ENDPOINT;

        try {
            HttpResponse<String> response = plainHttpClient.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .header("Accept", "application/json")
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new BcfApiException(response.statusCode(),
                        "Failed to fetch versions — HTTP " + response.statusCode());
            }

            return http.deserialize(response.body(), VersionsGET.class);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BcfApiException("Failed to fetch versions: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // PROJECT
    // -------------------------------------------------------------------------

    /** GET /projects */
    public List<Project> getProjects() {
        Project[] raw = http.get(url("/projects"), Project[].class);
        return Arrays.asList(raw);
    }

    /** GET /projects/{projectId} */
    public Project getProject(String projectId) {
        return http.get(url("/projects/" + projectId), Project.class);
    }

    /** PUT /projects/{projectId} */
    public Project updateProject(String projectId, ProjectPUT payload) {
        return http.put(url("/projects/" + projectId), payload, Project.class);
    }

    /** GET /projects/{projectId}/extensions */
    public Extensions getExtension(String projectId) {
        return http.get(url("/projects/" + projectId + "/extensions"), Extensions.class);
    }

    // -------------------------------------------------------------------------
    // TOPIC
    // -------------------------------------------------------------------------

    /** GET /projects/{projectId}/topics - Optional: Filter */
    public List<Topic> getTopics(String projectId, Filter filter) {
        String endpoint = "/projects/" + projectId + "/topics";
        if (filter != null) endpoint += filter.toQueryString();
        Topic[] raw = http.get(url(endpoint), Topic[].class);
        return Arrays.asList(raw);
    }

    /** GET /projects/{projectId}/topics/{topicId} */
    public Topic getTopic(String projectId, String topicId) {
        return http.get(url("/projects/" + projectId + "/topics/" + topicId), Topic.class);
    }

    /** POST /projects/{projectId}/topics */
    public Topic createTopic(String projectId, TopicPOST payload) {
        return http.post(url("/projects/" + projectId + "/topics"), payload, Topic.class);
    }

    /** PUT /projects/{projectId}/topics/{topicId} */
    public Topic updateTopic(String projectId, String topicId, TopicPUT payload) {
        return http.put(url("/projects/" + projectId + "/topics/" + topicId), payload, Topic.class);
    }

    /** DELETE /projects/{projectId}/topics/{topicId} */
    public void deleteTopic(String projectId, String topicId) {
        http.delete(url("/projects/" + projectId + "/topics/" + topicId));
    }

    // -------------------------------------------------------------------------
    // COMMENT
    // -------------------------------------------------------------------------

    /** POST /projects/{projectId}/topics/{topicId}/comments */
    public Comment createComment(String projectId, String topicId, CommentPOST payload) {
        return http.post(url("/projects/" + projectId + "/topics/" + topicId + "/comments"), payload, Comment.class);
    }

    /** DELETE /projects/{projectId}/topics/{topicId}/comments/{commentId} */
    public void deleteComment(String projectId, String topicId, String commentId) {
        http.delete(url("/projects/" + projectId + "/topics/" + topicId + "/comments/" + commentId));
    }

    /** GET /projects/{projectId}/topics/{topicId}/comments/{commentId} */
    public Comment getComment(String projectId, String topicId, String commentId) {
        return http.get(url("/projects/" + projectId + "/topics/" + topicId + "/comments/" + commentId), Comment.class);
    }

    /** GET /projects/{projectId}/topics/{topicId}/comments - Optional: Filter */
    public List<Comment> getComments(String projectId, String topicId, Filter filter) {
        String endpoint = "/projects/" + projectId + "/topics/" + topicId + "/comments";
        if (filter != null) endpoint += filter.toQueryString();
        Comment[] raw = http.get(url(endpoint), Comment[].class);
        return Arrays.asList(raw);
    }

    /** PUT /projects/{projectId}/topics/{topicId}/comments/{commentId}  */
    public Comment updateComment(String projectId, String topicId, String commentId, CommentPUT payload) {
        return http.put(url("/projects/" + projectId + "/topics/" + topicId + "/comments/" + commentId), payload, Comment.class);
    }

    // -------------------------------------------------------------------------
    // URL builder
    // -------------------------------------------------------------------------
    private String url(String endpoint) {
        if (bcfBaseUrl == null) {
            throw new BcfApiException("Call resolveVersion() before making BCF API calls.");
        }
        return bcfBaseUrl + endpoint;
    }

}
