package io.github.robertomanfreda.jcge.annotator.jackson;

import com.fasterxml.jackson.annotation.*;
import com.squareup.javapoet.JavaFile;
import io.github.robertomanfreda.jcge.factory.JavaFileFactory;
import io.github.robertomanfreda.jcge.utils.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.nio.file.Files;

class JacksonClassAnnotatorTest {

    private String generatedCode;
    private String completeClassName;

    @BeforeEach
    void setUp() throws Exception {
        String filePath = "annotator/jackson/student.yaml";
        File file = FileUtils.getLocalFile(filePath);
        String fileExtension = FileUtils.getFileExtension(filePath);

        JavaFileFactory javaFileFactory = new JavaFileFactory(filePath, Files.readAllBytes(file.toPath()),
                fileExtension
        );

        JavaFile sourceFile = javaFileFactory.generate().getJavaFile();
        generatedCode = sourceFile.toString();
        completeClassName = javaFileFactory.getGenerationDetails().getPackageName() + "." +
                javaFileFactory.getGenerationDetails().getClassName();
    }

    @Test
    public void testGenerateJacksonAnnotations() throws Exception {
        // Use InMemoryJavaCompiler to compile the code from memory
        InMemoryJavaCompiler inMemoryJavaCompiler = InMemoryJavaCompiler.newInstance().useOptions("-proc:none");

        Class<?> generatedClass = inMemoryJavaCompiler.compile(completeClassName, generatedCode);

        // Check for JsonIgnoreProperties annotation
        JsonIgnoreProperties jsonIgnoreProperties = getAnnotation(generatedClass, JsonIgnoreProperties.class);
        Assertions.assertNotNull(jsonIgnoreProperties);
        Assertions.assertArrayEquals(new String[]{"ssn", "password"}, jsonIgnoreProperties.value());

        // Check for JsonInclude annotation
        JsonInclude jsonInclude = getAnnotation(generatedClass, JsonInclude.class);
        Assertions.assertNotNull(jsonInclude);
        Assertions.assertEquals(JsonInclude.Include.NON_NULL, jsonInclude.value());

        // Check for JsonPropertyOrder annotation
        JsonPropertyOrder jsonPropertyOrder = getAnnotation(generatedClass, JsonPropertyOrder.class);
        Assertions.assertNotNull(jsonPropertyOrder);
        Assertions.assertArrayEquals(new String[]{"id", "name", "email", "createdAt", "updatedAt"}, jsonPropertyOrder.value());

        // Check field-specific annotations
        Field nameField = generatedClass.getDeclaredField("name");
        JsonProperty jsonProperty = getAnnotation(nameField, JsonProperty.class);
        Assertions.assertNotNull(jsonProperty);
        Assertions.assertEquals("full_name", jsonProperty.value());

        Field passwordField = generatedClass.getDeclaredField("password");
        JsonIgnore jsonIgnore = getAnnotation(passwordField, JsonIgnore.class);
        Assertions.assertNotNull(jsonIgnore);

        Field ssnField = generatedClass.getDeclaredField("ssn");
        jsonIgnore = getAnnotation(ssnField, JsonIgnore.class);
        Assertions.assertNotNull(jsonIgnore);

        Field createdAtField = generatedClass.getDeclaredField("createdAt");
        JsonFormat jsonFormat = getAnnotation(createdAtField, JsonFormat.class);
        Assertions.assertNotNull(jsonFormat);
        Assertions.assertEquals(JsonFormat.Shape.STRING, jsonFormat.shape());
        Assertions.assertEquals("yyyy-MM-dd HH:mm:ss", jsonFormat.pattern());
        Assertions.assertEquals("UTC", jsonFormat.timezone());

        Field updatedAtField = generatedClass.getDeclaredField("updatedAt");
        jsonFormat = getAnnotation(updatedAtField, JsonFormat.class);
        Assertions.assertNotNull(jsonFormat);
        Assertions.assertEquals(JsonFormat.Shape.STRING, jsonFormat.shape());
        Assertions.assertEquals("yyyy-MM-dd HH:mm:ss", jsonFormat.pattern());
        Assertions.assertEquals("UTC", jsonFormat.timezone());
    }

    private <T extends Annotation> T getAnnotation(AnnotatedElement element, Class<T> annotationClass) {
        return element.getAnnotation(annotationClass);
    }
}
