package hr.algebra.aisi.camel.rest.route;

import hr.algebra.aisi.camel.rest.config.AppConfig;
import hr.algebra.aisi.camel.rest.config.CryptoConfig;
import hr.algebra.aisi.camel.rest.processor.ResponseProcessor;
import hr.algebra.aisi.camel.rest.service.AuthService;
import hr.algebra.project.s0273.proto.BookingEvent;
import hr.algebra.project.s0273.proto.BookingStatus;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.apache.camel.dataformat.protobuf.ProtobufDataFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class UpdateRoute extends RouteBuilder {
    private final ResponseProcessor responseProcessor;
    private final AuthService authService;
    private final CryptoDataFormat aesFormat;

    public UpdateRoute(
            ResponseProcessor responseProcessor,
            AuthService authService,
            @Qualifier(CryptoConfig.AES_FORMAT) final CryptoDataFormat aesFormat) {
        this.responseProcessor = responseProcessor;
        this.authService = authService;
        this.aesFormat = aesFormat;
    }

    @Override
    public void configure() {
        errorHandler(RouteSupport.defaultErrorHandler());

        //producer
        from("direct:update-" + AppConfig.ENTITY_NAME)
                .routeId("update-movie-proxy-producer")
                .log(">>> HTTP Proxy Received: PUT movie")
                .unmarshal().json()
                .process(exchange -> {
                    Map<String, Object> movie = exchange.getIn().getBody(Map.class);
                    String id = String.valueOf(exchange.getIn().getHeader("targetId"));

                    BookingEvent updateEvent = BookingEvent.newBuilder()
                            .setBookingId("bk-update-0273")
                            .setStatus(BookingStatus.CONFIRMED)
                            .setMovieId(Integer.parseInt(id))
                            .setMovieTitle((String) movie.get("name"))
                            .setDescription((String) movie.get("description"))
                            .setGenre((String) movie.get("genre"))
                            .setDuration(movie.get("duration") != null ? (Integer) movie.get("duration") : 0)
                            .setReleaseDate((String) movie.get("releaseDate"))
                            .setLanguage((String) movie.get("language"))
                            .build();

                    exchange.getIn().setBody(updateEvent);
                })
                .marshal(new ProtobufDataFormat(BookingEvent.getDefaultInstance()))
                .marshal(aesFormat)
                .setExchangePattern(ExchangePattern.InOnly)
                .to(AppConfig.QUEUE_UPDATE)
                .setBody(constant("{\"status\": \"Movie update request proxied via ActiveMQ\"}"));

        //consumer
        from(AppConfig.QUEUE_UPDATE)
                .routeId("update-movie-proxy-consumer")
                .log("<<< ActiveMQ Consumer: Processing encrypted Protobuf payload for update")
                .unmarshal(aesFormat)
                .unmarshal(new ProtobufDataFormat(BookingEvent.getDefaultInstance()))
                .process(exchange -> {
                    BookingEvent event = exchange.getIn().getBody(BookingEvent.class);
                    String jsonBackend = String.format(
                            "{\"name\":\"%s\", \"description\":\"%s\", \"genre\":\"%s\", \"duration\":%d, \"releaseDate\":\"%s\", \"language\":\"%s\"}",
                            event.getMovieTitle(),
                            event.getDescription(),
                            event.getGenre(),
                            event.getDuration(),
                            event.getReleaseDate(),
                            event.getLanguage()
                    );

                    exchange.getIn().setHeader("targetId", String.valueOf(event.getMovieId()));
                    exchange.getIn().setBody(jsonBackend);
                })
                .setProperty(ResponseProcessor.OP_METHOD, constant("PUT"))
                .setProperty(ResponseProcessor.OP_ENDPOINT, simple(AppConfig.BASE_URL))
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .setHeader("Authorization", method(authService, "getAdminToken").prepend("Bearer "))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(AppConfig.BASE_URL + "?bridgeEndpoint=true") // Appelle l'URL avec l'ID via le bridge
                .process(responseProcessor)
                .to("file:%s?fileName=update-%s-${date:now:%s}.json"
                        .formatted(AppConfig.OUTPUT_DIR, AppConfig.ENTITY_NAME, AppConfig.TIMESTAMP_FORMAT))
                .to(AppConfig.METRIC_PROTO_UPDATE_SUCCESS);
    }
}