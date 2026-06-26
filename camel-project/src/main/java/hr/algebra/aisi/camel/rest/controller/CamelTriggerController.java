package hr.algebra.aisi.camel.rest.controller;


import hr.algebra.aisi.camel.rest.config.AppConfig;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/camel/movies")
public class CamelTriggerController {

    private static final Logger LOG = LoggerFactory.getLogger(CamelTriggerController.class);

    private ProducerTemplate producerTemplate;

    public CamelTriggerController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> triggerGetAll() {
        LOG.info("HTTP trigger received: GET /camel/movies");
        String result = producerTemplate.requestBody(
                "direct:get-all-" + AppConfig.ENTITY_NAME, null, String.class);
        return ResponseEntity.ok(result);
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> triggerCreate(@RequestBody String movieJson) {
        LOG.info("HTTP trigger received: POST /camel/movies");
        String result = producerTemplate.requestBody(
                "direct:create-" + AppConfig.ENTITY_NAME, movieJson, String.class);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> triggerUpdate(@PathVariable Long id,
                                                @RequestBody String movieJson) {
        LOG.info("HTTP trigger received: PUT /camel/movies/{}", id);
        Map<String, Object> headers = new HashMap<>();
        headers.put("targetId", id);
        String result = producerTemplate.requestBodyAndHeaders(
                "direct:update-movie", movieJson, headers, String.class);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> triggerDelete(@PathVariable Long id) {
        LOG.info("HTTP trigger received: DELETE /camel/movies/{}", id);
        Map<String, Object> headers = new HashMap<>();
        headers.put("targetId", id);
        String result = producerTemplate.requestBodyAndHeaders(
                "direct:delete-movie", null, headers, String.class);
        return ResponseEntity.ok(result);
    }

}

