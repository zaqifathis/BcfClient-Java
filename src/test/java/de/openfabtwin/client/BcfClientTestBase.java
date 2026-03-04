package de.openfabtwin.client;

import de.openfabtwin.BcfClient;
import de.openfabtwin.auth.FoundationClient;
import de.openfabtwin.BcfHttpClient;
import org.junit.jupiter.api.BeforeEach;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * BcfClientTestBase — shared setup for all BcfClient tests.
 *
 * Subclasses inherit:
 * - mockHttpClient   → stub HTTP responses
 * - mockFoundation   → stub token + baseUrl
 * - bcfClient        → the class under test
 * - stubResponse()   → helper to mock any HTTP response
 */

public class BcfClientTestBase {

    protected HttpClient           mockHttpClient;
    protected HttpResponse<String> mockResponse;
    protected FoundationClient     mockFoundation;
    protected BcfClient            bcfClient;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() {
        mockHttpClient = mock(HttpClient.class);
        mockResponse   = mock(HttpResponse.class);
        mockFoundation = mock(FoundationClient.class);

        when(mockFoundation.getBaseUrl()).thenReturn("https://bcf.server/");
        when(mockFoundation.getAccessToken()).thenReturn("fake-token");

        BcfHttpClient bcfHttpClient = new BcfHttpClient(mockFoundation, mockHttpClient);
        bcfClient = new BcfClient(mockFoundation, "3.0", bcfHttpClient);

        // skip resolveVersion() — set directly
        bcfClient.setBcfBaseUrl("https://bcf.server/bcf/3.0");
    }

    /**
     * Stubs the mock HTTP client to return the given status + body.
     * Call this at the start of each test.
     */
    @SuppressWarnings("unchecked")
    protected void stubResponse(int status, String body) throws Exception {
        when(mockResponse.statusCode()).thenReturn(status);
        when(mockResponse.body()).thenReturn(body);
        doReturn(mockResponse).when(mockHttpClient).send(any(HttpRequest.class), any());
    }
}
