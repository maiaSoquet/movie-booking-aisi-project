package hr.algebra.aisi.camel.rest.route;

import hr.algebra.aisi.camel.rest.config.AppConfig;
import hr.algebra.aisi.camel.rest.config.CryptoConfig;
import hr.algebra.aisi.camel.rest.processor.ResponseProcessor;
import hr.algebra.aisi.camel.rest.service.AuthService;
import hr.algebra.project.s0273.proto.BookingEvent;
import hr.algebra.project.s0273.proto.BookingStatus;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.spring.security.SpringSecurityAuthorizationPolicy;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.apache.camel.dataformat.protobuf.ProtobufDataFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Map;

@Component
public class CreateRoute extends RouteBuilder {
    private final ResponseProcessor responseProcessor;
    private final AuthService authService;
    private final CryptoDataFormat aesFormat; // On ne garde que l'essentiel pour le chiffrement

    public CreateRoute(
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
        from("direct:create-" + AppConfig.ENTITY_NAME)
                .routeId("create-movie-proxy-producer")
                .log(">>> HTTP Proxy Received: POST movie")
                .unmarshal().json()
                .process(exchange -> {
                    Map<String, Object> movie = exchange.getIn().getBody(Map.class);
                    String title = (String) movie.getOrDefault("name", "Proxy Movie");

                    BookingEvent bookingEvent = BookingEvent.newBuilder()
                            .setBookingId("bk-proxy-0273")
                            .setStatus(BookingStatus.PENDING)
                            .setMovieTitle((String) movie.get("name"))
                            .setDescription((String) movie.get("description"))
                            .setGenre((String) movie.get("genre"))
                            .setDuration(movie.get("duration") != null ? (Integer) movie.get("duration") : 0)
                            .setReleaseDate((String) movie.get("releaseDate"))
                            .setLanguage((String) movie.get("language"))
                            .build();

                    exchange.getIn().setBody(bookingEvent);
                })
                //.marshal(new ProtobufDataFormat(BookingEvent.getDefaultInstance()))
                .setExchangePattern(org.apache.camel.ExchangePattern.InOnly)
                .marshal(new ProtobufDataFormat(BookingEvent.getDefaultInstance()))
                .marshal(aesFormat)
                .to(AppConfig.QUEUE_CREATE)
                .setBody(constant("{\"status\": \"Movie creation request proxied via ActiveMQ\"}"));

        //consumer
        from(AppConfig.QUEUE_CREATE)
                .routeId("create-movie-proxy-consumer")
                .log("<<< ActiveMQ Consumer: Processing Protobuf payload for backend storage")
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
                    exchange.getIn().setBody(jsonBackend);
                })
                .setProperty(ResponseProcessor.OP_METHOD, constant("POST"))
                .setProperty(ResponseProcessor.OP_ENDPOINT, simple(AppConfig.BASE_URL + "/addmovie"))
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Authorization", method(authService, "getAdminToken").prepend("Bearer "))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(AppConfig.BASE_URL + "/addmovie?bridgeEndpoint=true")
                .process(responseProcessor)
                .to("file:%s?fileName=create-%s-${date:now:%s}.json"
                        .formatted(AppConfig.OUTPUT_DIR, AppConfig.ENTITY_NAME, AppConfig.TIMESTAMP_FORMAT))
                .to(AppConfig.METRIC_PROTO_CREATE_SUCCESS);
    }
}