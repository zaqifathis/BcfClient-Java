package de.openfabtwin.auth;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * OAuthReceiver — temporary local HTTP server on localhost:8081
 * Purpose:
 * - Opens for ONE request only
 * - Extracts auth code + state
 * - Shuts itself down immediately after
 *
 */

public class OAuthReceiver {

    private static final int PORT = 8081;
    private static final int TIMEOUT_SECONDS = 120;

    private HttpServer server;
    private String authCode;
    private String authState;

    // blocks until code arrives or timeout
    private final CountDownLatch latch = new CountDownLatch(1);

    public String waitForCode(String expectedState) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new CallbackHandler());
        server.start();

        try {
            boolean received = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!received) {
                throw new IllegalStateException("Timed out waiting for OAuth callback after " + TIMEOUT_SECONDS + "s");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for OAuth callback");
        } finally {
            server.stop(0); // shut down immediately after
        }

        if (!expectedState.equals(authState)) {
            throw new IllegalStateException("OAuth state mismatch — possible CSRF attack");
        }

        return authCode;
    }

    public String getRedirectUri() {
        return "http://localhost:" + PORT;
    }

    // -------------------------------------------------------------------------
    // Inner handler — processes the one incoming redirect request
    // -------------------------------------------------------------------------

    private class CallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI requestUri = exchange.getRequestURI();
            Map<String, String> params = parseQueryParams(requestUri.getQuery());

            authCode  = params.getOrDefault("code", "");
            authState = params.getOrDefault("state", "");

            // respond to browser
            String responseBody = "You have now authenticated :) You may now close this browser window.";
            exchange.sendResponseHeaders(200, responseBody.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBody.getBytes());
            }

            latch.countDown(); // unblock waitForCode()
        }
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;

        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2) {
                params.put(parts[0], parts[1]);
            }
        }
        return params;
    }
}