package com.yunbian.adsscoring;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ConfigurationPropertiesScan
@MapperScan(
        basePackages = "com.yunbian.adsscoring",
        annotationClass = Mapper.class
)
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        loadDotEnvToSystemProperties();
        SpringApplication.run(Application.class, args);
        System.out.println("yunbian_ads_scoring_java started");
    }

    private static void loadDotEnvToSystemProperties() {
        List<Path> candidates = List.of(
                Path.of(".env"),
                Path.of("../yunbian_ads_scoring/.env"),
                Path.of("../yunbian_ads_scoring_java/.env")
        );

        for (Path candidate : candidates) {
            if (Files.exists(candidate) && Files.isRegularFile(candidate)) {
                loadEnvFile(candidate);
                System.out.println("Loaded .env from: " + candidate.toAbsolutePath().normalize());
                return;
            }
        }

        System.out.println("No .env file found, continue with existing environment variables / JVM properties.");
    }

    private static void loadEnvFile(Path envFile) {
        try {
            List<String> lines = Files.readAllLines(envFile, StandardCharsets.UTF_8);
            for (String rawLine : lines) {
                String line = rawLine == null ? "" : rawLine.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int idx = line.indexOf('=');
                if (idx <= 0) {
                    continue;
                }

                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();

                if ((value.startsWith("\"") && value.endsWith("\"")) ||
                        (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }

                String envValue = System.getenv(key);
                String propValue = System.getProperty(key);

                if ((propValue == null || propValue.isBlank()) &&
                        (envValue == null || envValue.isBlank())) {
                    System.setProperty(key, value);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load .env file: " + envFile.toAbsolutePath(), e);
        }
    }
}