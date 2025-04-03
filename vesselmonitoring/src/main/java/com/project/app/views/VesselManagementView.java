package com.project.app.views;

import com.project.app.model.UserVessel;
import com.project.app.model.Vessel;
import com.project.app.service.UserVesselService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.html.Span;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "vessels", layout = MainLayout.class)
@PageTitle("Vessel Management")
public class VesselManagementView extends VerticalLayout {

    private final UserVesselService userVesselService;
    private Grid<UserVessel> vesselGrid = new Grid<>(UserVessel.class); // Display UserVessel (mmsi and email)
    private Map<String, String> statusColorMap = new HashMap<>();
    private Div statusUpdateDiv = new Div();

    public VesselManagementView(UserVesselService userVesselService) {
        this.userVesselService = userVesselService;

        // Initialize status colors
        statusColorMap.put("MOORED", "#4CAF50");     // Green
        statusColorMap.put("UNDERWAY", "#2196F3");   // Blue
        statusColorMap.put("ANCHORED", "#FF9800");   // Orange
        statusColorMap.put("NOT_DEFINED", "#9E9E9E"); // Grey
        statusColorMap.put("ANOMALOUS_BEHAVIOR", "#F44336"); // Red

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(createMainContent());

        // Initial data load
        updateVesselGrid();
    }

    private Component createMainContent() {
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setSizeFull();
        mainContent.setSpacing(true);

        // Configure vessel grid
        vesselGrid.setSizeFull();

        // Set columns to display lat, lon, name, and status
        vesselGrid.addColumn(new ComponentRenderer<>(userVessel -> {
            // Fetch vessel details using MMSI from UserVesselService
            Vessel vessel = userVesselService.getVesselByMmsi(userVessel.getMmsi());
            if (vessel != null) {
                // Display vessel name
                return new Span(vessel.getName()); // Display vessel name
            }
            return new Span("Unknown");
        })).setHeader("Vessel Name");

        vesselGrid.addColumn(new ComponentRenderer<>(userVessel -> {
            // Fetch vessel details using MMSI from UserVesselService
            Vessel vessel = userVesselService.getVesselByMmsi(userVessel.getMmsi());
            if (vessel != null) {
                // Display vessel latitude
                return new Span(String.valueOf(vessel.getLatitude())); // Display vessel latitude
            }
            return new Span("Unknown");
        })).setHeader("Latitude");

        vesselGrid.addColumn(new ComponentRenderer<>(userVessel -> {
            // Fetch vessel details using MMSI from UserVesselService
            Vessel vessel = userVesselService.getVesselByMmsi(userVessel.getMmsi());
            if (vessel != null) {
                // Display vessel longitude
                return new Span(String.valueOf(vessel.getLongitude())); // Display vessel longitude
            }
            return new Span("Unknown");
        })).setHeader("Longitude");

        // Add the status column back
        vesselGrid.addColumn(new ComponentRenderer<>(userVessel -> {
            // Fetch vessel details using MMSI from UserVesselService
            Vessel vessel = userVesselService.getVesselByMmsi(userVessel.getMmsi());
            if (vessel != null) {
                String status = vessel.getStatus(); // Get vessel status
                String color = statusColorMap.getOrDefault(status, "#9E9E9E");

                Span statusBadge = new Span(status);
                statusBadge.getStyle()
                        .set("background-color", color)
                        .set("color", "white")
                        .set("padding", "5px 10px")
                        .set("border-radius", "3px");

                return statusBadge;
            }
            return new Span("Unknown");
        })).setHeader("Status");

        // Layout setup (Left: Vessel List, Right: Add Vessel Form)
        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.add(new H3("Tracked Vessels"));
        leftLayout.add(vesselGrid);
        leftLayout.setFlexGrow(3);

        // Right-side add vessel form setup
        VerticalLayout rightLayout = new VerticalLayout();
        rightLayout.setSpacing(true);
        rightLayout.setPadding(true);
        rightLayout.setWidth("300px");
        rightLayout.getStyle().set("background-color", "#f5f5f5");
        rightLayout.getStyle().set("border-radius", "5px");
        rightLayout.getStyle().set("padding", "20px");

        H3 formTitle = new H3("Add Vessel to Track");
        TextField mmsiField = new TextField("MMSI Number");
        mmsiField.setPlaceholder("Enter vessel MMSI...");
        mmsiField.setWidthFull();

        Button addButton = new Button("Add to Tracking", e -> {
            String mmsi = mmsiField.getValue();
            if (mmsi == null || mmsi.trim().isEmpty()) {
                Notification.show("Please enter a valid MMSI number",
                        3000, Notification.Position.BOTTOM_CENTER);
                return;
            }
            addVesselToTracking(mmsi);
        });
        addButton.setWidthFull();

        // Additional features
        Button refreshButton = new Button("Refresh Data", e -> updateVesselGrid());
        refreshButton.setWidthFull();

        rightLayout.add(formTitle, mmsiField, addButton, new Div(), refreshButton);

        mainContent.add(leftLayout, rightLayout);
        return mainContent;
    }

    private void updateVesselGrid() {
        String userEmail = (String) VaadinSession.getCurrent().getAttribute("user_email");

        if (userEmail != null && !userEmail.isEmpty()) {
            // Fetch the vessels tracked by the current user
            List<UserVessel> trackedVessels = userVesselService.getVesselsTrackedByUser(userEmail);

            if (trackedVessels.isEmpty()) {
                Notification.show("No vessels found for the user", 3000, Notification.Position.BOTTOM_CENTER);
            }

            vesselGrid.setItems(trackedVessels);
        } else {
            Notification.show("You need to be logged in to view your tracked vessels.",
                    3000, Notification.Position.BOTTOM_CENTER);
        }
    }

    private void addVesselToTracking(String mmsi) {
        String userEmail = (String) VaadinSession.getCurrent().getAttribute("user_email");

        if (userEmail == null || userEmail.isEmpty()) {
            Notification.show("You need to be logged in to track vessels.",
                    3000, Notification.Position.BOTTOM_CENTER);
            return;
        }

        try {
            userVesselService.addVesselToUser(userEmail, mmsi);
            Notification.show("Vessel added to your tracking list.", 3000, Notification.Position.BOTTOM_CENTER);
        } catch (RuntimeException e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.BOTTOM_CENTER);
        }

        updateVesselGrid();
    }
}
