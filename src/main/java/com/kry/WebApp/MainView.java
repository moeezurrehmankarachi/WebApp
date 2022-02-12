package com.kry.WebApp;

import com.kry.WebApp.domainobject.ExternalService;
import com.kry.WebApp.components.ServiceAddEditForm;
import com.kry.WebApp.components.ServiceGrid;
import com.kry.WebApp.service.ExternalServiceService;
import com.kry.WebApp.utilities.UIUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Route
@PWA(name = "Kry Vaadin Application", shortName = "Kry Vaadin App", description = "This is the solution of technical task by Kry", enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout implements BeforeLeaveObserver {
    private ExternalServiceService externalServiceService;
    private ServiceGrid grid;
    private Dialog parentDialog;
    private Registration registration;
    private Logger logger = LoggerFactory.getLogger(MainView.class);


    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        if (registration != null) {
            registration.remove();
        }
    }

    public MainView(@Autowired ExternalServiceService externalServiceService, @Value("${poller.time}") Integer pollerTime) {
        this.setHeightFull();
        this.setWidthFull();
        this.externalServiceService = externalServiceService;
        makeParentDialog();

        add(makeHeader("Kry Technical Task", "dark"));
        add(makeAddServiceButton());
        add(makeHeader("All Services", "light"));
        add(makeGrid());
        grid.fetchAndSetItems();

        UI.getCurrent().setPollInterval(pollerTime);
        registration = UI.getCurrent().addPollListener(e -> {
            logger.info("Poll Event: ");
            grid.fetchAndSetItems();
        });
    }

    public Component makeHeader(String text, String themeType) {
        H1 header = new H1(text);
        header.getElement().getThemeList().add(themeType);
        header.getStyle().set("margin", "0").set("padding", "16px").set("width", "100%");
        return header;
    }

    public Component makeGrid() {
        grid = new ServiceGrid(externalServiceService);
        return grid;
    }

    public Component makeAddServiceButton() {
        Button button = UIUtils.createSuccessPrimaryButton("Add Service", VaadinIcon.ADD_DOCK);
        button.addClickListener(buttonClickEvent -> {
            this.parentDialog.removeAll();
            this.parentDialog.add(new ServiceAddEditForm(new ExternalService(), this.externalServiceService, this.grid, this.parentDialog));
            this.parentDialog.setOpened(true);
        });
        return button;
    }

    public void makeParentDialog(){
        this.parentDialog = new Dialog();
        this.parentDialog.setCloseOnEsc(true);
        this.parentDialog.setCloseOnOutsideClick(true);
    }
}
