package io.github.robertomanfreda.jcge.factory;

import io.github.robertomanfreda.jcge.testutils.TestUtils;
import io.github.robertomanfreda.jcge.utils.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class JavaFileFactoryTest {

    private final String OUT_PATH = "target/generated-test-sources/jcge";

    @Test
    void persistCustom() throws IOException {
        String filePath = "examples/custom/student.yaml";
        File file = FileUtils.getLocalFile(filePath);
        String fileExtension = FileUtils.getFileExtension(filePath);

        JavaFileFactory javaFileFactory = new JavaFileFactory(filePath, Files.readAllBytes(file.toPath()),
                fileExtension
        ).generate();

        TestUtils.persist(OUT_PATH, javaFileFactory.getJavaFile(), javaFileFactory.getGenerationDetails(),
                javaFileFactory.getSourceFile()
        );
    }


    @Test
    void persistJPA() throws IOException {
        String filePath = "examples/jpa/student.yaml";
        File file = FileUtils.getLocalFile(filePath);
        String fileExtension = FileUtils.getFileExtension(filePath);

        JavaFileFactory javaFileFactory = new JavaFileFactory(filePath, Files.readAllBytes(file.toPath()),
                fileExtension
        ).generate();

        TestUtils.persist(OUT_PATH, javaFileFactory.getJavaFile(), javaFileFactory.getGenerationDetails(),
                javaFileFactory.getSourceFile()
        );
    }

    @Test
    void persistJackson() throws IOException {
        String filePath = "examples/jackson/student.yaml";
        File file = FileUtils.getLocalFile(filePath);
        String fileExtension = FileUtils.getFileExtension(filePath);

        JavaFileFactory javaFileFactory = new JavaFileFactory(filePath, Files.readAllBytes(file.toPath()),
                fileExtension
        ).generate();

        TestUtils.persist(OUT_PATH, javaFileFactory.getJavaFile(), javaFileFactory.getGenerationDetails(),
                javaFileFactory.getSourceFile()
        );
    }

    @Test
    void persistJsr303() throws IOException {
        String filePath = "examples/jsr303/student.yaml";
        File file = FileUtils.getLocalFile(filePath);
        String fileExtension = FileUtils.getFileExtension(filePath);

        JavaFileFactory javaFileFactory = new JavaFileFactory(filePath, Files.readAllBytes(file.toPath()),
                fileExtension
        ).generate();

        TestUtils.persist(OUT_PATH, javaFileFactory.getJavaFile(), javaFileFactory.getGenerationDetails(),
                javaFileFactory.getSourceFile()
        );
    }
}