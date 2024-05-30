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

class BuilderGeneratorTest {

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
    public void testGenerateBuilder() throws Exception {
        // Use InMemoryJavaCompiler to compile the code from memory
        InMemoryJavaCompiler inMemoryJavaCompiler = InMemoryJavaCompiler.newInstance().useOptions("-proc:none");

        Class<?> generatedClass = inMemoryJavaCompiler.compile(completeClassName, generatedCode);

        // Check that the class contains the builder method
        Method builderMethod = generatedClass.getDeclaredMethod("builder");
        Assertions.assertNotNull(builderMethod);
        Assertions.assertTrue(java.lang.reflect.Modifier.isStatic(builderMethod.getModifiers()));

        // Load the Builder class using the class loader of the generated class
        Class<?> builderClass = generatedClass.getClassLoader().loadClass(completeClassName + "$Builder");
        Assertions.assertEquals(builderClass, builderMethod.getReturnType());

        // Check that the Builder class contains the expected setter methods
        Method setId = builderClass.getDeclaredMethod("id", Integer.class);
        Assertions.assertNotNull(setId);
        Assertions.assertEquals(builderClass, setId.getReturnType());

        Method setFirstName = builderClass.getDeclaredMethod("firstName", String.class);
        Assertions.assertNotNull(setFirstName);
        Assertions.assertEquals(builderClass, setFirstName.getReturnType());

        Method setLastName = builderClass.getDeclaredMethod("lastName", String.class);
        Assertions.assertNotNull(setLastName);
        Assertions.assertEquals(builderClass, setLastName.getReturnType());

        // Check that the Builder class contains the build method
        Method buildMethod = builderClass.getDeclaredMethod("build");
        Assertions.assertNotNull(buildMethod);
        Assertions.assertEquals(generatedClass, buildMethod.getReturnType());

        // Create an instance using the builder and verify the fields
        Object builderInstance = builderMethod.invoke(null);
        builderInstance = setId.invoke(builderInstance, 1);
        builderInstance = setFirstName.invoke(builderInstance, "John");
        builderInstance = setLastName.invoke(builderInstance, "Doe");
        Object instance = buildMethod.invoke(builderInstance);

        Method getId = generatedClass.getDeclaredMethod("getId");
        Method getFirstName = generatedClass.getDeclaredMethod("getFirstName");
        Method getLastName = generatedClass.getDeclaredMethod("getLastName");

        Assertions.assertEquals(1, getId.invoke(instance));
        Assertions.assertEquals("John", getFirstName.invoke(instance));
        Assertions.assertEquals("Doe", getLastName.invoke(instance));
    }
}
