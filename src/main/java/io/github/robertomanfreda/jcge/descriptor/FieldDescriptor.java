package io.github.robertomanfreda.jcge.descriptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.FieldSpec;

public class FieldDescriptor extends AbstractDescriptor<FieldSpec.Builder> {

    private final JsonNode fieldProps;

    public FieldDescriptor(FieldSpec.Builder fieldBuilder, JsonNode fieldProps) {
        super(fieldBuilder);
        this.fieldProps = fieldProps;
    }

    @Override
    protected JsonNode getDescriptionNode() {
        return fieldProps.get("description");
    }

    @Override
    protected void addJavadocToBuilder(FieldSpec.Builder builder, String description) {
        builder.addJavadoc("$L\n", description);
    }
}