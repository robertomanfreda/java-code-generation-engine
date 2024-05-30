package io.github.robertomanfreda.jcge.annotator.jsr303;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import io.github.robertomanfreda.jcge.annotator.AbstractAnnotator;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import io.github.robertomanfreda.jcge.model.enums.EGenerationVersion;

/**
 * JSR303FieldAnnotator is a concrete implementation of AbstractAnnotator
 * to handle the addition of JSR-303 (Bean Validation) annotations to a JavaPoet FieldSpec.Builder.
 */
public class JSR303FieldAnnotator extends AbstractAnnotator<FieldSpec.Builder> {

    private final JsonNode fieldProps;

    /**
     * Constructs a new JSR303FieldAnnotator.
     *
     * @param generationDetails the details for generation
     * @param builder           the JavaPoet FieldSpec.Builder to which annotations will be added
     * @param fieldProps        the JsonNode containing field properties and annotation details
     */
    public JSR303FieldAnnotator(GenerationDetails generationDetails, FieldSpec.Builder builder, JsonNode fieldProps) {
        super(generationDetails, builder);
        this.fieldProps = fieldProps;
    }

    /**
     * Retrieves the JsonNode containing JSR-303 annotation details for the field.
     *
     * @return the JsonNode with JSR-303 annotation information
     */
    @Override
    protected JsonNode getNode() {
        return fieldProps.path("jsr303");
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
     * Gets the ClassName for a given JSR-303 annotation name based on the generation version.
     *
     * @param annotationName the name of the annotation
     * @return the ClassName of the annotation
     */
    @Override
    protected ClassName getClassName(String annotationName) {
        return getGenerationDetails().getGenerationVersion() == EGenerationVersion.JAKARTA
                ? ClassName.get("jakarta.validation.constraints", annotationName)
                : ClassName.get("javax.validation.constraints", annotationName);
    }
}
