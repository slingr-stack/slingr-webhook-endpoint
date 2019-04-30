package io.slingr.endpoints.slingrWebhook;

import io.slingr.endpoints.Endpoint;
import io.slingr.endpoints.framework.annotations.ApplicationLogger;
import io.slingr.endpoints.framework.annotations.EndpointFunction;
import io.slingr.endpoints.framework.annotations.SlingrEndpoint;
import io.slingr.endpoints.services.concurrency.ConcurrencyService;
import io.slingr.endpoints.services.AppLogs;
import io.slingr.endpoints.services.rest.RestClient;
import io.slingr.endpoints.utils.Json;
import io.slingr.endpoints.ws.exchange.FunctionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>SLINGR webhook endpoint
 *
 * <p>Created by egonzalez on 02/26/18.
 */
@SlingrEndpoint(name = "slingr-webhook")
public class SlingrWebhookEndpoint extends Endpoint {
    private static final Logger logger = LoggerFactory.getLogger(SlingrWebhookEndpoint.class);

    @ApplicationLogger
    private AppLogs appLogger;

    private ConcurrencyService concurrencyService;

    @Override
    public void endpointStarted() {
        concurrencyService = new ConcurrencyService();
        concurrencyService.start();
    }

    @Override
    public void endpointStopped(String cause) {
        concurrencyService.stop();
    }

    @EndpointFunction
    public Json publishEvent(FunctionRequest request) {
        final Json data = request.getJsonParams();
        appLogger.info(String.format("Request to dispatch event received"), data);
        Json webhook = data.json("webhook");
        data.remove("webhook");
        concurrencyService.queueTask(() -> {
            try {
                RestClient.builder(webhook.string("url"))
                        .header("token", webhook.string("verificationToken"))
                        .put(data);
                appLogger.info(String.format("Event [%s] sent successfully to [%s]", data.json("event"), webhook.string("url")));
            } catch (Exception e) {
                appLogger.error(String.format("There was an error trying to send webhook to [%s]", webhook.string("url")), e);
            }
        });
        return data;
    }
}
