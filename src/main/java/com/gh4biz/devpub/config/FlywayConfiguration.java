package com.gh4biz.devpub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {
    @Bean
    public FlywayConfigurationCustomizer customizeLicense(
            @Value("${my-app.flyway.license}") String license) {
        return configuration -> configuration.licenseKey(license);
    }
}
