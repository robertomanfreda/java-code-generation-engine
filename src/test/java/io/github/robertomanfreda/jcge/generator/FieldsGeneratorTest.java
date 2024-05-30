package io.github.robertomanfreda.jcge.generator;

import com.squareup.javapoet.JavaFile;
import io.github.robertomanfreda.jcge.factory.JavaFileFactory;
import io.github.robertomanfreda.jcge.utils.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;

class FieldsGeneratorTest {

    String completeClassName;
    private String generatedCode;

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
    public void testGenerateFields() throws Exception {
        // Use InMemoryJavaCompiler to compile the code from memory
        InMemoryJavaCompiler inMemoryJavaCompiler = InMemoryJavaCompiler.newInstance().useOptions("-proc:none");

        Class<?> generatedClass = inMemoryJavaCompiler.compile(completeClassName, generatedCode);

        // Check that the class contains tKhe expected fields
        Field idField = generatedClass.getDeclaredField("id");
        Assertions.assertNotNull(idField);
        Assertions.assertTrue(Modifier.isPrivate(idField.getModifiers()));

        Field firstNameField = generatedClass.getDeclaredField("firstName");
        Assertions.assertNotNull(firstNameField);
        Assertions.assertTrue(Modifier.isPrivate(firstNameField.getModifiers()));

        Field lastNameField = generatedClass.getDeclaredField("lastName");
        Assertions.assertNotNull(lastNameField);
        Assertions.assertTrue(Modifier.isPrivate(lastNameField.getModifiers()));
    }
}
