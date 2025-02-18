package com.modsen.ratingservice.e2e.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatasourceSetup {

    @Qualifier("jdbcRideTemplate")
    private final JdbcTemplate jdbcRideTemplate;

    private final ResourceLoader resourceLoader;

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void setUp() throws Exception {
        executeSqlScript(jdbcRideTemplate, "classpath:/com/modsen/ratingservice/sql/ride-datasource-setup.sql");
        executeMongoPreparation(mongoTemplate);
    }

    private void executeSqlScript(JdbcTemplate jdbcTemplate, String path) throws Exception {
        Resource resource = resourceLoader.getResource(path);
        String sql = new String(Files.readAllBytes(Paths.get(resource.getURI())));

        jdbcTemplate.execute(sql);
    }

    private void executeMongoPreparation(MongoTemplate mongoTemplate) throws Exception {
        mongoTemplate.dropCollection("driver_ratings");

        Map<String, Object> driverRating = new HashMap<>();
        driverRating.put("_id", new ObjectId("67af2043d38b5434a550ce2d"));
        driverRating.put("rideId", 2);
        driverRating.put("userId", "00000000-0000-0001-0000-000000000001");
        driverRating.put("rating", 1);
        driverRating.put("comment", "This is a comment");
        driverRating.put("deleted", false);

        mongoTemplate.save(driverRating, "driver_ratings");

        mongoTemplate.dropCollection("passenger_ratings");

        Map<String, Object> passengerRating = new HashMap<>();
        passengerRating.put("_id", new ObjectId("67af2045d38b5434a550ce2e"));
        passengerRating.put("rideId",2);
        passengerRating.put("userId","00000000-0000-0001-0000-000000000002");
        passengerRating.put("rating", 1);
        passengerRating.put("comment", "This is a comment");
        passengerRating.put("deleted", false);

        Map<String, Object> res = mongoTemplate.save(passengerRating, "passenger_ratings");
        log.info(res.get("_id").toString());
    }
}
