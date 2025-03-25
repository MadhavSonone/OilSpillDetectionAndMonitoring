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
@RouteAlias(value = "", layout = MainLayout.class)
@JsModule("./leaflet.js") 
@CssImport("./leaflet.css") 
@AnonymousAllowed
public class DashboardView extends VerticalLayout {
	private static final long serialVersionUID = 1L;

    public DashboardView()  {
    	
        if (VaadinSession.getCurrent().getAttribute("user") == null) {
            UI.getCurrent().navigate("login");
        }
        
        Div mapContainer = new Div();
        mapContainer.setId("map");
        mapContainer.getStyle().set("width", "100%").set("height", "100vh");
        add(mapContainer);
        
        
        String mapId = mapContainer.getId().orElseThrow(() -> new IllegalStateException("Map container ID is missing"));
        getElement().executeJs(
        	    "const map = L.map($0).setView([18.516, 73.856], 13); " +
        	    "L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png', {" +
        	    "    attribution: '&copy; <a href=\"https://carto.com/attributions\">CartoDB</a> contributors'" +
        	    "}).addTo(map);",
        	mapId);




    }
}
