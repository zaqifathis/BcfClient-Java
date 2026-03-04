package de.openfabtwin;

import de.openfabtwin.auth.FoundationClient;
import de.openfabtwin.generated.model.*;
import de.openfabtwin.mapper.ExtensionMapper;
import de.openfabtwin.mapper.ProjectMapper;
import de.openfabtwin.domain.Extensions;
import de.openfabtwin.domain.Project;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
        ProjectGET[] raw = http.get(url("/projects"), ProjectGET[].class);
        return ProjectMapper.toDomainList(raw);
    }

    /** GET /projects/{projectId} */
    public Project getProject(String projectId) {
        ProjectGET raw = http.get(url("/projects/" + projectId), ProjectGET.class);
        return ProjectMapper.toDomain(raw);
    }

    /** PUT /projects/{projectId} */
    public Project updateProject(String projectId, ProjectPUT payload) {
        ProjectGET raw = http.put(url("/projects/" + projectId), payload, ProjectGET.class);
        return ProjectMapper.toDomain(raw);
    }

    /** GET /projects/{projectId}/extensions */
    public Extensions getExtension(String projectId){
        ExtensionsGET raw = http.get(url("/projects/" + projectId + "/extensions"), ExtensionsGET.class);
        return ExtensionMapper.toDomain(raw);
    }

    // -------------------------------------------------------------------------
    // TOPIC
    // -------------------------------------------------------------------------



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
