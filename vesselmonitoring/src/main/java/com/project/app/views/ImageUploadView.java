package com.project.app.views;

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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.InputStream;

@PageTitle("Image Upload | Vessel Tracking")
@Route(value = "upload", layout = MainLayout.class)
@AnonymousAllowed
public class ImageUploadView extends VerticalLayout {

    private MemoryBuffer buffer;
    private Upload upload;
    private Image preview;
    private Button submitButton;
    private Div imageContainer;

    public ImageUploadView() {
        addClassName("upload-view");
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        setPadding(true);
        setSpacing(true);

        // Create a card container for upload contents
        Div card = new Div();
        card.addClassNames(
            LumoUtility.Background.BASE,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.BoxShadow.MEDIUM,
            LumoUtility.Padding.LARGE,
            LumoUtility.Width.MEDIUM
        );



        H2 title = new H2("Oil Spill Detection");
        title.addClassName(LumoUtility.Margin.Top.MEDIUM);

        Paragraph description = new Paragraph("Upload satellite or drone Images to check for oil spill.");
        description.addClassNames(
            LumoUtility.TextColor.SECONDARY,
            LumoUtility.Margin.Top.NONE,
            LumoUtility.Margin.Bottom.MEDIUM
        );

        // Setup upload component
        buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setDropAllowed(true);
        upload.setAcceptedFileTypes("image/png", "image/jpeg", "image/jpg");
        upload.addClassName(LumoUtility.Width.FULL);
        
        // Style the upload component
        upload.getElement().getStyle().set("box-sizing", "border-box");
        upload.getElement().getStyle().set("border", "1px dashed var(--lumo-contrast-30pct)");
        upload.getElement().getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        upload.getElement().getStyle().set("padding", "var(--lumo-space-m)");
        upload.getElement().getStyle().set("background-color", "var(--lumo-contrast-5pct)");

        // Image preview container
        imageContainer = new Div();
        imageContainer.setVisible(false);
        imageContainer.addClassNames(
            LumoUtility.Padding.MEDIUM, 
            LumoUtility.Border.ALL,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Margin.Vertical.MEDIUM,
            LumoUtility.Width.FULL
        );
        imageContainer.getStyle().set("border-style", "dashed");
        imageContainer.getStyle().set("border-color", "var(--lumo-contrast-30pct)");
        imageContainer.getStyle().set("text-align", "center");

        preview = new Image();
        preview.setAlt("Preview image");
        preview.setMaxHeight("300px");
        preview.addClassName(LumoUtility.Width.AUTO);
        
        imageContainer.add(preview);

        // Submit button with loading state
        submitButton = new Button("Analyze Image");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setIcon(new Icon(VaadinIcon.SEARCH));
        submitButton.addClassNames(LumoUtility.Width.FULL, LumoUtility.Margin.Top.MEDIUM);
        submitButton.setEnabled(false);

        // Handle successful upload
        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            StreamResource resource = new StreamResource(fileName, () -> buffer.getInputStream());
            preview.setSrc(resource);
            imageContainer.setVisible(true);
            submitButton.setEnabled(true);
            
            Notification notification = Notification.show(
                "Image uploaded successfully!", 
                3000, 
                Notification.Position.BOTTOM_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        // Handle failed upload
        upload.addFailedListener(event -> {
            Notification notification = Notification.show(
                "Upload failed: " + event.getReason(), 
                5000, 
                Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        // Handle file clear
        upload.addFileRejectedListener(event -> {
            Notification notification = Notification.show(
                "File rejected: " + event.getErrorMessage(), 
                5000, 
                Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        });

        // Submit button click listener
        submitButton.addClickListener(event -> {
            submitButton.setEnabled(false);
            submitButton.setText("Analyzing...");
            submitButton.setIcon(new Icon(VaadinIcon.HOURGLASS));

            // Simulate processing delay
            submitButton.getUI().ifPresent(ui -> {
                ui.push();
                try {
                    // Simulate processing time
                    Thread.sleep(2000);
                    
                    InputStream fileData = buffer.getInputStream();
                    String fileName = buffer.getFileName();
                    
                    // TODO: Send fileData to your oil spill detection model
                    
                    ui.access(() -> {
                        Notification notification = Notification.show(
                            "Analysis complete! Processing results for " + fileName, 
                            5000, 
                            Notification.Position.BOTTOM_END
                        );
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        
                        submitButton.setText("Analyze Image");
                        submitButton.setIcon(new Icon(VaadinIcon.SEARCH));
                        submitButton.setEnabled(true);
                        
                        // Navigate to results page (uncomment when you have a results view)
                        // ui.navigate(ResultsView.class, fileName);
                    });
                } catch (InterruptedException e) {
                    ui.access(() -> {
                        Notification notification = Notification.show(
                            "Error processing image", 
                            3000, 
                            Notification.Position.MIDDLE
                        );
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        
                        submitButton.setText("Analyze Image");
                        submitButton.setIcon(new Icon(VaadinIcon.SEARCH));
                        submitButton.setEnabled(true);
                    });
                }
            });
        });

        // Help text
        Paragraph helpText = new Paragraph("Supported formats: JPEG, PNG • Max size: 10MB");
        helpText.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL, LumoUtility.TextAlignment.CENTER);

        // Add components to card
        card.add(
            title,
            description,
            upload,
            imageContainer,
            submitButton,
            helpText
        );

        // Add card to main layout
        add(card);
    }
}