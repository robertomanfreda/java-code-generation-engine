package io.github.robertomanfreda.jcge.mojo;

import io.github.robertomanfreda.jcge.factory.JavaFileFactory;
import io.github.robertomanfreda.jcge.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@Slf4j
public class CodeGenerationMojo extends AbstractMojo {

    @Parameter(property = "localFiles")
    private List<String> localFiles;

    @Parameter(property = "remoteFiles")
    private List<String> remoteFiles;

    @Parameter(property = "localFolders")
    private List<String> localFolders;

    @Parameter(property = "remoteFolders")
    private List<String> remoteFolders;

    public void execute() throws MojoExecutionException {
        try {
            if (localFiles != null && !localFiles.isEmpty()) {
                processLocalFiles(localFiles);
            }

            if (remoteFiles != null && !remoteFiles.isEmpty()) {
                processRemoteFiles(remoteFiles);
            }

            if (localFolders != null && !localFolders.isEmpty()) {
                processLocalFolders(localFolders);
            }

            if (remoteFolders != null && !remoteFolders.isEmpty()) {
                processRemoteFolders(remoteFolders);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error executing code generation", e);
        }
    }

    private void processLocalFiles(List<String> files) throws IOException {
        log.info("Generating code for {} local files.", files.size());
        for (String file : files) {
            processLocalFile(file);
        }
    }

    private void processRemoteFiles(List<String> files) throws IOException {
        log.info("Generating code for {} remote files.", files.size());
        for (String file : files) {
            processRemoteFile(file);
        }
    }

    private void processLocalFolders(List<String> folders) throws IOException {
        log.info("Generating code from local folders.");
        for (String folder : folders) {
            try (Stream<Path> walk = Files.walk(Paths.get(folder))) {
                walk.filter(Files::isRegularFile).forEach(file -> {
                    try {
                        processLocalFile(file.toString());
                    } catch (IOException e) {
                        log.error("Error while processing local file: {}", file, e);
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    private void processRemoteFolders(List<String> folders) throws IOException {
        log.info("Generating code from remote folders.");
        for (String folder : folders) {
            List<String> remoteFiles = FileUtils.listRemoteFiles(folder);
            for (String file : remoteFiles) {
                processRemoteFile(file);
            }
        }
    }

    private void processLocalFile(String filePath) throws IOException {
        log.info("Processing local file: {}", filePath);
        File file = FileUtils.getLocalFile(filePath);
        String fileExtension = FileUtils.getFileExtension(filePath);

        new JavaFileFactory(filePath.trim(), Files.readAllBytes(file.toPath()), fileExtension).generate().persist();
    }

    private void processRemoteFile(String fileUrl) throws IOException {
        log.info("Processing remote file: {}", fileUrl);
        byte[] bytes = FileUtils.downloadFile(fileUrl);
        String fileExtension = FileUtils.getFileExtension(fileUrl);

        new JavaFileFactory(fileUrl.trim(), bytes, fileExtension).generate().persist();
    }
}

