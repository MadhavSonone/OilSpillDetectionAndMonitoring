package com.project.app.views;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.router.RouteAlias;
@Route(value = "dashboard", layout = MainLayout.class)
@JsModule("./leaflet.js")
@CssImport("./leaflet.css")
public class DashboardView extends VerticalLayout {
    private static final long serialVersionUID = 1L;

    public DashboardView() {
        if (VaadinSession.getCurrent().getAttribute("user") == null) {
            UI.getCurrent().navigate("login");
        }

        Div mapContainer = new Div();
        mapContainer.setId("map");
        mapContainer.getStyle().set("width", "100%").set("height", "100vh");
        add(mapContainer);

        String mapId = mapContainer.getId().orElseThrow(() -> new IllegalStateException("Map container ID is missing"));

        // Initialize CartoDB basemap and fetch markers
        getElement().executeJs(
            """
            const map = L.map($0).setView([18.516, 73.856], 3);
            L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="https://carto.com/attributions">CartoDB</a>'
            }).addTo(map);

            const vesselIcon = L.icon({
                iconUrl: 'https://cdn-icons-png.flaticon.com/512/870/870119.png', // Ship icon
                iconSize: [32, 32],
                iconAnchor: [16, 16],
                popupAnchor: [0, -16]
            });

            fetch("http://localhost:9090/api/vessels/all")
              .then(response => response.json())
              .then(data => {
                const limited = data.slice(0, 100); // Limit to first 100 vessels
                limited.forEach(vessel => {
                  const marker = L.marker([vessel.latitude, vessel.longitude], { icon: vesselIcon }).addTo(map);
                  marker.bindPopup(
                    `<b>${vessel.vesselName || 'Unnamed Vessel'}</b><br>` +
                    `MMSI: ${vessel.mmsi}<br>` +
                    `Timestamp: ${vessel.timestamp || 'N/A'}<br>`
                  );
                  marker.on('mouseover', function () { this.openPopup(); });
                  marker.on('mouseout', function () { this.closePopup(); });
                });
              });
            """,
            mapId
        );
    }
}
