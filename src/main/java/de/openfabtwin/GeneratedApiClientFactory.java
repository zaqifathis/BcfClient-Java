package de.openfabtwin;

import de.openfabtwin.auth.FoundationClient;
import de.openfabtwin.generated.invoker.ApiClient;

public class GeneratedApiClientFactory {
    private GeneratedApiClientFactory() {}

    public static ApiClient create(FoundationClient foundation) {
        ApiClient client = new ApiClient();

        String baseUrl = foundation.getBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        client.updateBaseUri(baseUrl);

        client.setRequestInterceptor(builder ->
                builder.header("Authorization", "Bearer " + foundation.getAccessToken())
        );

        return client;
    }
}
