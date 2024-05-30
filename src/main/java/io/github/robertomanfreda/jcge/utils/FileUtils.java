package io.github.robertomanfreda.jcge.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileUtils {

    public static File getLocalFile(String path) {
        // Try to load the file as a classpath resource
        URL resource = FileUtils.class.getClassLoader().getResource(path);
        if (resource != null) {
            try {
                // Convert the URL to a file path
                Path resourcePath = Paths.get(resource.toURI());
                return resourcePath.toFile();
            } catch (URISyntaxException e) {
                throw new RuntimeException("Error converting resource URI to path", e);
            }
        }

        // Try to load the file as an absolute path
        Path absolutePath = Paths.get(path);
        if (Files.exists(absolutePath)) {
            return absolutePath.toFile();
        }

        // If the file is not found, throw an exception
        throw new RuntimeException("File not found: " + path);
    }

    public static byte[] downloadFile(String fileURL) throws IOException {
        log.info("Downloading file: {}", fileURL);

        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // Check if the connection is successful
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // Extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
                }
            } else {
                // Extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            }

            log.debug("Content-Type = {}", contentType);
            log.debug("Content-Length = {}", contentLength);
            log.debug("File Name = {}", fileName);

            // Opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            BufferedInputStream inStream = new BufferedInputStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            byteArrayOutputStream.close();
            inStream.close();
            httpConn.disconnect();

            log.info("Downloaded file: {}", fileName);

            return byteArrayOutputStream.toByteArray();
        } else {
            throw new RuntimeException("Error connecting to URL: " + fileURL);
        }
    }

    public static String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");

        if (lastIndexOfDot == -1) {
            return "";
        }

        String ext = fileName.substring(lastIndexOfDot + 1);

        log.info("File extension is: {}", ext);

        return ext;
    }

    public static List<String> listRemoteFiles(String folderUrl) throws IOException {
        List<String> fileUrls = new ArrayList<>();
        log.info("Listing files in remote folder: {}", folderUrl);

        // For the sake of example, assuming the remote server lists files in a plain text format, one URL per line
        URL url = new URL(folderUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                fileUrls.add(line.trim());
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new RuntimeException("Error connecting to URL: " + folderUrl);
        }

        log.info("Found {} files in remote folder: {}", fileUrls.size(), folderUrl);

        return fileUrls;
    }
}
