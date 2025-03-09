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
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.html.Div;

@PageTitle("Login | Vessel Tracking")
@Route("login")
@CssImport(value = "./themes/mytheme/loginview.css")
public class LoginView extends Div  {
	private static final long serialVersionUID = 1L;

    public LoginView() {
    	LoginOverlay loginOverlay = new LoginOverlay();
        add(loginOverlay);
        loginOverlay.setOpened(true);
    }
}