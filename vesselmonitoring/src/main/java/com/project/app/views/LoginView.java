package com.project.app.views;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.UI;
import com.project.app.model.Login;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.data.binder.*;
import com.project.app.config.PasswordValidator;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("login")
@PageTitle("Login | Your App Name")
public class LoginView extends VerticalLayout {
    private static final long serialVersionUID = 1L;

    public LoginView() {

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSpacing(false);
        setPadding(true);
        addClassName("login-view");
        
        Div card = new Div();
        card.addClassNames("login-card", LumoUtility.BoxShadow.MEDIUM, LumoUtility.BorderRadius.LARGE);
        card.setWidth("400px");
        card.getStyle().set("background-color", "var(--lumo-base-color)");
        card.getStyle().set("padding", "2rem");
        

        
        VerticalLayout header = new VerticalLayout();
        header.setSpacing(false);
        header.setPadding(false);
        header.setAlignItems(Alignment.CENTER);
        
        Icon lockIcon = VaadinIcon.LOCK.create();
        lockIcon.setSize("2rem");
        lockIcon.setColor("var(--lumo-primary-color)");
        
        H2 title = new H2("Welcome Back");
        title.addClassName(LumoUtility.TextAlignment.CENTER);
        title.getStyle().set("margin-top", "0.5rem");
        title.getStyle().set("margin-bottom", "0");
        
        Div subtitle = new Div();
        subtitle.setText("Sign in to continue to your account");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER);
        subtitle.getStyle().set("margin-bottom", "1.5rem");
        
        header.add(lockIcon, title, subtitle);


        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        formLayout.setWidth("100%");
        
        EmailField email = new EmailField();
        email.setLabel("Email");
        email.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        email.setPlaceholder("your.email@example.com");
        email.setWidthFull();
        email.setAutofocus(true);
        email.setClearButtonVisible(true);
        
        PasswordField password = new PasswordField();
        password.setLabel("Password");
        password.setPrefixComponent(VaadinIcon.KEY.create());
        password.setPlaceholder("Enter your password");
        password.setWidthFull();
        password.setClearButtonVisible(true);
        
        Button loginButton = new Button("Sign In");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidthFull();
        loginButton.getStyle().set("margin-top", "1.5rem");
        loginButton.getStyle().set("margin-bottom", "1rem");
        
        Binder<Login> binder = new Binder<>(Login.class);
        binder.forField(email)
            .withValidator(new EmailValidator("Please enter a valid email address"))
            .bind(Login::getEmail, Login::setEmail);
        
        binder.forField(password)
            .withValidator(
                pwd -> PasswordValidator.isValid(pwd), 
                "Password must contain at least 8 characters, including uppercase, lowercase, and a special character"
            )
            .bind(Login::getPassword, Login::setPassword);
        
        Login user = new Login();
        binder.setBean(user);
        
        loginButton.addClickListener(event -> {
            if (binder.validate().isOk()) {
                Notification notification = Notification.show("Signed in successfully!");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.TOP_CENTER);
                
                VaadinSession.getCurrent().setAttribute("user", user);
                UI.getCurrent().navigate(DashboardView.class);
            } else {
                Notification notification = Notification.show("Please check your credentials");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.TOP_CENTER);
            }
        });
        
        formLayout.add(email, password, loginButton);
        
        // Footer with links
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.BETWEEN);
        footer.setPadding(false);
        footer.setMargin(false);
        footer.getStyle().set("margin-top", "0.5rem");
        
        Anchor forgotPassword = new Anchor("forgot-password", "Forgot Password?");
        forgotPassword.getStyle().set("color", "var(--lumo-primary-color)");
        forgotPassword.getStyle().set("text-decoration", "none");
        forgotPassword.getStyle().set("font-size", "var(--lumo-font-size-s)");
        
        Anchor register = new Anchor("register", "Create Account");
        register.getStyle().set("color", "var(--lumo-primary-color)");
        register.getStyle().set("text-decoration", "none");
        register.getStyle().set("font-size", "var(--lumo-font-size-s)");
        
        footer.add(forgotPassword, register);
        
        card.add(header, formLayout, footer);
        
        add(card);
        
        getStyle().set("background", "linear-gradient(135deg, var(--lumo-primary-color-10pct), var(--lumo-primary-color-50pct))");
    }
}