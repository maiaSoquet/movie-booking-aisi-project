package hr.algebra.aisi.camel.rest.route;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hr.algebra.aisi.camel.rest.config.AppConfig;
import hr.algebra.aisi.camel.rest.processor.ResponseProcessor;
import hr.algebra.aisi.camel.rest.service.AuthService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DeleteRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteRoute.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final AuthService authService;

    public DeleteRoute(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void configure() {

        onException(Exception.class)
                .handled(true)
                .log("ERROR in delete-" + AppConfig.ENTITY_NAME + ": ${exception.message}")
                .setBody(simple("""
                        {"error": "${exception.message}", "routeId": "delete-%s"}"""
                        .formatted(AppConfig.ENTITY_NAME)))
                .to("file:%s?fileName=delete-%s-error-${date:now:%s}.json"
                        .formatted(AppConfig.OUTPUT_DIR,
                                AppConfig.ENTITY_NAME,
                                AppConfig.TIMESTAMP_FORMAT));
        from("direct:delete-" + AppConfig.ENTITY_NAME)
                .routeId("delete-" + AppConfig.ENTITY_NAME)
                .log(">>> Triggered: delete-" + AppConfig.ENTITY_NAME + " (id=${header.targetId})")

                .setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
                .setHeader(Exchange.HTTP_PATH, simple("/deletemovie/${header.targetId}"))
                .setHeader("Authorization", method(authService, "getAdminToken").prepend("Bearer "))

                .to(AppConfig.BASE_URL + "?bridgeEndpoint=true")

                .process(exchange -> {
                    Long deletedId = exchange.getIn().getHeader("targetId", Long.class);
                    Integer status = exchange.getIn()
                            .getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);

                    LOG.info("    DELETE backend status: {}", status);

                    ObjectNode summary = MAPPER.createObjectNode();
                    summary.put("capturedAt", LocalDateTime.now()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    summary.put("routeId", "delete-" + AppConfig.ENTITY_NAME);
                    summary.put("httpMethod", "DELETE");
                    summary.put("endpoint", AppConfig.BASE_URL + "/" + deletedId);
                    summary.put("httpStatus", status != null ? status : 0);
                    summary.put("deletedId", deletedId);
                    summary.put("note", "DELETE returns 204 No Content — no response body from backend");

                    exchange.getIn().setBody(
                            MAPPER.writerWithDefaultPrettyPrinter()
                                    .writeValueAsString(summary));
                })

                .to("file:%s?fileName=delete-%s-id${header.targetId}-${date:now:%s}.json"
                        .formatted(AppConfig.OUTPUT_DIR,
                                AppConfig.ENTITY_NAME,
                                AppConfig.TIMESTAMP_FORMAT))

                .log("<<< Completed: delete-" + AppConfig.ENTITY_NAME + " (id=${header.targetId})");
    }
}
