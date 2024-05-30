package io.github.robertomanfreda.jcge.annotator;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class to handle the annotation process.
 *
 * @param <T> the type of builder used for annotations
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractAnnotator<T> {

    @Getter
    private final GenerationDetails generationDetails;
    private final T builder;

    /**
     * Adds annotations based on the JsonNode retrieved from getNode().
     */
    public void addAnnotations() {
        JsonNode jpaNode = getNode();
        if (jpaNode.isArray()) {
            for (JsonNode annotation : jpaNode) {
                addAnnotation(annotation);
            }
        } else if (jpaNode.isTextual()) {
            addAnnotation(jpaNode);
        }
    }

    /**
     * Retrieves the JsonNode containing annotation details.
     *
     * @return the JsonNode with annotation information
     */
    protected abstract JsonNode getNode();

    /**
     * Adds the provided AnnotationSpec to the builder.
     *
     * @param builder        the builder to add the annotation to
     * @param annotationSpec the AnnotationSpec to be added
     */
    protected abstract void addAnnotationToBuilder(T builder, AnnotationSpec annotationSpec);

    /**
     * Gets the ClassName for a given annotation name.
     *
     * @param annotationName the name of the annotation
     * @return the ClassName of the annotation
     */
    protected abstract ClassName getClassName(String annotationName);

    /**
     * Adds an annotation to the builder based on the JsonNode provided.
     *
     * @param annotation the JsonNode containing annotation information
     */
    private void addAnnotation(JsonNode annotation) {
        String annotationText = annotation.asText();
        log.trace("Annotation Text = {}", annotationText);

        if (annotationText.contains("(")) {
            // Split annotation name and parameters
            String[] parts = annotationText.split("\\(", 2);
            String annotationName = parts[0];
            String annotationParams = parts[1].substring(0, parts[1].length() - 1); // Remove the closing parenthesis

            // Get the ClassName for the annotation
            ClassName className = getClassName(annotationName);
            AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(className);

            // Process each parameter in the annotation
            for (String param : splitParameters(annotationParams)) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2) {
                    // Key-value pair parameter
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    log.trace("Key = {}, Value = {}", key, value);
                    addParameter(annotationBuilder, key, value, className);
                } else if (keyValue.length == 1) {
                    // Single value parameter
                    String value = keyValue[0].trim();
                    log.trace("Value = {}", value);
                    addParameter(annotationBuilder, "value", value, className);
                } else {
                    throw new IllegalArgumentException("Invalid annotation parameter format: " + param);
                }
            }

            // Add the built annotation to the builder
            addAnnotationToBuilder(builder, annotationBuilder.build());
        } else {
            // Annotation without parameters
            ClassName className = getClassName(annotationText);
            addAnnotationToBuilder(builder, AnnotationSpec.builder(className).build());
        }
    }

    /**
     * Splits parameters in the annotation string, handling cases where values may contain commas.
     *
     * @param params the parameter string to split
     * @return an array of split parameters
     */
    private String[] splitParameters(String params) {
        // Splits parameters taking into account commas within values
        // Uses regex to handle balanced parentheses
        return params.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    /**
     * Adds parameters to the annotation builder, handling arrays and strings appropriately.
     *
     * @param annotationBuilder the AnnotationSpec builder
     * @param key               the key of the parameter
     * @param value             the value of the parameter
     * @param className         the class name of the annotation to resolve enums
     */
    private void addParameter(AnnotationSpec.Builder annotationBuilder, String key, String value, ClassName className) {
        log.trace("Adding Parameter - Key: {}, Value: {}", key, value);

        if (value.startsWith("{") || value.endsWith("}")) {
            String[] elements = value.replace("{", "").replace("}", "").split(",");
            for (String element : elements) {
                String trimmedElement = element.trim();
                if (trimmedElement.startsWith("\"") && trimmedElement.endsWith("\"")) {
                    trimmedElement = trimmedElement.substring(1, trimmedElement.length() - 1);
                }
                annotationBuilder.addMember(key, "$S", trimmedElement);
            }
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            String unescapedValue = value.substring(1, value.length() - 1).replace("\\\\", "\\");
            annotationBuilder.addMember(key, "$S", unescapedValue);
        } else if (value.contains(".")) {
            String[] enumParts = value.split("\\.");
            if (enumParts.length == 2) {
                String enumClassName = className.packageName() + "." + enumParts[0];
                try {
                    Class<?> enumClass = Class.forName(enumClassName);
                    annotationBuilder.addMember(key, "$T.$L", enumClass, enumParts[1]);
                } catch (ClassNotFoundException e) {
                    log.warn("Enum class not found for value: {}", value, e);
                    annotationBuilder.addMember(key, "$L", value);
                }
            } else {
                annotationBuilder.addMember(key, "$L", value);
            }
        } else {
            annotationBuilder.addMember(key, "$L", value);
        }
    }
}
