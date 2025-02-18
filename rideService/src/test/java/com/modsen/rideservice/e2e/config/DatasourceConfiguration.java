package com.modsen.rideservice.e2e.config;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@Configuration
public class DatasourceConfiguration {
    @Primary
    @Bean(name = "rideDataSource")
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "driverDataSource")
    @ConfigurationProperties(prefix="spring.datasource.driver")
    public DataSource driverDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "passengerDataSource")
    @ConfigurationProperties(prefix="spring.datasource.passenger")
    public DataSource passengerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcRideTemplate")
    public JdbcTemplate jdbcRideTemplate(@Qualifier(value = "rideDataSource") DataSource rideDataSource) {
        return new JdbcTemplate(rideDataSource);
    }

    @Bean(name = "jdbcDriverTemplate")
    public JdbcTemplate jdbcDriverTemplate(@Qualifier(value = "driverDataSource") DataSource driverDataSource) {
        return new JdbcTemplate(driverDataSource);
    }

    @Bean(name = "jdbcPassengerTemplate")
    public JdbcTemplate jdbcPassengerTemplate(@Qualifier(value = "passengerDataSource") DataSource passengerDataSource) {
        return new JdbcTemplate(passengerDataSource);
    }
}