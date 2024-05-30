package io.github.robertomanfreda.jcge.generator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import lombok.extern.slf4j.Slf4j;

import javax.lang.model.element.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class EqualsGenerator extends AbstractGenerator<EqualsGenerator> {

    private MethodSpec equalsMethod;

    public EqualsGenerator(GenerationDetails generationDetails, TypeSpec.Builder classBuilder) {
        super(generationDetails, classBuilder);
    }

    @Override
    public EqualsGenerator build() {
        log.debug("Building equals method for class: {}", getGenerationDetails().getClassName());

        String className = getGenerationDetails().getClassName();

        MethodSpec.Builder equalsBuilder = MethodSpec.methodBuilder("equals")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addParameter(Object.class, "o")
                .beginControlFlow("if (this == o)")
                .addStatement("return true")
                .endControlFlow()
                .beginControlFlow("if (o == null || getClass() != o.getClass())")
                .addStatement("return false")
                .endControlFlow()
                .addStatement("$L that = ($L) o", className, className)
                .addCode("\nreturn ");

        AtomicBoolean first = new AtomicBoolean(true);

        getGenerationDetails().getFields().forEach(field -> {
            String fieldName = field.getKey();
            log.debug("Adding field '{}' to equals method", fieldName);

            if (first.get()) {
                equalsBuilder.addCode("Objects.equals(this.$L, that.$L)", fieldName, fieldName);
                first.set(false);
            } else {
                equalsBuilder.addCode(" &&\nObjects.equals(this.$L, that.$L)", fieldName, fieldName);
            }
        });

        equalsBuilder.addCode(";\n");

        this.equalsMethod = equalsBuilder.build();

        log.debug("Finished building equals method for class: {}", getGenerationDetails().getClassName());
        return this;
    }

    @Override
    public void add() {
        log.debug("Adding equals method to class: {}", getGenerationDetails().getClassName());

        if (null != equalsMethod) {
            getClassBuilder().addMethod(equalsMethod);
            log.debug("Added equals method to class: {}", getGenerationDetails().getClassName());
        } else {
            throw new IllegalStateException("Equals method has not been built yet");
        }
    }
}

