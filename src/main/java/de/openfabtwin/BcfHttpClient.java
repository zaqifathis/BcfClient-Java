package de.openfabtwin;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.openfabtwin.auth.FoundationClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Responsibilities:
 * - GET / POST / PUT / DELETE
 * - Auto attach Bearer token via FoundationClient (always fresh)
 * - Deserialize JSON response into requested type
 * - Centralized error handling
 *
 * BcfClient uses this — never deals with raw HTTP itself.
 */

public class BcfHttpClient {
    private final FoundationClient foundation;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /** Production */
    public BcfHttpClient(FoundationClient foundation) {
        this(foundation, HttpClient.newHttpClient());
    }

    /** Test — inject HttpClient */
    public BcfHttpClient(FoundationClient foundation, HttpClient httpClient) {
        this.foundation   = foundation;
        this.httpClient   = httpClient;
        this.objectMapper = new ObjectMapper();
    }

    // -------------------------------------------------------------------------
    // Public HTTP methods
    // -------------------------------------------------------------------------

    public <T> T get(String url, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", bearerToken())
                .build();

        return send(request, url, responseType);
    }

    public <T> T post(String url, Object body, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(toJson(body))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", bearerToken())
                .build();

        return send(request, url, responseType);
    }

    public <T> T put(String url, Object body, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(toJson(body))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", bearerToken())
                .build();

        return send(request, url, responseType);
    }

    public void delete(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .header("Authorization", bearerToken())
                .build();

        send(request, url, Void.class);
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private <T> T send(HttpRequest request, String url, Class<T> responseType) {
        try {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204 || responseType == Void.class) {
                return null; // no content
            }

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BcfApiException(response.statusCode(),
                        "HTTP " + response.statusCode() + " for: " + url);
            }

            return objectMapper.readValue(response.body(), responseType);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BcfApiException("Request failed for: " + url + " — " + e.getMessage());
        }
    }

    /**
     * getAccessToken() handles expiry + refresh automatically.
     * Token is always fresh — no manual sync needed.
     */
    private String bearerToken() {
        return "Bearer " + foundation.getAccessToken();
    }

    private HttpRequest.BodyPublisher toJson(Object body) {
        try {
            return HttpRequest.BodyPublishers.ofString(
                    objectMapper.writeValueAsString(body));
        } catch (IOException e) {
            throw new BcfApiException("Failed to serialize request body: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // exposed for BcfClient to reuse Jackson deserialization
    // -------------------------------------------------------------------------
    public <T> T deserialize(String body, Class<T> type) {
        try {
            return objectMapper.readValue(body, type);
        } catch (IOException e) {
            throw new BcfApiException("Failed to deserialize response: " + e.getMessage());
        }
    }
}
