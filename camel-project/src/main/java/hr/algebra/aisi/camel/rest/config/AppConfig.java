package hr.algebra.aisi.camel.rest.config;

import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    public static final String BASE_URL = "http://localhost:8080/api/movies";
    public static final String OUTPUT_DIR = "output/responses/";
    public static final String TIMESTAMP_FORMAT = "dd.MM.yyyy-HH.mm.ss";
    public static final String ENTITY_NAME = "movie";

    public static final String QUEUE_GET_ALL = "activemq:queue:movies-0273-json-queue";
    public static final String QUEUE_CREATE  = "activemq:queue:movies-0273-protobuf-create-queue";
    public static final String QUEUE_UPDATE  = "activemq:queue:movies-0273-protobuf-update-queue";
    public static final String QUEUE_DLQ     = "activemq:queue:movies-0273-dlq";

    public static final String DIRECT_PUBLISH_AVRO = "direct:publish-movie-sensors-0273";
    public static final String DIRECT_PUBLISH_CREATE_PROTO = "direct:publish-booking-protobuf-0273";
    public static final String DIRECT_PUBLISH_UPDATE_PROTO = "direct:publish-update-protobuf-0273";

    public static final String METRIC_AVRO_SUCCESS = "micrometer:counter:movies_0273_avro_success";
    public static final String METRIC_PROTO_CREATE_SUCCESS = "micrometer:counter:movies_0273_proto_create_success";
    public static final String METRIC_PROTO_UPDATE_SUCCESS = "micrometer:counter:movies_0273_proto_update_success";


    private AppConfig() {
    }
}
