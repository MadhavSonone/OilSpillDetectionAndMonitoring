package com.project.app.views;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

	private static final long serialVersionUID = 1L;
    public MainLayout() {
        createNavbar();
        createDrawer();
    }

    private void createNavbar() {

        H1 title = new H1("Vessel Monitoring");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)");
        title.getStyle().set("margin", "0");

        HorizontalLayout navbar = new HorizontalLayout(new DrawerToggle(), title);
        navbar.setWidthFull();
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);	
        navbar.addClassNames(LumoUtility.Padding.SMALL);
        addToNavbar(navbar);
    }

    private void createDrawer() {
        // Create tabs for navigation
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(
            createTab(VaadinIcon.DASHBOARD, "Dashboard", DashboardView.class),
            createTab(VaadinIcon.FILE_PICTURE, "Upload", ImageUploadView.class),
            //createTab(VaadinIcon.MAP_MARKER, "Tracking", Track.class),
            //createTab(VaadinIcon.BELL, "Alerts", "alerts"),
            //createTab(VaadinIcon.COG, "AboutUs", AboutUs.class),
            createTab(VaadinIcon.SIGN_OUT, "Logout", LoginView.class)
        );
        
        addToDrawer(tabs);
    }

    private Tab createTab(VaadinIcon viewIcon, String viewName, Class<? extends Component> viewClass) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("margin-inline-end", "var(--lumo-space-s)");
        
        RouterLink link = new RouterLink();
        link.add(icon, new Span(viewName));
        link.setRoute(viewClass);
        link.setTabIndex(-1);
        
        return new Tab(link);
    }
}