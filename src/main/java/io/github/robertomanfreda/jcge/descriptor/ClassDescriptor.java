package io.github.robertomanfreda.jcge.descriptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.TypeSpec;


public class ClassDescriptor extends AbstractDescriptor<TypeSpec.Builder> {

    private final JsonNode rootNode;

    public ClassDescriptor(TypeSpec.Builder classBuilder, JsonNode rootNode) {
        super(classBuilder);
        this.rootNode = rootNode;
    }

    @Override
    protected JsonNode getDescriptionNode() {
        return rootNode.get("description");
    }

    @Override
    protected void addJavadocToBuilder(TypeSpec.Builder builder, String description) {
        builder.addJavadoc("$L\n", description);
    }
}
