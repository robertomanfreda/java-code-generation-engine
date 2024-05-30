package io.github.robertomanfreda.jcge.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.annotator.jackson.JacksonFieldAnnotator;
import io.github.robertomanfreda.jcge.annotator.jpa.JPAFieldAnnotator;
import io.github.robertomanfreda.jcge.annotator.jsr303.JSR303FieldAnnotator;
import io.github.robertomanfreda.jcge.descriptor.FieldDescriptor;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.robertomanfreda.jcge.utils.TypeUtils.mapType;

@Getter
@Slf4j
public class FieldsGenerator extends AbstractGenerator<FieldsGenerator> {

    private List<FieldSpec> fieldSpecs = new ArrayList<>();

    public FieldsGenerator(GenerationDetails generationDetails, TypeSpec.Builder classBuilder) {
        super(generationDetails, classBuilder);
    }

    @Override
    public FieldsGenerator build() {
        log.debug("Building fields for class: {}", getGenerationDetails().getClassName());

        fieldSpecs = getGenerationDetails().getFields().stream()
                .map(field -> {
                    String fieldName = field.getKey();
                    JsonNode fieldProps = field.getValue();
                    log.debug("Generating field: {}", fieldName);

                    FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(
                            mapType(fieldProps),
                            fieldName,
                            Modifier.PRIVATE
                    );

                    // Add Javadoc at field level
                    new FieldDescriptor(fieldSpecBuilder, fieldProps).addDescription();

                    // Add JPA Annotations at field level
                    new JPAFieldAnnotator(getGenerationDetails(), fieldSpecBuilder, fieldProps).addAnnotations();

                    // Add JSR-303 Annotations at field level
                    new JSR303FieldAnnotator(getGenerationDetails(), fieldSpecBuilder, fieldProps).addAnnotations();

                    // Add Jackson Annotations at field level
                    new JacksonFieldAnnotator(getGenerationDetails(), fieldSpecBuilder, fieldProps).addAnnotations();

                    FieldSpec fieldSpec = fieldSpecBuilder.build();
                    log.debug("Generated field spec: {}", fieldSpec);
                    return fieldSpec;
                })
                .collect(Collectors.toList());

        log.debug("Finished building {} fields for class: {}", fieldSpecs.size(), getGenerationDetails().getClassName());
        return this;
    }

    @Override
    public void add() {
        log.debug("Adding fields to class: {}", getGenerationDetails().getClassName());

        if (null != fieldSpecs && !fieldSpecs.isEmpty()) {
            fieldSpecs.forEach(field -> {
                log.debug("Adding field: {}", field);
                getClassBuilder().addField(field);
            });

            log.debug("Added {} fields to class: {}", fieldSpecs.size(), getGenerationDetails().getClassName());
        } else {
            throw new IllegalStateException("FieldSpecs has not been built yet");
        }
    }
}

