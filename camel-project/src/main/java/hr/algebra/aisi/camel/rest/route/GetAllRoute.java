package hr.algebra.aisi.camel.rest.route;

import hr.algebra.aisi.camel.rest.config.AppConfig;
import hr.algebra.aisi.camel.rest.processor.ResponseProcessor;
import hr.algebra.aisi.camel.rest.service.AuthService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class GetAllRoute extends RouteBuilder {
    private final ResponseProcessor responseProcessor;
    private final AuthService authService;

    public GetAllRoute(ResponseProcessor responseProcessor, AuthService authService) {
        this.responseProcessor = responseProcessor;
        this.authService = authService;
    }

    @Override
    public void configure() {
        errorHandler(RouteSupport.defaultErrorHandler());

        //producer
        from("direct:get-all-" + AppConfig.ENTITY_NAME)
                .routeId("get-all-movies-proxy-producer")
                .log(">>> HTTP Proxy Received: GET all movies")
                .setExchangePattern(org.apache.camel.ExchangePattern.InOnly)
                .to(AppConfig.QUEUE_GET_ALL)
                .setBody(constant("{\"status\": \"Request queued via ActiveMQ Proxy\"}"));


        //consumer
        from(AppConfig.QUEUE_GET_ALL)
                .routeId("get-all-movies-proxy-consumer")
                .log("<<< ActiveMQ Consumer: Fetching from Backend REST API")
                .setProperty(ResponseProcessor.OP_METHOD, constant("GET"))
                .setProperty(ResponseProcessor.OP_ENDPOINT, simple(AppConfig.BASE_URL + "/getallmovies"))
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader("Authorization", method(authService, "getAdminToken").prepend("Bearer "))
                .to(AppConfig.BASE_URL + "/getallmovies?bridgeEndpoint=true")
                .process(responseProcessor)
                .to("file:%s?fileName=get-all-%s-${date:now:%s}.json"
                        .formatted(AppConfig.OUTPUT_DIR, AppConfig.ENTITY_NAME, AppConfig.TIMESTAMP_FORMAT))
                .to(AppConfig.METRIC_AVRO_SUCCESS)
                .log("<<< Proxy Flow Completed successfully");
    }
}