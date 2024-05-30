package io.github.robertomanfreda.jcge.utils;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.robertomanfreda.jcge.model.enums.EGenerationVersion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileValidator {

    public static void validateRootNode(JsonNode rootNode) {
        if (!rootNode.hasNonNull("version")) {
            log.error("Validation error: Field 'version' must be present.");
            throw new IllegalArgumentException("Field 'version' must be present.");
        }

        String version = rootNode.get("version").asText().trim().toUpperCase();
        log.debug("Validating root node for version: {}", version);

        if (!rootNode.hasNonNull("title")) {
            log.error("Validation error: Field 'title' must be present.");
            throw new IllegalArgumentException("Field 'title' must be present.");
        } else {
            if (!ClassNameValidator.isValidClassName(rootNode.get("title").asText().trim())) {
                throw new IllegalArgumentException("Validation error: Field 'title' must be a valid class name.");
            }
        }

        if (!rootNode.hasNonNull("generationType")) {
            log.error("Validation error: Field 'generationType' must be present.");
            throw new IllegalArgumentException("Field 'generationType' must be present.");
        }

        validateNode(rootNode, EGenerationVersion.valueOf(version));
    }

    public static void validatePropertiesNode(JsonNode propertiesNode, EGenerationVersion version) {
        log.debug("Validating properties node for version: {}", version);

        for (JsonNode jsonNode : propertiesNode) {
            validateNode(jsonNode, version);
        }
    }

    private static void validateNode(JsonNode jsonNode, EGenerationVersion version) {
        if (jsonNode.hasNonNull("jackson")) {
            log.debug("Validating Jackson annotations");
            validateJackson(jsonNode);
        }

        if (jsonNode.hasNonNull("jpa")) {
            log.debug("Validating JPA annotations for version: {}", version);
            validateJPA(jsonNode, version);
        }

        if (jsonNode.hasNonNull("jsr303")) {
            log.debug("Validating JSR-303 annotations for version: {}", version);
            validateJSR303(jsonNode, version);
        }
    }

    private static void validateJackson(JsonNode jsonNode) {
        for (JsonNode jacksonAnnotation : jsonNode.get("jackson")) {
            String[] parts = jacksonAnnotation.asText().split("\\(", 2);
            String annotationName = parts[0];
            try {
                Class.forName("com.fasterxml.jackson.annotation." + annotationName);
                log.debug("Validated Jackson annotation: {}", annotationName);
            } catch (ClassNotFoundException e) {
                log.error("Annotation 'com.fasterxml.jackson.annotation.{}' not found.", annotationName);
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private static void validateJPA(JsonNode jsonNode, EGenerationVersion generationVersion) {
        for (JsonNode jpaAnnotation : jsonNode.get("jpa")) {
            String[] parts = jpaAnnotation.asText().split("\\(", 2);
            String annotationName = parts[0];

            try {
                if (generationVersion == EGenerationVersion.JAKARTA) {
                    Class.forName("jakarta.persistence." + annotationName);
                } else {
                    Class.forName("javax.persistence." + annotationName);
                }
                log.debug("Validated JPA annotation: {} for version: {}", annotationName, generationVersion);
            } catch (ClassNotFoundException e) {
                String annotationType = generationVersion == EGenerationVersion.JAKARTA
                        ? "jakarta.persistence."
                        : "javax.persistence.";

                logNotFoundError(annotationType, annotationName);

                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private static void validateJSR303(JsonNode jsonNode, EGenerationVersion generationVersion) {
        for (JsonNode jsr303Annotation : jsonNode.get("jsr303")) {
            String[] parts = jsr303Annotation.asText().split("\\(", 2);
            String annotationName = parts[0];

            try {
                if (generationVersion == EGenerationVersion.JAKARTA) {
                    Class.forName("jakarta.validation.constraints." + annotationName);
                } else {
                    Class.forName("javax.validation.constraints." + annotationName);
                }
                log.debug("Validated JSR-303 annotation: {} for version: {}", annotationName, generationVersion);
            } catch (ClassNotFoundException e) {
                String annotationType = generationVersion == EGenerationVersion.JAKARTA
                        ? "jakarta.validation.constraints."
                        : "javax.validation.constraints.";

                logNotFoundError(annotationType, annotationName);

                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private static void logNotFoundError(String annotationType, String annotationName) {
        log.error("Annotation '{}{}' not found.", annotationType, annotationName);

    }
}
