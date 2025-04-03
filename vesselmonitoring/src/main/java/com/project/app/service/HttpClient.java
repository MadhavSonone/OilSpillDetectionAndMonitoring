package com.project.app.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

public class HttpClient {

    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    public static String postFile(String url, String filePath) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            File file = new File(filePath);
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", file)
                    .build();
            post.setEntity(entity);

            HttpResponse response = httpClient.execute(post);

            // Check if the response was successful
            if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
                String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                log.error("Failed to upload file. HTTP error code: {}. Response body: {}",
                          response.getStatusLine().getStatusCode(), responseBody);
                throw new Exception("Failed to upload file, HTTP error code: " 
                                    + response.getStatusLine().getStatusCode() + ", Response: " + responseBody);
            }


            // If successful, read and return the response body
            InputStream inputStream = response.getEntity().getContent();
            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            log.info("File uploaded successfully, received response: {}", result);
            return result;
        } catch (IOException e) {
            log.error("Error during file upload: ", e);
            throw new Exception("I/O error during file upload: " + e.getMessage(), e);
        }
        catch (Exception e) {
            log.error("Error during HTTP request", e);
            throw new Exception("Error during HTTP request: " + e.getMessage(), e);
        }

    }
}
