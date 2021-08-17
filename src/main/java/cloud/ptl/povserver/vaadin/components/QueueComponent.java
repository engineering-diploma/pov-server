package cloud.ptl.povserver.vaadin.components;

import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.content.Item;
import com.github.appreciated.card.content.VerticalCardComponentContainer;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class QueueComponent extends VerticalLayout {
    public QueueComponent() {
        VerticalCardComponentContainer cardComponentContainer = new VerticalCardComponentContainer();
        Image img = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Mersan.JPG/1920px-Mersan.JPG", "car");
        cardComponentContainer.add(this.createCard("Some title", "Some longer description", img));
        add(cardComponentContainer);
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
