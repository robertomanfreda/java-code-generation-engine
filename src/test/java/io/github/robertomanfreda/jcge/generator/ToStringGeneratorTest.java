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

class ToStringGeneratorTest {

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
    public void testGenerateToString() throws Exception {
        // Use InMemoryJavaCompiler to compile the code from memory
        InMemoryJavaCompiler inMemoryJavaCompiler = InMemoryJavaCompiler.newInstance().useOptions("-proc:none");

        Class<?> generatedClass = inMemoryJavaCompiler.compile(completeClassName, generatedCode);

        // Check that the class contains the toString method
        Method toStringMethod = generatedClass.getDeclaredMethod("toString");
        Assertions.assertNotNull(toStringMethod);
        Assertions.assertEquals(String.class, toStringMethod.getReturnType());

        // Create an instance of the generated class and set its fields
        Object instance = generatedClass.getDeclaredConstructor().newInstance();
        Method setId = generatedClass.getDeclaredMethod("setId", Integer.class);
        setId.invoke(instance, 1);
        Method setFirstName = generatedClass.getDeclaredMethod("setFirstName", String.class);
        setFirstName.invoke(instance, "John");
        Method setLastName = generatedClass.getDeclaredMethod("setLastName", String.class);
        setLastName.invoke(instance, "Doe");

        // Invoke the toString method and verify the output
        String expectedToString = "Student{id='1', firstName='John', lastName='Doe'}";
        String actualToString = toStringMethod.invoke(instance).toString();
        Assertions.assertEquals(expectedToString, actualToString);
    }
}
