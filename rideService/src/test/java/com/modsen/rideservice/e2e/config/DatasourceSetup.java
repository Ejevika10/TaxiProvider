package com.modsen.rideservice.e2e.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class DatasourceSetup {

    @Autowired
    @Qualifier("jdbcRideTemplate")
    private JdbcTemplate jdbcRideTemplate;

    @Autowired
    @Qualifier("jdbcDriverTemplate")
    private JdbcTemplate jdbcDriverTemplate;

    @Autowired
    @Qualifier("jdbcPassengerTemplate")
    private JdbcTemplate jdbcPassengerTemplate;

    private final ResourceLoader resourceLoader;

    @PostConstruct
    public void setUp() throws Exception {
        executeSqlScript(jdbcRideTemplate, "classpath:/e2e/sql/ride-datasource-setup.sql");
        executeSqlScript(jdbcDriverTemplate, "classpath:/e2e/sql/driver-datasource-setup.sql");
        executeSqlScript(jdbcPassengerTemplate, "classpath:/e2e/sql/passenger-datasource-setup.sql");
    }

    private void executeSqlScript(JdbcTemplate jdbcTemplate, String path) throws Exception {
        Resource resource = resourceLoader.getResource(path);
        String sql = new String(Files.readAllBytes(Paths.get(resource.getURI())));

        jdbcTemplate.execute(sql);
    }
}
