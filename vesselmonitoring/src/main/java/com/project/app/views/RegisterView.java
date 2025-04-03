package com.project.app.views;

import com.project.app.model.User;
import com.project.app.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;

@Route("register")
@PageTitle("Register | Your App Name")
public class RegisterView extends VerticalLayout {
    private final UserService userService;

    @Autowired
    public RegisterView(UserService userService) {
        this.userService = userService;
        
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSpacing(false);
        setPadding(true);
        addClassName("register-view");

        Div card = new Div();
        card.addClassNames("register-card", LumoUtility.BoxShadow.MEDIUM, LumoUtility.BorderRadius.LARGE);
        card.setWidth("400px");
        card.getStyle().set("background-color", "var(--lumo-base-color)");
        card.getStyle().set("padding", "2rem");

        H2 title = new H2("Create an Account");
        title.addClassName(LumoUtility.TextAlignment.CENTER);
        title.getStyle().set("margin-bottom", "1rem");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        formLayout.setWidth("100%");

        TextField fname = new TextField("First Name");
        fname.setWidthFull();

        TextField lname = new TextField("Last Name");
        lname.setWidthFull();

        EmailField email = new EmailField("Email");
        email.setPlaceholder("your.email@example.com");
        email.setWidthFull();
        email.setClearButtonVisible(true);

        PasswordField password = new PasswordField("Password");
        password.setPlaceholder("Enter your password");
        password.setWidthFull();
        password.setClearButtonVisible(true);

        PasswordField confirmPassword = new PasswordField("Re-enter Password");
        confirmPassword.setPlaceholder("Re-enter your password");
        confirmPassword.setWidthFull();
        confirmPassword.setClearButtonVisible(true);

        confirmPassword.addValueChangeListener(event -> {
            if (!password.getValue().equals(confirmPassword.getValue())) {
                confirmPassword.setInvalid(true);
                confirmPassword.setErrorMessage("Passwords do not match");
            } else {
                confirmPassword.setInvalid(false);
            }
        });

        Button registerButton = new Button("Register");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidthFull();
        registerButton.getStyle().set("margin-top", "1.5rem");

        Binder<User> binder = new Binder<>(User.class);
        binder.forField(email)
            .withValidator(new EmailValidator("Please enter a valid email address"))
            .bind(User::getEmail, User::setEmail);
        
        binder.forField(password)
            .withValidator(pwd -> pwd.length() >= 8, "Password must be at least 8 characters long")
            .bind(User::getPassword, User::setPassword);
        
        binder.bind(fname, User::getFname, User::setFname);
        binder.bind(lname, User::getLname, User::setLname);

        registerButton.addClickListener(event -> {
            if (!password.getValue().equals(confirmPassword.getValue())) {
                Notification.show("Passwords do not match!", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            if (binder.validate().isOk()) {
                try {
                    User user = new User();
                    user.setFname(fname.getValue());
                    user.setLname(lname.getValue());
                    user.setEmail(email.getValue());
                    user.setPassword(password.getValue());
                    userService.registerUser(user);
                    Notification notification = Notification.show("Registered successfully!");
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    notification.setPosition(Notification.Position.TOP_CENTER);
                    UI.getCurrent().navigate("login");
                } catch (Exception e) {
                    Notification notification = Notification.show("Registration failed: " + e.getMessage());
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.setPosition(Notification.Position.TOP_CENTER);
                }
            } else {
                Notification notification = Notification.show("Please fill in all required fields correctly");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.TOP_CENTER);
            }
        });

        formLayout.add(fname, lname, email, password, confirmPassword, registerButton);
        card.add(title, formLayout);
        add(card);

        getStyle().set("background", "linear-gradient(135deg, var(--lumo-primary-color-10pct), var(--lumo-primary-color-50pct))");
    }
}
