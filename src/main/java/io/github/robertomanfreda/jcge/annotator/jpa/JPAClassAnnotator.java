package io.github.robertomanfreda.jcge.annotator.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.annotator.AbstractAnnotator;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import io.github.robertomanfreda.jcge.model.enums.EGenerationVersion;

/**
 * JPAClassAnnotator is a concrete implementation of AbstractAnnotator
 * to handle the addition of JPA annotations to a JavaPoet TypeSpec.Builder.
 */
public class JPAClassAnnotator extends AbstractAnnotator<TypeSpec.Builder> {

    private final JsonNode rootNode;

    /**
     * Constructs a new JPAClassAnnotator.
     *
     * @param generationDetails the details for generation
     * @param builder           the JavaPoet TypeSpec.Builder to which annotations will be added
     * @param rootNode          the root JsonNode containing annotation details
     */
    public JPAClassAnnotator(GenerationDetails generationDetails, TypeSpec.Builder builder, JsonNode rootNode) {
        super(generationDetails, builder);
        this.rootNode = rootNode;
    }

    /**
     * Retrieves the JsonNode containing JPA annotation details.
     *
     * @return the JsonNode with JPA annotation information
     */
    @Override
    protected JsonNode getNode() {
        return rootNode.path("jpa");
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
     * Gets the ClassName for a given JPA annotation name based on the generation version.
     *
     * @param annotationName the name of the annotation
     * @return the ClassName of the annotation
     */
    @Override
    protected ClassName getClassName(String annotationName) {
        return getGenerationDetails().getGenerationVersion() == EGenerationVersion.JAKARTA
                ? ClassName.get("jakarta.persistence", annotationName)
                : ClassName.get("javax.persistence", annotationName);
    }
}
