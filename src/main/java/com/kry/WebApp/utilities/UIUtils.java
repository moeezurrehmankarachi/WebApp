package com.kry.WebApp.utilities;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UIUtils {
    public static Button createButton(String text, VaadinIcon icon, ButtonVariant... variants) {
        Icon i = new Icon(icon);
        i.getElement().setAttribute("slot", "prefix");
        Button button = new Button(text, i);
        button.addThemeVariants(variants);
        return button;
    }

    public static Button createButton(String text, ButtonVariant... variants) {
        Button button = new Button(text);
        button.addThemeVariants(variants);
        button.getElement().setAttribute("aria-label", text);
        return button;
    }

    public static Button createPrimaryButton(String text) {
        return createButton(text, ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createContrastPrimaryButton(String text) {
        return createButton(text, ButtonVariant.LUMO_CONTRAST,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createErrorPrimaryButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createSuccessPrimaryButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_SUCCESS,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static String getLocalDateTime(LocalDateTime localDateTime){
        if(localDateTime != null){
            return localDateTime.format(DateTimeFormatter.ofPattern("MMM dd, YYYY HH:mm:ss", Locale.ENGLISH));
        }
        return "NA";
    }

    public static Component makeNotification(String message, Boolean error, int duration) {
        Notification notification = new Notification();
        notification.setText(message);
        notification.setDuration(duration);

        if (error) {
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.BOTTOM_END);
        } else {
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.BOTTOM_START);
        }
        notification.open();
        return notification;
    }
}
