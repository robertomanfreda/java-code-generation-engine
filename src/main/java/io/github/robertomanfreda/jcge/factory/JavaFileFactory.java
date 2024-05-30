package io.github.robertomanfreda.jcge.factory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import io.github.robertomanfreda.jcge.model.enums.EGenerationType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@RequiredArgsConstructor
@Slf4j
public class JavaFileFactory implements IFactory<JavaFileFactory> {

    private final String sourceFile;
    private final byte[] fileContent;
    private final String fileExtension;

    private GenerationDetails generationDetails;
    private JavaFile javaFile;

    public JavaFileFactory generate() throws IOException {
        log.info("Generating Java file starting from: {}", sourceFile);

        ObjectMapper objectMapper;

        if (fileExtension.equalsIgnoreCase("yaml") || fileExtension.equalsIgnoreCase("yml")) {
            objectMapper = new ObjectMapper(new YAMLFactory());
        } else if (fileExtension.equalsIgnoreCase("json")) {
            objectMapper = new ObjectMapper(new JsonFactory());
        } else {
            throw new RuntimeException("Unsupported file extension: " + fileExtension);
        }

        JsonNode rootNode = objectMapper.readTree(fileContent);

        generationDetails = new GenerationDetailsFactory(rootNode).generate();

        TypeSpec typeSpec = new ClassFactory(generationDetails).generate();

        javaFile = JavaFile.builder(generationDetails.getPackageName(), typeSpec).indent("    ").build();

        return this;
    }

    public void persist() throws IOException {
        log.debug("Persisting Java file starting from: {}", sourceFile);

        String outputPath = "target/generated-sources/jcge/";

        if (generationDetails.getGenerationType() == EGenerationType.ENTITY) {
            outputPath = outputPath + "entity/";
        } else {
            outputPath = outputPath + "dto/";
        }

        outputPath = outputPath + generationDetails.getClassName() + ".java";

        Path finalOutputPath = Path.of(outputPath);
        Path parent = finalOutputPath.getParent();

        Files.createDirectories(parent);
        Files.write(finalOutputPath, javaFile.toString().getBytes(StandardCharsets.UTF_8));

        log.info("Code generated at: {}", finalOutputPath);
    }
}
