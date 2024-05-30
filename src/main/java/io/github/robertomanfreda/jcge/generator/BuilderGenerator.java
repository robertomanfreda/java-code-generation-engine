package io.github.robertomanfreda.jcge.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import lombok.extern.slf4j.Slf4j;

import javax.lang.model.element.Modifier;
import java.util.List;

@Slf4j
public class BuilderGenerator extends AbstractGenerator<BuilderGenerator> {

    private final List<FieldSpec> fieldSpecs;
    private MethodSpec builderMethod;

    public BuilderGenerator(GenerationDetails generationDetails, TypeSpec.Builder classBuilder) {
        super(generationDetails, classBuilder);
        this.fieldSpecs = classBuilder.fieldSpecs;
    }

    @Override
    public BuilderGenerator build() {
        log.debug("Building builder for class: {}", getGenerationDetails().getClassName());

        ClassName className = ClassName.get(getGenerationDetails().getPackageName(), getGenerationDetails().getClassName());
        TypeSpec.Builder builderClassBuilder = TypeSpec.classBuilder("Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        // Add fields to the builder
        fieldSpecs.forEach(fieldSpec -> {
            FieldSpec builderField = FieldSpec.builder(fieldSpec.type, fieldSpec.name, Modifier.PRIVATE).build();
            builderClassBuilder.addField(builderField);

            // Add setter methods to the builder
            MethodSpec setterMethod = MethodSpec.methodBuilder(fieldSpec.name)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.bestGuess("Builder"))
                    .addParameter(fieldSpec.type, fieldSpec.name)
                    .addStatement("this.$N = $N", fieldSpec.name, fieldSpec.name)
                    .addStatement("return this")
                    .build();
            builderClassBuilder.addMethod(setterMethod);
        });

        // Add the build method
        MethodSpec.Builder buildMethodBuilder = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(className)
                .addStatement("$T instance = new $T()", className, className);

        fieldSpecs.forEach(fieldSpec -> buildMethodBuilder.addStatement("instance.$N = this.$N", fieldSpec.name, fieldSpec.name));

        buildMethodBuilder.addStatement("return instance");

        builderClassBuilder.addMethod(buildMethodBuilder.build());

        // Add the builder class to the main class
        getClassBuilder().addType(builderClassBuilder.build());

        // Add the static builder method to the main class
        builderMethod = MethodSpec.methodBuilder("builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.bestGuess("Builder"))
                .addStatement("return new Builder()")
                .build();

        log.debug("Finished building builder for class: {}", getGenerationDetails().getClassName());

        return this;
    }

    @Override
    public void add() {
        log.debug("Adding equals method to class: {}", getGenerationDetails().getClassName());

        if (null != builderMethod) {
            getClassBuilder().addMethod(builderMethod);
            log.debug("Added equals method to class: {}", getGenerationDetails().getClassName());
        } else {
            throw new IllegalStateException("Builder method has not been built yet");
        }
    }
}
