package com.kry.WebApp.utilities;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ConfirmationDialog extends Dialog {
    private Button confirm;
    private Button cancel ;
    public Boolean answer = false;

    public ConfirmationDialog() {
    }

    public ConfirmationDialog(String text) {
        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(true);
        this.setMinWidth("300px");

        VerticalLayout verticalLayout = new VerticalLayout();

        confirm = UIUtils.createSuccessPrimaryButton("Confirm", VaadinIcon.CHECK);
        confirm.addClickListener(e -> {
            answer = true;
            this.close();
        });

        cancel = UIUtils.createErrorPrimaryButton("Cancel", VaadinIcon.CLOSE);
        cancel.addClickListener(e -> {
            this.close();
        });

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.add(confirm, cancel);
        buttonsLayout.setSpacing(true);

        verticalLayout.add(new Label(text), buttonsLayout);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, buttonsLayout);

         this.add(verticalLayout);
         this.setOpened(false);
    }

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }
}
