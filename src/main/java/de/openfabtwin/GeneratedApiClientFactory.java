package de.openfabtwin;

import de.openfabtwin.auth.FoundationClient;
import de.openfabtwin.generated.invoker.ApiClient;

public class GeneratedApiClientFactory {
    private GeneratedApiClientFactory() {}

    public static ApiClient create(FoundationClient foundation, String bcfBaseUrl) {
        ApiClient client = new ApiClient();

        client.updateBaseUri(bcfBaseUrl);

        client.setRequestInterceptor(builder ->
                builder.header("Authorization", "Bearer " + foundation.getAccessToken())
        );

        return client;
    }
}
