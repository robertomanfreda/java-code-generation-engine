package io.github.robertomanfreda.jcge.descriptor;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractDescriptor<T> implements IDescriptor {

    private final T builder;

    public void addDescription() {
        JsonNode descriptionNode = getDescriptionNode();
        String description = descriptionNode != null ? descriptionNode.asText() : null;
        if (description != null && !description.isEmpty()) {
            addJavadocToBuilder(builder, description);
        }
    }

    protected abstract JsonNode getDescriptionNode();

    protected abstract void addJavadocToBuilder(T builder, String description);
}
