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
public class GettersGenerator extends AbstractGenerator<GettersGenerator> {

    private List<MethodSpec> getterMethods = new ArrayList<>();

    public GettersGenerator(GenerationDetails generationDetails, TypeSpec.Builder classBuilder) {
        super(generationDetails, classBuilder);
    }

    @Override
    public GettersGenerator build() {
        log.debug("Building getters for class: {}", getGenerationDetails().getClassName());

        getterMethods = getGenerationDetails().getFields().stream()
                .map(this::generateGetter)
                .collect(Collectors.toList());

        log.debug("Finished building {} getters for class: {}", getterMethods.size(), getGenerationDetails().getClassName());
        return this;
    }

    @Override
    public void add() {
        log.debug("Adding getters to class: {}", getGenerationDetails().getClassName());

        if (null != getterMethods && !getterMethods.isEmpty()) {
            getterMethods.forEach(getter -> {
                log.debug("Adding getter method: {}", getter);
                getClassBuilder().addMethod(getter);
            });

            log.debug("Added {} getters to class: {}", getterMethods.size(), getGenerationDetails().getClassName());
        } else {
            throw new IllegalStateException("Getter methods not built yet");
        }
    }

    private MethodSpec generateGetter(Map.Entry<String, JsonNode> field) {
        String fieldName = field.getKey();
        JsonNode fieldProps = field.getValue();
        log.debug("Generating getter for field: {}", fieldName);

        MethodSpec getter = MethodSpec.methodBuilder("get" + StringUtils.capitalize(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .returns(mapType(fieldProps))
                .addStatement("return $L", fieldName)
                .build();

        log.debug("Generated getter method: {}", getter);
        return getter;
    }
}

