package io.github.robertomanfreda.jcge.generator;

import com.squareup.javapoet.JavaFile;
import io.github.robertomanfreda.jcge.factory.JavaFileFactory;
import io.github.robertomanfreda.jcge.utils.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Objects;

class HashCodeGeneratorTest {

    private String generatedCode;
    private String completeClassName;

    @BeforeEach
    void setUp() throws Exception {
        String filePath = "generator/student.yaml";
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
    public void testGenerateHashCode() throws Exception {
        // Use InMemoryJavaCompiler to compile the code from memory
        InMemoryJavaCompiler inMemoryJavaCompiler = InMemoryJavaCompiler.newInstance().useOptions("-proc:none");

        Class<?> generatedClass = inMemoryJavaCompiler.compile(completeClassName, generatedCode);

        // Check that the class contains the hashCode method
        Method hashCodeMethod = generatedClass.getDeclaredMethod("hashCode");
        Assertions.assertNotNull(hashCodeMethod);
        Assertions.assertEquals(int.class, hashCodeMethod.getReturnType());

        // Create an instance of the generated class and set its fields
        Object instance = generatedClass.getDeclaredConstructor().newInstance();
        Method setId = generatedClass.getDeclaredMethod("setId", Integer.class);
        setId.invoke(instance, 1);
        Method setFirstName = generatedClass.getDeclaredMethod("setFirstName", String.class);
        setFirstName.invoke(instance, "John");
        Method setLastName = generatedClass.getDeclaredMethod("setLastName", String.class);
        setLastName.invoke(instance, "Doe");

        // Invoke the hashCode method and verify the output
        int expectedHashCode = Objects.hash(1, "John", "Doe");
        int actualHashCode = (int) hashCodeMethod.invoke(instance);
        Assertions.assertEquals(expectedHashCode, actualHashCode);
    }
}
