package io.github.robertomanfreda.jcge.annotator.jpa;

import com.squareup.javapoet.JavaFile;
import io.github.robertomanfreda.jcge.factory.JavaFileFactory;
import io.github.robertomanfreda.jcge.utils.FileUtils;
import jakarta.persistence.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.nio.file.Files;

class JPAClassAnnotatorTest {

    private String generatedCode;
    private String completeClassName;

    @BeforeEach
    void setUp() throws Exception {
        String filePath = "annotator/jpa/student.yaml";
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
    public void testGenerateJPAAnnotations() throws Exception {
        // Use InMemoryJavaCompiler to compile the code from memory
        InMemoryJavaCompiler inMemoryJavaCompiler = InMemoryJavaCompiler.newInstance().useOptions("-proc:none");

        Class<?> generatedClass = inMemoryJavaCompiler.compile(completeClassName, generatedCode);

        // Check for Entity annotation
        Entity entityAnnotation = getAnnotation(generatedClass, Entity.class);
        Assertions.assertNotNull(entityAnnotation);

        // Check for Table annotation
        Table tableAnnotation = getAnnotation(generatedClass, Table.class);
        Assertions.assertNotNull(tableAnnotation);
        Assertions.assertEquals("students", tableAnnotation.name());

        // Check field-specific annotations
        checkFieldAnnotation(generatedClass, "id", Id.class, Assertions::assertNotNull);
        checkFieldAnnotation(generatedClass, "id", GeneratedValue.class, generatedValue -> {
            Assertions.assertEquals(GenerationType.IDENTITY, generatedValue.strategy());
        });
        checkFieldAnnotation(generatedClass, "name", Column.class, column -> {
            Assertions.assertEquals("student_name", column.name());
        });
        checkFieldAnnotation(generatedClass, "email", Column.class, column -> {
            Assertions.assertTrue(column.unique());
        });
        checkFieldAnnotation(generatedClass, "createdAt", Column.class, column -> {
            Assertions.assertFalse(column.updatable());
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
