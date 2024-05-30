package io.github.robertomanfreda.jcge.annotator.jsr303;

import com.squareup.javapoet.JavaFile;
import io.github.robertomanfreda.jcge.factory.JavaFileFactory;
import io.github.robertomanfreda.jcge.utils.FileUtils;
import jakarta.validation.constraints.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.nio.file.Files;

class JSR303FieldAnnotatorTest {

    private String generatedCode;
    private String completeClassName;

    @BeforeEach
    void setUp() throws Exception {
        String filePath = "annotator/jsr303/student.yaml";
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
    public void testGenerateJSR303FieldAnnotations() throws Exception {
        // Use InMemoryJavaCompiler to compile the code from memory
        InMemoryJavaCompiler inMemoryJavaCompiler = InMemoryJavaCompiler.newInstance().useOptions("-proc:none");

        Class<?> generatedClass = inMemoryJavaCompiler.compile(completeClassName, generatedCode);

        // Check field-specific annotations
        checkFieldAnnotation(generatedClass, "id", NotNull.class, Assertions::assertNotNull);
        checkFieldAnnotation(generatedClass, "name", NotBlank.class, Assertions::assertNotNull);
        checkFieldAnnotation(generatedClass, "name", Size.class, size -> {
            Assertions.assertEquals(1, size.min());
            Assertions.assertEquals(100, size.max());
        });
        checkFieldAnnotation(generatedClass, "email", NotBlank.class, Assertions::assertNotNull);
        checkFieldAnnotation(generatedClass, "email", Email.class, Assertions::assertNotNull);
        checkFieldAnnotation(generatedClass, "password", NotBlank.class, Assertions::assertNotNull);
        checkFieldAnnotation(generatedClass, "password", Size.class, size -> {
            Assertions.assertEquals(8, size.min());
            Assertions.assertEquals(100, size.max());
        });
        checkFieldAnnotation(generatedClass, "ssn", Pattern.class, pattern -> {
            Assertions.assertEquals("^(?!000|666)[0-8][0-9]{2}-[0-9]{2}-[0-9]{4}$", pattern.regexp());
        });
        checkFieldAnnotation(generatedClass, "createdAt", PastOrPresent.class, Assertions::assertNotNull);
        checkFieldAnnotation(generatedClass, "updatedAt", PastOrPresent.class, Assertions::assertNotNull);
    }

    private <T extends Annotation> void checkFieldAnnotation(Class<?> generatedClass,
                                                             String fieldName, Class<T> annotationClass,
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
