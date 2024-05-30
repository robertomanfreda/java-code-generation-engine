package io.github.robertomanfreda.jcge.factory;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import io.github.robertomanfreda.jcge.model.enums.EGenerationType;
import io.github.robertomanfreda.jcge.model.enums.EGenerationVersion;
import io.github.robertomanfreda.jcge.utils.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class GenerationDetailsFactory implements IFactory<GenerationDetails> {

    private final JsonNode rootNode;

    public GenerationDetails generate() {
        FileValidator.validateRootNode(rootNode);

        JsonNode propertiesNode = rootNode.path("properties");

        EGenerationVersion version = EGenerationVersion.valueOf(rootNode.path("version").asText().trim().toUpperCase());
        EGenerationType type = EGenerationType.valueOf(rootNode.path("generationType").asText().trim().toUpperCase());

        FileValidator.validatePropertiesNode(propertiesNode, version);

        String className = rootNode.path("title").asText();

        String packageName = type == EGenerationType.ENTITY ? "jcge.entity" : "jcge.dto";

        return new GenerationDetails(className, packageName, version, type, rootNode, propertiesNode);
    }

}