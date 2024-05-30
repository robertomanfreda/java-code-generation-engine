package io.github.robertomanfreda.jcge.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.robertomanfreda.jcge.utils.TypeUtils.mapType;

@Slf4j
public class SettersGenerator extends AbstractGenerator<SettersGenerator> {

    private List<MethodSpec> setterMethods = new ArrayList<>();

    public SettersGenerator(GenerationDetails generationDetails, TypeSpec.Builder classBuilder) {
        super(generationDetails, classBuilder);
    }

    @Override
    public SettersGenerator build() {
        log.debug("Building setters for class: {}", getGenerationDetails().getClassName());

        setterMethods = getGenerationDetails().getFields().stream()
                .map(this::generateSetter)
                .collect(Collectors.toList());

        log.debug("Finished building {} setters for class: {}", setterMethods.size(), getGenerationDetails().getClassName());
        return this;
    }

    @Override
    public void add() {
        log.debug("Adding setters to class: {}", getGenerationDetails().getClassName());

        if (null != setterMethods && !setterMethods.isEmpty()) {
            setterMethods.forEach(setter -> {
                log.debug("Adding setter method: {}", setter);
                getClassBuilder().addMethod(setter);
            });

            log.debug("Added {} setters to class: {}", setterMethods.size(), getGenerationDetails().getClassName());
        } else {
            throw new IllegalStateException("Setter methods not built yet");
        }
    }

    private MethodSpec generateSetter(Map.Entry<String, JsonNode> field) {
        String fieldName = field.getKey();
        JsonNode fieldProps = field.getValue();
        log.debug("Generating setter for field: {}", fieldName);

        MethodSpec setter = MethodSpec.methodBuilder("set" + StringUtils.capitalize(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mapType(fieldProps), fieldName)
                .addStatement("this.$L = $L", fieldName, fieldName)
                .build();

        log.debug("Generated setter method: {}", setter);
        return setter;
    }

}

