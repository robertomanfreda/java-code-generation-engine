package io.github.robertomanfreda.jcge.generator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import lombok.extern.slf4j.Slf4j;

import javax.lang.model.element.Modifier;

@Slf4j
public class ToStringGenerator extends AbstractGenerator<ToStringGenerator> {

    private MethodSpec toStringMethod;

    public ToStringGenerator(GenerationDetails generationDetails, TypeSpec.Builder classBuilder) {
        super(generationDetails, classBuilder);
    }

    @Override
    public ToStringGenerator build() {
        log.debug("Building toString method for class: {}", getGenerationDetails().getClassName());
        StringBuilder toStringBody = new StringBuilder();
        toStringBody.append("return \"").append(getGenerationDetails().getClassName()).append("{\" +\n");

        getGenerationDetails().getFields().forEach(field -> {
            String fieldName = field.getKey();
            log.debug("Adding field '{}' to toString method", fieldName);

            toStringBody.append("\"")
                    .append(fieldName)
                    .append("='\" + ")
                    .append(fieldName)
                    .append(" + '\\'' + \", \" +\n");
        });

        // Remove the last ", " from the string
        int lastCommaIndex = toStringBody.lastIndexOf(", \" +\n");
        if (lastCommaIndex != -1) {
            toStringBody.delete(lastCommaIndex, lastCommaIndex + 7);
        }

        toStringBody.append("}\";");
        log.debug("toString method body: \n{}", toStringBody);

        toStringMethod = MethodSpec.methodBuilder("toString")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addAnnotation(Override.class)
                .addCode(toStringBody.toString())
                .build();

        log.debug("toString method for class '{}' built successfully", getGenerationDetails().getClassName());
        return this;
    }

    @Override
    public void add() {
        log.debug("Adding toString method to class: {}", getGenerationDetails().getClassName());

        if (null != toStringMethod) {
            getClassBuilder().addMethod(toStringMethod);
            log.debug("toString method added to class: {}", getGenerationDetails().getClassName());
        } else {
            throw new IllegalStateException("ToString method has not been built yet");
        }
    }
}
