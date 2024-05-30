package io.github.robertomanfreda.jcge.generator;

import com.squareup.javapoet.JavaFile;
import io.github.robertomanfreda.jcge.factory.JavaFileFactory;
import io.github.robertomanfreda.jcge.utils.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;

class NoArgsConstructorGeneratorTest {

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
    public void testGenerateNoArgsConstructor() throws Exception {
        // Use InMemoryJavaCompiler to compile the code from memory
        InMemoryJavaCompiler inMemoryJavaCompiler = InMemoryJavaCompiler.newInstance().useOptions("-proc:none");

        Class<?> generatedClass = inMemoryJavaCompiler.compile(completeClassName, generatedCode);

        // Check that the class contains a no-args constructor
        Constructor<?> noArgsConstructor = generatedClass.getDeclaredConstructor();
        Assertions.assertNotNull(noArgsConstructor);
        Assertions.assertEquals(0, noArgsConstructor.getParameterCount());
        Assertions.assertEquals(Modifier.PUBLIC, noArgsConstructor.getModifiers());
    }
}
