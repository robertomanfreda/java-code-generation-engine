package io.github.robertomanfreda.jcge.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.robertomanfreda.jcge.model.enums.EGenerationType;
import io.github.robertomanfreda.jcge.model.enums.EGenerationVersion;
import io.github.robertomanfreda.jcge.utils.FileValidator;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class GenerationDetails {
    private final String className;
    private final String packageName;
    private final List<Map.Entry<String, JsonNode>> fields = new ArrayList<>();
    private final EGenerationVersion generationVersion;
    private final EGenerationType generationType;
    private final JsonNode rootNode;
    private final JsonNode propertiesNode;

    public GenerationDetails(String className, String packageName, EGenerationVersion generationVersion,
                             EGenerationType generationType, JsonNode rootNode, JsonNode propertiesNode) {

        this.className = className;
        this.packageName = packageName;
        this.generationVersion = generationVersion;
        this.generationType = generationType;
        this.rootNode = rootNode;
        this.propertiesNode = propertiesNode;

        FileValidator.validatePropertiesNode(propertiesNode, generationVersion);

        propertiesNode.fields().forEachRemaining(fields::add);
    }
}
