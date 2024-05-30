package io.github.robertomanfreda.jcge.annotator.jsr303;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.annotator.AbstractAnnotator;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import io.github.robertomanfreda.jcge.model.enums.EGenerationVersion;

/**
 * JSR303ClassAnnotator is a concrete implementation of AbstractAnnotator
 * to handle the addition of JSR-303 (Bean Validation) annotations to a JavaPoet TypeSpec.Builder.
 */
public class JSR303ClassAnnotator extends AbstractAnnotator<TypeSpec.Builder> {

    private final JsonNode rootNode;

    /**
     * Constructs a new JSR303ClassAnnotator.
     *
     * @param generationDetails the details for generation
     * @param classBuilder      the JavaPoet TypeSpec.Builder to which annotations will be added
     * @param rootNode          the root JsonNode containing annotation details
     */
    public JSR303ClassAnnotator(GenerationDetails generationDetails, TypeSpec.Builder classBuilder, JsonNode rootNode) {
        super(generationDetails, classBuilder);
        this.rootNode = rootNode;
    }

    /**
     * Retrieves the JsonNode containing JSR-303 annotation details.
     *
     * @return the JsonNode with JSR-303 annotation information
     */
    @Override
    protected JsonNode getNode() {
        return rootNode.path("jsr303");
    }

    /**
     * Adds the provided AnnotationSpec to the TypeSpec.Builder.
     *
     * @param builder        the builder to add the annotation to
     * @param annotationSpec the AnnotationSpec to be added
     */
    @Override
    protected void addAnnotationToBuilder(TypeSpec.Builder builder, AnnotationSpec annotationSpec) {
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
