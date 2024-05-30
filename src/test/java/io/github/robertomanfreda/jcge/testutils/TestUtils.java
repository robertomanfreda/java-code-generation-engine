package io.github.robertomanfreda.jcge.testutils;

import com.squareup.javapoet.JavaFile;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import io.github.robertomanfreda.jcge.model.enums.EGenerationType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class TestUtils {
    public static void persist(String outputPath, JavaFile javaFile, GenerationDetails generationDetails,
                               String sourceFile) throws IOException {

        log.debug("Persisting Java file starting from: {}", sourceFile);

        if (generationDetails.getGenerationType() == EGenerationType.ENTITY) {
            outputPath = outputPath + "/entity/";
        } else {
            outputPath = outputPath + "/dto/";
        }

        outputPath = outputPath + generationDetails.getClassName() + ".java";
        outputPath = outputPath.replaceAll("//+", "/");

        Path finalOutputPath = Path.of(outputPath);
        Path parent = finalOutputPath.getParent();

        Files.createDirectories(parent);
        Files.write(finalOutputPath, javaFile.toString().getBytes(StandardCharsets.UTF_8));

        log.info("Code generated at: {}", finalOutputPath);
    }
}
