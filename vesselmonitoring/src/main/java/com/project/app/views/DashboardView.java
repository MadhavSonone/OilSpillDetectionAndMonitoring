package com.project.app.views;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("dashboard")
@AnonymousAllowed
public class DashboardView extends VerticalLayout {
	private static final long serialVersionUID = 1L;

    public DashboardView() {
        Button logoutButton = new Button("Logout", event -> {
            getUI().ifPresent(ui -> ui.getPage().setLocation("logout"));
        });

        add(logoutButton);
    }

}
