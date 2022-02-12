package com.kry.WebApp.components;

import com.kry.WebApp.domainobject.ExternalService;
import com.kry.WebApp.MainView;
import com.kry.WebApp.service.ExternalServiceMapper;
import com.kry.WebApp.service.ExternalServiceService;
import com.kry.WebApp.utilities.ConfirmationDialog;
import com.kry.WebApp.utilities.UIUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceGrid extends Grid<ExternalService> {
    private ExternalServiceService externalServiceService;
    private ConfirmationDialog confirmationDialog = new ConfirmationDialog();
    private Dialog parentDialog;
    private Logger logger = LoggerFactory.getLogger(MainView.class);


    public ServiceGrid(ExternalServiceService externalServiceService) {
        this.externalServiceService = externalServiceService;
        makeParentDialog();
        this.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT);
        this.setHeightByRows(true);
        this.addColumn(new ComponentRenderer<>(this::makeEditServiceButton)).setHeader("Update Service");
        this.addColumn(ExternalService::getName).setHeader("Name").setResizable(true);
        this.addColumn(ExternalService::getUrl).setHeader("URL").setResizable(true);
        this.addColumn(externalService -> externalService.getCreation_datetime() != null ? UIUtils.getLocalDateTime(externalService.getCreation_datetime()) : "NA").setHeader("Creation Date Time");
        this.addColumn(externalService -> externalService.getUpdate_datetime() != null ? UIUtils.getLocalDateTime(externalService.getUpdate_datetime()) : "NA").setHeader("Update Date Time");
        this.addColumn(externalService -> externalService.getLast_verified_datetime() != null ? UIUtils.getLocalDateTime(externalService.getLast_verified_datetime()) : "NA").setHeader("Verified Date Time");
        this.addColumn(new ComponentRenderer<>(this::makeStatus)).setHeader("Status");
        this.addColumn(new ComponentRenderer<>(this::makeCheckServiceButton)).setHeader("Check Service");
        this.addColumn(new ComponentRenderer<>(this::makeDeleteServiceButton)).setHeader("Delete Service");

        this.setWidthFull();
    }


    public void makeParentDialog() {
        this.parentDialog = new Dialog();
        this.parentDialog.setCloseOnEsc(true);
        this.parentDialog.setCloseOnOutsideClick(true);
    }

    public Component makeEditServiceButton(ExternalService externalService) {
        Button button = UIUtils.createSuccessPrimaryButton("Update", VaadinIcon.USER_CHECK);
        button.addClickListener(buttonClickEvent -> {
            Notification.show("Update Button Clicked!!!", 5000, Notification.Position.TOP_END);

            parentDialog.removeAll();
            parentDialog.add(new ServiceAddEditForm(externalService, this.externalServiceService, this, parentDialog));
            parentDialog.setOpened(true);
        });
        return button;
    }

    public Component makeCheckServiceButton(ExternalService externalService) {
        UI ui = UI.getCurrent().getUI().get();

        Button button = UIUtils.createSuccessPrimaryButton("Check", VaadinIcon.SEARCH);
        button.addClickListener(buttonClickEvent -> ui.access(() -> {
            Notification.show("Check Service Button Clicked!!!", 5000, Notification.Position.TOP_END);

            this.externalServiceService.checkURLAndUpdateService(externalService)
                    .onErrorResume(throwable -> {
                        logger.error("An error occured: " + throwable.getMessage());
                        return null;
                    }).doOnError(throwable ->
                        ui.access(() -> {
                            UIUtils.makeNotification("Unforunately an error have occured!!!", true, 10000);
                        }))
                    .subscribe(externalServiceDTO -> ui.access(() -> {
                        if (externalServiceDTO.getStatus().equals("1")) {
                            UIUtils.makeNotification("Service: " + externalService.getName() + ", Status: OK", false, 10000);
                        } else {
                            UIUtils.makeNotification("Service: " + externalService.getName() + ", Status: Fail", true, 10000);
                        }
//                        this.getDataProvider().refreshAll();
                    }));
        }));
        return button;
    }

    public Component makeDeleteServiceButton(ExternalService externalService) {
        UI ui = UI.getCurrent().getUI().get();
        Button button = UIUtils.createErrorPrimaryButton("Remove", VaadinIcon.CLOSE);
        button.addClickListener(buttonClickEvent -> ui.access(() -> {
            Notification.show("Delete Button Clicked!!!", 5000, Notification.Position.TOP_END);

            this.confirmationDialog = new ConfirmationDialog("Are you sure you want to delete (" + externalService.getName() + ") service?");
            this.confirmationDialog.setOpened(true);
            this.confirmationDialog.addOpenedChangeListener(dialogOpenedChangeEvent -> {
                if (!dialogOpenedChangeEvent.isOpened() && this.confirmationDialog.getAnswer()) {
                    this.externalServiceService.removeServiceCall(externalService.getId() + "")
                            .onErrorResume(throwable -> {
                                logger.error("An error occured: " + throwable.getMessage());
                                return null;
                            }).doOnError(throwable ->
                                ui.access(() -> {
                                    UIUtils.makeNotification("Unforunately an error have occured!!!", true, 10000);
                                }))
                            .subscribe(integer -> ui.access(() -> {
                                if (integer > 0) {
//                                    this.getDataProvider().refreshAll();
                                    this.fetchAndSetItems();
                                }
                            }));
                }
            });
        }));
        return button;
    }

    public Component makeStatus(ExternalService externalService) {
        Icon icon;
        if (externalService.getStatus().equals("1")) {
            icon = new Icon(VaadinIcon.CHECK);
            icon.setColor("green");
            return icon;
        } else {
            icon = new Icon(VaadinIcon.CLOSE);
            icon.setColor("red");
            return icon;
        }
    }

    public void fetchAndSetItems() {
        Notification.show("Refreshing Services Grid!!!", 5000, Notification.Position.TOP_END);
        UI ui = UI.getCurrent().getUI().get();
        this.externalServiceService.fetchAllServices()
                .map(externalServiceDTOS -> ExternalServiceMapper.makeExternalServiceList(externalServiceDTOS))
                .onErrorResume(throwable -> {
                    logger.error("An error occured: " + throwable.getMessage());
                    return null;
                }).doOnError(throwable ->
                    ui.access(() -> {
                        UIUtils.makeNotification("Unforunately an error have occured!!!", true, 10000);
                    }))
                .subscribe(externalServices -> ui.access(() -> {
                    this.setItems(externalServices);
                }));

    }

}
