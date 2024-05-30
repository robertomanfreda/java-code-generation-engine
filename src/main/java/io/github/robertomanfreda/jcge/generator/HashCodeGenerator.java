package io.github.robertomanfreda.jcge.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import lombok.extern.slf4j.Slf4j;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class HashCodeGenerator extends AbstractGenerator<HashCodeGenerator> {

    private MethodSpec hashCodeMethod;

    public HashCodeGenerator(GenerationDetails generationDetails, TypeSpec.Builder classBuilder) {
        super(generationDetails, classBuilder);
    }

    @Override
    public HashCodeGenerator build() {
        log.debug("Building hashCode method for class: {}", getGenerationDetails().getClassName());

        List<Map.Entry<String, JsonNode>> fields = getGenerationDetails().getFields();

        MethodSpec.Builder hashCodeBuilder = MethodSpec.methodBuilder("hashCode")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addCode("return $T.hash(", Objects.class);

        for (int i = 0; i < fields.size(); i++) {
            Map.Entry<String, JsonNode> field = fields.get(i);
            String fieldName = field.getKey();
            log.debug("Adding field '{}' to hashCode method", fieldName);
            hashCodeBuilder.addCode("$L", fieldName);

            if (i < fields.size() - 1) {
                hashCodeBuilder.addCode(", ");
            }
        }

        hashCodeBuilder.addCode(");");

        hashCodeMethod = hashCodeBuilder.build();

        log.debug("Finished building hashCode method for class: {}", getGenerationDetails().getClassName());
        return this;
    }

    @Override
    public void add() {
        log.debug("Adding hashCode method to class: {}", getGenerationDetails().getClassName());

        if (null != hashCodeMethod) {
            getClassBuilder().addMethod(hashCodeMethod);
            log.debug("Added hashCode method to class: {}", getGenerationDetails().getClassName());
        } else {
            throw new IllegalStateException("HashCode method has not been built yet");
        }
    }
}

