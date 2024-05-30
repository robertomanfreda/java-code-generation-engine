package io.github.robertomanfreda.jcge.annotator.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

class JacksonFieldAnnotatorTest {

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
    public void testGenerateJacksonFieldAnnotations() throws Exception {
        // Use InMemoryJavaCompiler to compile the code from memory
        InMemoryJavaCompiler inMemoryJavaCompiler = InMemoryJavaCompiler.newInstance().useOptions("-proc:none");

        Class<?> generatedClass = inMemoryJavaCompiler.compile(completeClassName, generatedCode);

        // Check field-specific annotations
        checkFieldAnnotation(generatedClass, "name", JsonProperty.class, jsonProperty -> {
            Assertions.assertEquals("full_name", jsonProperty.value());
        });

        checkFieldAnnotation(generatedClass, "password", JsonIgnore.class, Assertions::assertNotNull);

        checkFieldAnnotation(generatedClass, "ssn", JsonIgnore.class, Assertions::assertNotNull);

        checkFieldAnnotation(generatedClass, "createdAt", JsonFormat.class, jsonFormat -> {
            Assertions.assertEquals(JsonFormat.Shape.STRING, jsonFormat.shape());
            Assertions.assertEquals("yyyy-MM-dd HH:mm:ss", jsonFormat.pattern());
            Assertions.assertEquals("UTC", jsonFormat.timezone());
        });

        checkFieldAnnotation(generatedClass, "updatedAt", JsonFormat.class, jsonFormat -> {
            Assertions.assertEquals(JsonFormat.Shape.STRING, jsonFormat.shape());
            Assertions.assertEquals("yyyy-MM-dd HH:mm:ss", jsonFormat.pattern());
            Assertions.assertEquals("UTC", jsonFormat.timezone());
        });
    }

    private <T extends Annotation> void checkFieldAnnotation(Class<?> generatedClass,
                                                             String fieldName,
                                                             Class<T> annotationClass,
                                                             AnnotationConsumer<T> consumer) throws NoSuchFieldException {

        Field field = generatedClass.getDeclaredField(fieldName);
        T annotation = getAnnotation(field, annotationClass);
        Assertions.assertNotNull(annotation);
        consumer.accept(annotation);
    }

    private <T extends Annotation> T getAnnotation(AnnotatedElement element, Class<T> annotationClass) {
        return element.getAnnotation(annotationClass);
    }

    @FunctionalInterface
    private interface AnnotationConsumer<T> {
        void accept(T annotation);
    }
}
