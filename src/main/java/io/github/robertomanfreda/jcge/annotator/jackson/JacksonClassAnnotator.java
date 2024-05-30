package io.github.robertomanfreda.jcge.annotator.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.annotator.AbstractAnnotator;
import io.github.robertomanfreda.jcge.model.GenerationDetails;

/**
 * JacksonClassAnnotator is a concrete implementation of AbstractAnnotator
 * to handle the addition of Jackson annotations to a JavaPoet TypeSpec.Builder.
 */
public class JacksonClassAnnotator extends AbstractAnnotator<TypeSpec.Builder> {

    private final JsonNode rootNode;

    /**
     * Constructs a new JacksonClassAnnotator.
     *
     * @param generationDetails the details for generation
     * @param builder           the JavaPoet TypeSpec.Builder to which annotations will be added
     * @param rootNode          the root JsonNode containing annotation details
     */
    public JacksonClassAnnotator(GenerationDetails generationDetails, TypeSpec.Builder builder, JsonNode rootNode) {
        super(generationDetails, builder);
        this.rootNode = rootNode;
    }

    /**
     * Retrieves the JsonNode containing Jackson annotation details.
     *
     * @return the JsonNode with Jackson annotation information
     */
    @Override
    protected JsonNode getNode() {
        return rootNode.path("jackson");
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
