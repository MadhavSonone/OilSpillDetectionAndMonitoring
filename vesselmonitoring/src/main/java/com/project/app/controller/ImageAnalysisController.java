package com.project.app.controller;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/analyze")
@CrossOrigin("*")
public class ImageAnalysisController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "Uploads";
    private static final String PYTHON_EXEC = "python";
    private static final String SCRIPT_PATH = System.getProperty("user.dir") + File.separator + "Spark" + File.separator + "analyze.py";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Ensure upload directory exists
        	if (!new File(UPLOAD_DIR).exists()) {
        	    new File(UPLOAD_DIR).mkdirs();
        	}


            // Save the uploaded file
            String filePath = UPLOAD_DIR + File.separator + file.getOriginalFilename();
            File destinationFile = new File(filePath);
            file.transferTo(destinationFile);

            // Calls Python Model
            String result = runPythonModel(filePath);
            if (result == null || result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image or empty output from model.");
            }

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error saving file: " + e.getMessage());
        }
    }

    private String runPythonModel(String imagePath) {
        try {
            // Start the Python script process
            ProcessBuilder processBuilder = new ProcessBuilder(PYTHON_EXEC, SCRIPT_PATH, imagePath);
            processBuilder.redirectErrorStream(false);  // Don't combine stdout and stderr
            Process process = processBuilder.start();

            // Capture the standard output and error streams separately
            try (InputStream inputStream = process.getInputStream();
                 InputStream errorStream = process.getErrorStream();
                 Scanner sc = new Scanner(inputStream).useDelimiter("\\A");
                 Scanner errScanner = new Scanner(errorStream).useDelimiter("\\A")) {

                String output = sc.hasNext() ? sc.next().trim() : "";
                String errorOutput = errScanner.hasNext() ? errScanner.next().trim() : "";

                // Wait for the process to complete and get the exit code
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    // Return an error message if the process fails
                    return "Python script failed with exit code: " + exitCode + ". Error: " + errorOutput;
                }

                // Return the standard output or error output if there was an issue
                return output.isEmpty() ? errorOutput : output;
            }
        } catch (IOException | InterruptedException e) {
            // Return error message if an exception occurs during the process execution
            return "Error running Python model: " + e.getMessage();
        }
    }

}
