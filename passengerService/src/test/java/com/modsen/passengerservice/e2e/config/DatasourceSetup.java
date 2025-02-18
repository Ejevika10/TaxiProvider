package com.modsen.passengerservice.e2e.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class DatasourceSetup {
    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;

    @PostConstruct
    public void setUp() throws Exception {
        executeSqlScript("classpath:/com.modsen.passengerservice.e2e/sql/passenger-datasource-setup.sql");
    }

    private void executeSqlScript(String path) throws Exception {
        Resource resource = resourceLoader.getResource(path);
        String sql = new String(Files.readAllBytes(Paths.get(resource.getURI())));

        jdbcTemplate.execute(sql);
    }
}
