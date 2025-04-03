package com.project.app.views;

import com.project.app.model.Vessel;
import com.project.app.service.VesselService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "vessel-status", layout = MainLayout.class)
@PageTitle("Update Vessel Anomaly Flag | Your App Name")
public class VesselUpdateView extends VerticalLayout {

    private final VesselService vesselService;

    @Autowired
    public VesselUpdateView(VesselService vesselService) {
        this.vesselService = vesselService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSpacing(false);
        setPadding(true);
        addClassName("vessel-update-view");

        Div card = new Div();
        card.addClassNames("vessel-update-card");
        card.setWidth("400px");
        card.getStyle().set("background-color", "var(--lumo-base-color)");
        card.getStyle().set("padding", "2rem");

        H2 title = new H2("Update Vessel Anomaly Flag");
        title.getStyle().set("margin-bottom", "1rem");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        formLayout.setWidth("100%");

        // Input for MMSI
        TextField mmsiField = new TextField("Vessel MMSI");
        mmsiField.setWidthFull();

        // Dropdown for Anomaly Flag
        ComboBox<String> statusComboBox = new ComboBox<>("Anomaly Flag");
        statusComboBox.setItems("NOT_DEFINED", "UNDERWAY", "MOORED", "ANCHORED", "RESTRICTED", "ANOMALOUS_BEHAVIOR");
        statusComboBox.setWidthFull();
        statusComboBox.setPlaceholder("Select Anomaly Flag");

        // Update Button
        Button updateButton = new Button("Update Status");
        updateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateButton.setWidthFull();
        updateButton.getStyle().set("margin-top", "1.5rem");

        // Button click listener for updating the status
        updateButton.addClickListener(event -> {
            String mmsi = mmsiField.getValue();
            String status = statusComboBox.getValue();

            if (mmsi != null && !mmsi.isEmpty() && status != null) {
                try {
                    Vessel updatedVessel = vesselService.updateVesselStatus(mmsi, status);
                    Notification notification = Notification.show("Vessel status updated successfully!");
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    notification.setPosition(Notification.Position.TOP_CENTER);
                    UI.getCurrent().navigate("vessel-status");
                } catch (Exception e) {
                    Notification notification = Notification.show("Error updating vessel status: " + e.getMessage());
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setPosition(Notification.Position.TOP_CENTER);
                }
            } else {
                Notification notification = Notification.show("Please fill in all fields correctly");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.TOP_CENTER);
            }
        });

        formLayout.add(mmsiField, statusComboBox, updateButton);
        card.add(title, formLayout);
        add(card);

        getStyle().set("background", "linear-gradient(135deg, var(--lumo-primary-color-10pct), var(--lumo-primary-color-50pct))");
    }
}
