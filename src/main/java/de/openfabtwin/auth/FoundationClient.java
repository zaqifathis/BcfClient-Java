package de.openfabtwin.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import de.openfabtwin.BcfException;
import de.openfabtwin.generated.model.AuthGET;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FoundationClient {

    private static final String REQUIRED_FLOW = "authorization_code_grant";
    private static final String AUTH_ENDPOINT = "foundation/1.1/auth";

    // --- config ---
    @Getter private final String baseUrl;
    @Getter private final String clientId;
    private final String clientSecret;

    // --- discovered from /auth ---
    private String authUrl;
    private String tokenUrl;

    // --- token state ---
    private String accessToken;
    private String refreshToken;
    private long   accessTokenExpiresOn;
    private long   refreshTokenExpiresOn;

    private final HttpClient   httpClient;
    private final ObjectMapper objectMapper;

    // Constructor -------------------------------------------------------------------------

    public FoundationClient(String baseUrl, String clientId, String clientSecret) {
        this.baseUrl      = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.clientId     = clientId;
        this.clientSecret = clientSecret;
        this.httpClient   = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();

        // start expired — forces login on first getAccessToken()
        this.accessTokenExpiresOn  = 0;
        this.refreshTokenExpiresOn = Long.MAX_VALUE;
    }

    // PUBLIC: getAccessToken -------------------------------------------------------------------------

    public String getAccessToken() {
        long now = epochSeconds();

        if (accessToken != null && accessTokenExpiresOn > now) {
            return accessToken;
        } else if (refreshToken != null && refreshTokenExpiresOn > now) {
            refreshAccessToken();
        } else {
            login();
        }
        return accessToken;
    }

    // STEP 1: discover auth methods -------------------------------------------------------------------------

    public List<String> getAuthMethods() {
        return getAuthInfo().getSupportedOauth2Flows();
    }

    public AuthGET getAuthInfo() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + AUTH_ENDPOINT))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new BcfException(response.statusCode(),
                        "Failed to fetch auth info — HTTP " + response.statusCode());
            }

            return objectMapper.readValue(response.body(), AuthGET.class);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BcfException("Cannot reach BCF server: " + baseUrl + AUTH_ENDPOINT);
        }
    }

    // STEP 2: login -------------------------------------------------------------------------

    public void login() {
        AuthGET authInfo = getAuthInfo();
        validateAuthFlow(authInfo.getSupportedOauth2Flows());

        this.authUrl  = authInfo.getOauth2AuthUrl();
        this.tokenUrl = authInfo.getOauth2TokenUrl();

        OAuthReceiver receiver  = new OAuthReceiver();
        String state            = UUID.randomUUID().toString();
        String redirectUri      = receiver.getRedirectUri();
        String browserUrl       = buildAuthUrl(authUrl, state, redirectUri);

        openBrowser(browserUrl);

        String code;
        try {
            code = receiver.waitForCode(state);
        } catch (IOException e) {
            throw new BcfException("Failed to start OAuth receiver: " + e.getMessage());
        }

        exchangeCodeForToken(code, redirectUri);
    }

    // STEP 3: token exchange + refresh -------------------------------------------------------------------------

    private void exchangeCodeForToken(String code, String redirectUri) {
        String body = "grant_type=authorization_code"
                + "&code="         + encode(code)
                + "&redirect_uri=" + encode(redirectUri);
        postToTokenEndpoint(body);
    }

    private void refreshAccessToken() {
        String body = "grant_type=refresh_token"
                + "&refresh_token=" + encode(refreshToken);
        postToTokenEndpoint(body);
    }

    private void postToTokenEndpoint(String formBody) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", buildBasicAuthHeader())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new BcfException(response.statusCode(),
                        "Token exchange failed — HTTP " + response.statusCode());
            }

            setTokensFromResponse(response.body());

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BcfException("Token request failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void setTokensFromResponse(String responseBody) {
        try {
            Map<String, Object> json = objectMapper.readValue(responseBody, Map.class);
            long now = epochSeconds();

            this.accessToken          = (String) json.get("access_token");
            this.refreshToken         = (String) json.get("refresh_token");
            this.accessTokenExpiresOn = now + ((Number) json.get("expires_in")).longValue();

            if (json.containsKey("refresh_token_expires_in")) {
                this.refreshTokenExpiresOn = now + ((Number) json.get("refresh_token_expires_in")).longValue();
            }

        } catch (IOException e) {
            throw new BcfException("Failed to parse token response: " + e.getMessage());
        }
    }

    // Utilities -------------------------------------------------------------------------

    private void validateAuthFlow(List<String> supportedFlows) {
        if (supportedFlows == null || !supportedFlows.contains(REQUIRED_FLOW)) {
            throw new BcfException(
                    "BCF server does not support 'authorization_code_grant'. Supported: " + supportedFlows);
        }
    }

    private String buildAuthUrl(String authEndpoint, String state, String redirectUri) {
        return authEndpoint
                + (authEndpoint.contains("?") ? "&" : "?")
                + "client_id="     + encode(clientId)
                + "&response_type=code"
                + "&state="        + encode(state)
                + "&redirect_uri=" + encode(redirectUri);
    }

    private String buildBasicAuthHeader() {
        String raw     = clientId + ":" + clientSecret;
        String encoded = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    private void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(url));
            } else {
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
            }
        } catch (IOException e) {
            throw new BcfException("Cannot open browser: " + e.getMessage());
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private long epochSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}