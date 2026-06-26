package hr.algebra.aisi.camel.rest.route;

import hr.algebra.aisi.camel.rest.config.AppConfig;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Defines the in-process Dead Letter pipe used by every other route's
 * {@link RouteSupport#defaultErrorHandler() default error handler}. After
 * exponential-backoff retries are exhausted, the failed exchange lands here
 * and is forwarded to the ActiveMQ DLQ queue, tagged with the originating route.
 */
@Component
public class DeadLetterRoute extends RouteBuilder {

    /** Camel id of the DLQ-pipe route. */
    public static final String DLQ_ROUTE_ID = "global-dead-letter-pipe";

    @Override
    public void configure() {
        from(RouteSupport.DLQ_ENDPOINT_URI)
                .routeId(DLQ_ROUTE_ID)
                .log(LoggingLevel.ERROR,
                        "Sending failed exchange to ActiveMQ DLQ: "
                                + "cause=${exception.message}, body=${body}")
                .setHeader("source", simple("${routeId}"))
                .to(AppConfig.QUEUE_DLQ);
    }
}