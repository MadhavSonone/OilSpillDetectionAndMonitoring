package com.project.app.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Login | Vessel Tracking")
@Route("login")
@AnonymousAllowed
@CssImport(value = "./themes/mytheme/loginview.css")
public class LoginView extends VerticalLayout {
	private static final long serialVersionUID = 1L;

    public LoginView() {
        addClassName("login-container");

        H2 title = new H2("Welcome Back!");
        title.addClassName("login-title");

        TextField email = new TextField("Email");
        email.addClassName("input-field");

        PasswordField password = new PasswordField("Password");
        password.addClassName("input-field");

        Button loginButton = new Button("Login");
        loginButton.addClassName("login-button");

        Anchor forgotPassword = new Anchor("#", "Forgot Password?");
        forgotPassword.addClassName("forgot-password");
        Anchor register = new Anchor("register", "New user? ");
        register.addClassName("new-user");

        HorizontalLayout others = new HorizontalLayout();
        others.add(forgotPassword);
        others.add(register);
 
        VerticalLayout formLayout = new VerticalLayout(title, email, password, loginButton, others);
        formLayout.addClassName("login-form");

        add(formLayout);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private void login(String email, String password) {
        if (email.equals("admin@example.com") && password.equals("password")) {
            getUI().ifPresent(ui -> ui.navigate("dashboard")); // Navigate to dashboard if login is correct
        } else {
            System.out.println("Invalid credentials!"); // Replace with proper Vaadin notification
        }
    }
}