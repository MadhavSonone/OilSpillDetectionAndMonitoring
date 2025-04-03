package com.project.app.views;

import com.project.app.service.HttpClient;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@PageTitle("Image Upload | Vessel Tracking")
@Route(value = "upload", layout = MainLayout.class)
public class ImageUploadView extends VerticalLayout {

    private MemoryBuffer buffer;
    private Upload upload;
    private Image preview;
    private Button analyzeButton;
    private Div imageContainer;
    private Div resultContainer;
    private Paragraph resultText;

    public ImageUploadView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        setPadding(true);
        setSpacing(true);

        // Card container for better UI
        Div card = new Div();
        card.addClassNames(
            LumoUtility.Background.BASE,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.BoxShadow.MEDIUM,
            LumoUtility.Padding.LARGE,
            LumoUtility.Width.MEDIUM
        );

        H2 title = new H2("Oil Spill Detection");
        title.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        Paragraph description = new Paragraph("Upload an image to analyze for oil spill detection.");
        description.addClassNames(LumoUtility.TextColor.SECONDARY);

        // Upload component
        buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setAcceptedFileTypes("image/png", "image/jpeg", "image/jpg");
        upload.addClassName(LumoUtility.Width.FULL);

        // Image preview container
        imageContainer = new Div();
        imageContainer.setVisible(false);
        preview = new Image();
        preview.setAlt("Uploaded Image");
        preview.setMaxHeight("300px");
        imageContainer.add(preview);

        // Analysis result container
        resultContainer = new Div();
        resultContainer.setVisible(false);
        resultText = new Paragraph();
        resultContainer.add(resultText);

        // Analyze button
        analyzeButton = new Button("Analyze Image", new Icon(VaadinIcon.SEARCH));
        analyzeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        analyzeButton.addClassNames(LumoUtility.Width.FULL, LumoUtility.Margin.Top.MEDIUM);
        analyzeButton.setEnabled(false);

        // Handle file upload
        upload.addSucceededListener(event -> {
            // Remove previous result container if any
            resultContainer.setVisible(false);  // Hide the previous result container

            String fileName = event.getFileName();
            StreamResource resource = new StreamResource(fileName, () -> buffer.getInputStream());
            preview.setSrc(resource);
            imageContainer.setVisible(true);
            analyzeButton.setEnabled(true);
        });

        // Handle file errors
        upload.addFileRejectedListener(event -> {
            Notification.show("File rejected: " + event.getErrorMessage(), 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        // Analyze image when button is clicked
        analyzeButton.addClickListener(e -> analyzeImage());

        // Help text
        Paragraph helpText = new Paragraph("Supported formats: JPEG, PNG • Max size: 10MB");
        helpText.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL, LumoUtility.TextAlignment.CENTER);

        // Add components to card
        card.add(title, description, upload, imageContainer, analyzeButton, resultContainer, helpText);
        add(card);
    }

    private void analyzeImage() {
        // Disable button and clear previous result
        analyzeButton.setEnabled(false);
        analyzeButton.setText("Analyzing...");
        analyzeButton.setIcon(new Icon(VaadinIcon.SPINNER));

        try {
            // Simulating input file stream
            InputStream fileData = buffer.getInputStream();
            String fileName = buffer.getFileName();
            String uploadDir = "Uploads"; // Local directory
            new File(uploadDir).mkdirs();

            File uploadedFile = new File(uploadDir, fileName);
            Files.copy(fileData, uploadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Send file to backend and get response
            String response = HttpClient.postFile("http://localhost:9090/api/analyze/upload", uploadedFile.getAbsolutePath());
            System.out.println("Backend Response: " + response);  // Logging backend response

            // Check if the response is valid
            if (response != null && !response.isEmpty()) {
                String originalFileName = fileName.substring(0, fileName.lastIndexOf('.'));
                String segmentedImageName = originalFileName + "_segmented.jpg";
                String segmentedImagePath = "Outputs" + File.separator + segmentedImageName;

                File segmentedFile = new File(segmentedImagePath);

                // Check if segmented image file exists
                if (segmentedFile.exists()) {
                    // Create resource for the segmented image
                    StreamResource segmentedImageResource = new StreamResource(segmentedImageName, () -> {
                        try {
                            return new FileInputStream(segmentedImagePath);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            return null;
                        }
                    });

                    Image segmentedImage = new Image(segmentedImageResource, "Segmented Image");
                    segmentedImage.setMaxWidth("500px");

                    // Clear previous results
                    resultContainer.removeAll();

                    // Add the image to the result container
                    resultContainer.add(segmentedImage);
                    resultContainer.setVisible(true);
                    resultText.setText("Analysis Result: Segmented Image Displayed");

                    // Show success notification
                    Notification.show("Analysis complete!", 3000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    // If segmented image does not exist, show error
                    Notification.show("Segmented image not found.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                // If backend response is empty or null, show error
                Notification.show("Analysis failed, please try again.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (Exception ex) {
            // If any error occurs during processing, show error
            Notification.show("Error during analysis: " + ex.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } finally {
            // Reset analyze button
            analyzeButton.setText("Analyze Image");
            analyzeButton.setIcon(new Icon(VaadinIcon.SEARCH));
            analyzeButton.setEnabled(true);
        }
    }
}
