package com.kry.WebApp.components;

import com.kry.WebApp.datatransferobjects.ExternalServiceDTO;
import com.kry.WebApp.service.ExternalServiceMapper;
import com.kry.WebApp.service.ExternalServiceService;
import com.kry.WebApp.domainobject.ExternalService;
import com.kry.WebApp.utilities.ConfirmationDialog;
import com.kry.WebApp.utilities.LumoStyles;
import com.kry.WebApp.utilities.UIUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServiceAddEditForm extends FormLayout {
    private Logger logger = LoggerFactory.getLogger(ServiceAddEditForm.class);
    private ExternalServiceService externalServiceService;
    public ExternalService externalService;
    private ServiceGrid serviceGrid;

    private TextField name = new TextField("Name");
    private TextField url = new TextField("Url");
    public Binder<ExternalService> binder = new Binder<>(ExternalService.class);
    private ConfirmationDialog confirmationDialog = new ConfirmationDialog();
    private Dialog parentDialog;
    private Boolean forceSave = false;
    private Boolean hasId;

    public ServiceAddEditForm(ExternalService externalService, ExternalServiceService externalServiceService, ServiceGrid serviceGrid, Dialog parentDialog) {
        this.externalService = externalService;
        this.externalServiceService = externalServiceService;
        this.serviceGrid = serviceGrid;
        this.parentDialog = parentDialog;
        this.hasId = externalService.getId() != null ? true : false;

        initiateFormFields();
        initiateBinder();
        initiateForm();
    }

    private void initiateFormFields() {
        url.setMinWidth("400px");
        name.setMinWidth("400px");
    }

    private void initiateBinder() {
        this.binder.forField(name)
                .withValidator(name -> name != null && !name.equals(""), "Kindly provide name")
                .bind(ExternalService::getName, ExternalService::setName);

        this.binder.forField(url)
                .withValidator(url -> url != null && !url.equals(""), "Kindly provide url")
                .withValidator(url -> {
                    if (this.externalServiceService.confirmURLRequest(url) || forceSave) {
                        return true;
                    }
                    return false;
                }, "URL not OK. Are you sure you want to store URL? Click save again to store!!!")
                .bind(ExternalService::getUrl, ExternalService::setUrl);

        this.binder.readBean(this.externalService);
    }

    private void initiateForm() {
        H4 formHeader = new H4(hasId ? "Edit Service" : "Add Service");
        formHeader.getElement().getThemeList().add("light");
        formHeader.getStyle().set("margin", "0").set("padding", "16px").set("width", "100%");

        if (!hasId) {
            H6 formSubHeader = new H6("Press esc to close the form");
            formSubHeader.getElement().getThemeList().add("light");
            formSubHeader.getStyle().set("margin", "0").set("padding", "16px").set("width", "100%");
            this.add(new HorizontalLayout(formHeader, formSubHeader));
        } else {
            this.add(new HorizontalLayout(formHeader));
        }

        this.setMaxWidth("500px");
        this.getStyle().set("border", "2px solid grey").set("padding", "16px").set("border-radius", "25px");

        this.addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.XL);
        this.setResponsiveSteps(
                new ResponsiveStep("0", 1,
                        ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("600px", 1,
                        ResponsiveStep.LabelsPosition.TOP),
                new ResponsiveStep("1024px", 1,
                        ResponsiveStep.LabelsPosition.TOP));

        this.add(name, 1);
        this.add(url, 1);

        Button save = UIUtils.createSuccessPrimaryButton("Save", VaadinIcon.ENTER);
        Button cancel = UIUtils.createContrastPrimaryButton("Cancel");
        UI ui = UI.getCurrent().getUI().get();

        save.addClickListener(e -> {
            try {
                forceSave = true;
                if (this.binder.isValid()) {
                    this.binder.writeBean(this.externalService);
                    makeSave();
                    forceSave = false;
                } else {
                    BinderValidationStatus<ExternalService> validate = this.binder.validate();
                    String errorText = validate.getFieldValidationStatuses()
                            .stream().filter(BindingValidationStatus::isError)
                            .map(BindingValidationStatus::getMessage)
                            .map(Optional::get).distinct()
                            .collect(Collectors.joining(", "));
                    logger.info("Validation Error: " + errorText);
                }
            } catch (Exception ex) {
                this.logger.error("Error add/update: \n" + ExceptionUtils.getStackTrace(ex));
            }
        });
//
        cancel.addClickListener(e -> ui.access(() -> {
            this.binder.readBean(this.hasId ? this.externalService : new ExternalService());
            this.parentDialog.close();
        }));
        // Button bar
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        this.add(actions, 4);
    }

    public void makeSave() {
        if (this.hasId) {
            this.confirmationDialog = new ConfirmationDialog("Are you sure you want to update(" + externalService.getName() + ") service?");
            this.externalService.setUpdate_datetime(LocalDateTime.now());
            executeSave();
//            this.binder.readBean(new ExternalService());
        } else {
            this.confirmationDialog = new ConfirmationDialog("Are you sure you want to add(" + externalService.getName() + ") service?");
            this.externalService.setCreation_datetime(LocalDateTime.now());
            this.externalService.setStatus("0");
            executeSave();
            this.binder.readBean(new ExternalService());
        }
    }

    public void executeSave() {
        UI ui = UI.getCurrent().getUI().get();
        this.confirmationDialog.setOpened(true);
        this.confirmationDialog.addOpenedChangeListener(dialogOpenedChangeEvent -> {
            if (!dialogOpenedChangeEvent.isOpened() && this.confirmationDialog.getAnswer()) {
                UIUtils.makeNotification("Initiating add/update service request !!!", false, 10000);
                this.externalServiceService.addService(ExternalServiceMapper.makeExternalServiceDTO(externalService))
                        .onErrorResume(throwable -> {
                            logger.error("An error occured: " + throwable.getMessage());
                            return null;
                        }).doOnError(throwable -> ui.access(() -> {
                            UIUtils.makeNotification("Unforunately an error have occured!!!", true, 10000);
                        }))
                        .subscribe(externalServiceDTO -> ui.access(() -> {
                            logger.info("Updated service: " + externalServiceDTO + " , Refreshing Grid!!!");
                            serviceGrid.getDataProvider().refreshAll();
                            UIUtils.makeNotification("Service Updated and Refreshing Grid !!!", false, 10000);
                            this.parentDialog.close();
                        }));
            }
        });
    }

}
