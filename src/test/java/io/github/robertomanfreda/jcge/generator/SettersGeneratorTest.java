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
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

class SettersGeneratorTest {

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
    public void testGenerateSetters() throws Exception {
        // Use InMemoryJavaCompiler to compile the code from memory
        InMemoryJavaCompiler inMemoryJavaCompiler = InMemoryJavaCompiler.newInstance().useOptions("-proc:none");

        Class<?> generatedClass = inMemoryJavaCompiler.compile(completeClassName, generatedCode);

        // Check that the class contains the expected setters
        List<String> expectedSetters = Arrays.asList("setId", "setFirstName", "setLastName"); // Add other setters if needed
        for (String setterName : expectedSetters) {
            Method setterMethod = generatedClass.getDeclaredMethod(setterName, determineParameterType(setterName));
            Assertions.assertNotNull(setterMethod);
            Assertions.assertEquals(void.class, setterMethod.getReturnType());
            Assertions.assertEquals(Modifier.PUBLIC, setterMethod.getModifiers());

            // Check the parameter type
            Parameter[] parameters = setterMethod.getParameters();
            Assertions.assertEquals(1, parameters.length);
            Assertions.assertEquals(getExpectedParameterType(setterName), parameters[0].getType());
        }
    }

    private Class<?> determineParameterType(String setterName) {
        switch (setterName) {
            case "setId":
                return Integer.class;
            case "setFirstName":
            case "setLastName":
                return String.class;
            default:
                throw new IllegalArgumentException("Unknown setter: " + setterName);
        }
    }

    private Class<?> getExpectedParameterType(String setterName) {
        switch (setterName) {
            case "setId":
                return Integer.class;
            case "setFirstName":
            case "setLastName":
                return String.class;
            default:
                throw new IllegalArgumentException("Unknown setter: " + setterName);
        }
    }
}
