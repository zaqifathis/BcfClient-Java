package de.openfabtwin;

import de.openfabtwin.generated.model.AuthGET;
import de.openfabtwin.auth.FoundationClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FoundationClientTest — unit tests. No real server needed.
 *
 * Strategy:
 * - Mock HttpClient → we control what the "server" returns
 * - Inject via test constructor
 * - Test logic only: validation, parsing, token caching, error handling
 */
class FoundationClientTest {

    private HttpClient           mockHttpClient;
    private HttpResponse<String> mockResponse;
    private FoundationClient     foundation;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() {
        mockHttpClient = mock(HttpClient.class);
        mockResponse   = mock(HttpResponse.class);

        foundation = new FoundationClient(
                "http://localhost:8080/",
                "test-client-id",
                "test-client-secret",
                mockHttpClient
        );
    }

    // -------------------------------------------------------------------------
    // baseUrl normalization
    // -------------------------------------------------------------------------

    @Test
    void shouldAddTrailingSlashToBaseUrl() {
        FoundationClient client = new FoundationClient("http://localhost:8080", "id", "secret");
        assertEquals("http://localhost:8080/", client.getBaseUrl());
    }

    @Test
    void shouldNotDoubleSlashIfAlreadyPresent() {
        FoundationClient client = new FoundationClient("http://localhost:8080/", "id", "secret");
        assertEquals("http://localhost:8080/", client.getBaseUrl());
    }

    // -------------------------------------------------------------------------
    // getAuthInfo() — happy path
    // -------------------------------------------------------------------------

    @Test
    void shouldParseAuthInfoSuccessfully() throws Exception {
        stubResponse(200, """
            {
              "oauth2_auth_url": "https://keycloak.example.com/auth",
              "oauth2_token_url": "https://keycloak.example.com/token",
              "http_basic_supported": false,
              "supported_oauth2_flows": ["authorization_code_grant", "implicit_grant"]
            }
            """);

        AuthGET info = foundation.getAuthInfo();

        assertEquals("https://keycloak.example.com/auth",  info.getOauth2AuthUrl());
        assertEquals("https://keycloak.example.com/token", info.getOauth2TokenUrl());
        assertFalse(info.getHttpBasicSupported());
        assertTrue(info.getSupportedOauth2Flows().contains("authorization_code_grant"));
    }

    // -------------------------------------------------------------------------
    // getAuthInfo() — error cases
    // -------------------------------------------------------------------------

    @Test
    void shouldThrowWhenServerReturns401() throws Exception {
        stubResponse(401, "");

        BcfApiException ex = assertThrows(BcfApiException.class, () -> foundation.getAuthInfo());
        assertEquals(401, ex.getStatusCode());
    }

    @Test
    void shouldThrowWhenServerReturns500() throws Exception {
        stubResponse(500, "");

        BcfApiException ex = assertThrows(BcfApiException.class, () -> foundation.getAuthInfo());
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void shouldThrowWhenNetworkFails() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), any()))
                .thenThrow(new IOException("connection refused"));

        BcfApiException ex = assertThrows(BcfApiException.class, () -> foundation.getAuthInfo());

        assertEquals(-1, ex.getStatusCode());  // logic error, not HTTP error
        assertTrue(ex.getMessage().contains("Cannot reach BCF server"));
    }

    // -------------------------------------------------------------------------
    // getAuthMethods()
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnSupportedFlows() throws Exception {
        stubResponse(200, """
            {
              "oauth2_auth_url": "https://keycloak.example.com/auth",
              "oauth2_token_url": "https://keycloak.example.com/token",
              "http_basic_supported": false,
              "supported_oauth2_flows": ["authorization_code_grant", "implicit_grant"]
            }
            """);

        List<String> methods = foundation.getAuthMethods();

        assertEquals(2, methods.size());
        assertTrue(methods.contains("authorization_code_grant"));
    }

    // -------------------------------------------------------------------------
    // getAccessToken() — token caching
    // -------------------------------------------------------------------------

    @Test
    void shouldReturnCachedTokenWithoutNetworkCall() {
        // inject valid token directly — simulates already-logged-in state
        foundation.setTokensForTest("my-access-token", "my-refresh-token",
                9999,   // access token valid for 9999s
                99999   // refresh token valid for 99999s
        );

        String token = foundation.getAccessToken();

        assertEquals("my-access-token", token);
        verifyNoInteractions(mockHttpClient);  // no network call made
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private void stubResponse(int status, String body) throws Exception {
        when(mockResponse.statusCode()).thenReturn(status);
        when(mockResponse.body()).thenReturn(body);
        doReturn(mockResponse).when(mockHttpClient).send(any(HttpRequest.class), any());
    }
}