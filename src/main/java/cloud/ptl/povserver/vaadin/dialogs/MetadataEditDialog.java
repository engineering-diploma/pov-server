package cloud.ptl.povserver.vaadin.dialogs;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.service.resource.ResourceService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class MetadataEditDialog extends Dialog {
    private final ResourceService resourceService;
    private TextField titleEdit;
    private TextArea descriptionEdit;
    private Button closeButton;
    private ResourceDAO resourceDAO;

    public MetadataEditDialog(ResourceDAO resourceDAO, ResourceService resourceService) {
        this.resourceDAO = resourceDAO;
        this.resourceService = resourceService;

        this.bootstrap();
    }

    public void bootstrap() {
        this.setWidthFull();
        VerticalLayout vl = new VerticalLayout();

        vl.add(
                this.createTitle()
        );
        vl.add(
                this.createDescription()
        );
        vl.add(
                this.createCloseButton()
        );
        this.add(vl);
    }

    public Component createTitle() {
        this.titleEdit = new TextField("Title");
        if (this.resourceDAO.getTitle() != null && !this.resourceDAO.getTitle().isBlank()) {
            this.titleEdit.setValue(
                    this.resourceDAO.getTitle()
            );
        }
        this.titleEdit.setClearButtonVisible(true);
        this.titleEdit.setPlaceholder("new title");
        this.titleEdit.addValueChangeListener(e -> this.resourceDAO.setTitle(e.getValue()));
        this.titleEdit.setWidthFull();
        return this.titleEdit;
    }

    public Component createDescription() {
        this.descriptionEdit = new TextArea("Description");
        if (this.resourceDAO.getDescription() != null && !this.resourceDAO.getDescription().isBlank()) {
            this.descriptionEdit.setValue(this.resourceDAO.getDescription());
        }
        this.descriptionEdit.setPlaceholder("new description");
        this.descriptionEdit.addValueChangeListener(e -> this.resourceDAO.setDescription(e.getValue()));
        this.descriptionEdit.setWidthFull();
        return this.descriptionEdit;
    }

    public Component createCloseButton() {
        this.closeButton = new Button("Save");
        this.closeButton.addClickListener(this::save);
        return this.closeButton;
    }

    private void save(ClickEvent<Button> buttonClickEvent) {
        this.resourceService.save(
                this.resourceDAO
        );
        this.close();
    }
}
