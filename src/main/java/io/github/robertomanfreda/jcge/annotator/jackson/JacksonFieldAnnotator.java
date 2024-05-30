package io.github.robertomanfreda.jcge.annotator.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import io.github.robertomanfreda.jcge.annotator.AbstractAnnotator;
import io.github.robertomanfreda.jcge.model.GenerationDetails;

/**
 * JacksonFieldAnnotator is a concrete implementation of AbstractAnnotator
 * to handle the addition of Jackson annotations to a JavaPoet FieldSpec.Builder.
 */
public class JacksonFieldAnnotator extends AbstractAnnotator<FieldSpec.Builder> {

    private final JsonNode fieldProps;

    /**
     * Constructs a new JacksonFieldAnnotator.
     *
     * @param generationDetails the details for generation
     * @param fieldBuilder      the JavaPoet FieldSpec.Builder to which annotations will be added
     * @param fieldProps        the JsonNode containing field properties and annotation details
     */
    public JacksonFieldAnnotator(GenerationDetails generationDetails, FieldSpec.Builder fieldBuilder, JsonNode fieldProps) {
        super(generationDetails, fieldBuilder);
        this.fieldProps = fieldProps;
    }

    /**
     * Retrieves the JsonNode containing Jackson annotation details for the field.
     *
     * @return the JsonNode with Jackson annotation information
     */
    @Override
    protected JsonNode getNode() {
        return fieldProps.path("jackson");
    }

    /**
     * Adds the provided AnnotationSpec to the FieldSpec.Builder.
     *
     * @param builder        the builder to add the annotation to
     * @param annotationSpec the AnnotationSpec to be added
     */
    @Override
    protected void addAnnotationToBuilder(FieldSpec.Builder builder, AnnotationSpec annotationSpec) {
        builder.addAnnotation(annotationSpec);
    }

    /**
     * Gets the ClassName for a given Jackson annotation name.
     *
     * @param annotationName the name of the annotation
     * @return the ClassName of the annotation
     */
    @Override
    protected ClassName getClassName(String annotationName) {
        return ClassName.get("com.fasterxml.jackson.annotation", annotationName);
    }
}
