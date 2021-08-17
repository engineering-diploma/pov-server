package cloud.ptl.povserver.vaadin.components;

import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.content.Item;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;


public class SearchComponent extends VerticalLayout {
    public SearchComponent() {
        VerticalLayout vl = new VerticalLayout();
        add(this.createSearchBar());
        add(this.createSpinner());
        Image img = new Image("https://cdn.motor1.com/images/mgl/ybYwo/s1/2019-cupra-formentor-concept.webp", "car");
        add(this.createCard("Resulting data", "", img));
    }

    private Component createSearchBar() {
        VerticalLayout vl = new VerticalLayout();
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        TextField textField = new TextField();
        textField.setSizeFull();
        Button button = new Button("Search");
        button.setWidth("100px");
        hl.add(textField);
        hl.add(button);
        Label label = new Label("Video / GIF URL");
        vl.add(label);
        vl.add(hl);
        return vl;
    }

    private Component createSpinner() {
        VerticalLayout verticalLayout = new VerticalLayout();
        ProgressBar progressBar = new ProgressBar(0, 100);
        progressBar.setValue(20);
        verticalLayout.add(progressBar);
        Label progress = new Label("Your data is being downloaded...");
        verticalLayout.add(progress);
        return verticalLayout;
    }

    private RippleClickableCard createCard(String title, String description, Image image) {
        HorizontalLayout hl = new HorizontalLayout();
        Item item = new Item(title, description);
        hl.add(image);
        image.setHeight("7%");
        image.setWidth("15%");
        hl.add(item);
        RippleClickableCard rippleClickableCard = new RippleClickableCard(hl);
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(rippleClickableCard);
        contextMenu.addItem("Delete", e -> Notification.show("Deleted"));
        contextMenu.addItem("Move Up", e -> Notification.show("Move up"));
        contextMenu.addItem("Move Down", e -> Notification.show("Moved Down"));
        add(contextMenu);
        return rippleClickableCard;
    }
}
