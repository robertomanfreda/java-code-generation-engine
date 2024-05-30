package io.github.robertomanfreda.jcge.factory;

import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.annotator.jackson.JacksonClassAnnotator;
import io.github.robertomanfreda.jcge.annotator.jpa.JPAClassAnnotator;
import io.github.robertomanfreda.jcge.annotator.jsr303.JSR303ClassAnnotator;
import io.github.robertomanfreda.jcge.descriptor.ClassDescriptor;
import io.github.robertomanfreda.jcge.generator.*;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.lang.model.element.Modifier;
import java.io.Serializable;

@Getter
@RequiredArgsConstructor
@Slf4j
public class ClassFactory implements IFactory<TypeSpec> {

    private final GenerationDetails generationDetails;

    @Override
    public TypeSpec generate() {
        String className = generationDetails.getClassName();

        log.info("Generating class: {}", className);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(Serializable.class);

        // Add Javadoc at class level
        new ClassDescriptor(classBuilder, generationDetails.getRootNode()).addDescription();

        // Add JPA Annotations at class level
        new JPAClassAnnotator(generationDetails, classBuilder, generationDetails.getRootNode()).addAnnotations();

        // Add JSR-303 Annotations at class level
        new JSR303ClassAnnotator(generationDetails, classBuilder, generationDetails.getRootNode()).addAnnotations();

        // Add Jackson Annotations at class level
        new JacksonClassAnnotator(generationDetails, classBuilder, generationDetails.getRootNode()).addAnnotations();

        // Add fields
        new FieldsGenerator(generationDetails, classBuilder).build().add();

        // Add NoArgsConstructor
        new NoArgsConstructorGenerator(generationDetails, classBuilder).build().add();

        // Add Equals
        new EqualsGenerator(generationDetails, classBuilder).build().add();

        // Add HashCode
        new HashCodeGenerator(generationDetails, classBuilder).build().add();

        // Add ToString
        new ToStringGenerator(generationDetails, classBuilder).build().add();

        // Add Getters
        new GettersGenerator(generationDetails, classBuilder).build().add();

        // Add Setters
        new SettersGenerator(generationDetails, classBuilder).build().add();

        // Add Builder
        new BuilderGenerator(generationDetails, classBuilder).build().add();

        return classBuilder.build();
    }

}