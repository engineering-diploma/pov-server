package cloud.ptl.povserver.vaadin.dialogs;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.service.resource.ResourceService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;

public class MetadataEditDialog extends Dialog {
    private final ResourceService resourceService;
    private TextField titleEdit;
    private Button closeButton;
    private ResourceDAO resourceDAO;

    public MetadataEditDialog(ResourceDAO resourceDAO, ResourceService resourceService) {
        this.resourceDAO = resourceDAO;
        this.resourceService = resourceService;

        this.bootstrap();
    }

    public void bootstrap() {
        this.add(
                this.createTitle()
        );
        this.add(
                this.createCloseButton()
        );
    }

    public Component createTitle() {
        this.titleEdit = new TextField("Title");
        if (this.resourceDAO.getTitle() != null && !this.resourceDAO.getTitle().isBlank()) {
            this.titleEdit.setValue(
                    this.resourceDAO.getTitle()
            );
        }
        this.titleEdit.setClearButtonVisible(true);
        this.titleEdit.setPlaceholder("New title");
        this.titleEdit.addValueChangeListener(e -> this.resourceDAO.setTitle(e.getValue()));
        return this.titleEdit;
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
