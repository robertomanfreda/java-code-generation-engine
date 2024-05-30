package io.github.robertomanfreda.jcge.generator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import lombok.extern.slf4j.Slf4j;

import javax.lang.model.element.Modifier;

@Slf4j
public class NoArgsConstructorGenerator extends AbstractGenerator<NoArgsConstructorGenerator> {

    private MethodSpec noArgsConstructorMethod;

    public NoArgsConstructorGenerator(GenerationDetails generationDetails, TypeSpec.Builder classBuilder) {
        super(generationDetails, classBuilder);
    }

    @Override
    public NoArgsConstructorGenerator build() {
        log.debug("Building no-args constructor for class: {}", getGenerationDetails().getClassName());

        noArgsConstructorMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build();

        log.debug("Finished building no-args constructor for class: {}", getGenerationDetails().getClassName());
        return this;
    }

    @Override
    public void add() {
        log.debug("Adding no-args constructor to class: {}", getGenerationDetails().getClassName());

        if (null != noArgsConstructorMethod) {
            getClassBuilder().addMethod(noArgsConstructorMethod);
            log.debug("Added no-args constructor to class: {}", getGenerationDetails().getClassName());
        } else {
            throw new IllegalStateException("No args constructor has not been built yet");
        }
    }
}
