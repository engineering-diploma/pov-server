package cloud.ptl.povserver.vaadin.components;

import cloud.ptl.povserver.amqp.RabbitSender;
import cloud.ptl.povserver.amqp.converter.ResourceDAO2VideoPingMessageConverter;
import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.service.queue.QueueService;
import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.content.Item;
import com.github.appreciated.card.content.VerticalCardComponentContainer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

public class QueueComponent extends VerticalLayout {
    private final QueueService queueService;
    private final UI ui;
    private final RabbitSender rabbitSender;

    public QueueComponent(QueueService queueService, UI ui, RabbitSender rabbitSender) {
        this.ui = ui;
        this.queueService = queueService;
        this.rabbitSender = rabbitSender;

        this.init();
    }

    @Transactional
    public void init() {
        getChildren().forEach(this::remove);
        add(this.createResourceList());
    }

    private RippleClickableCard createCard(ResourceDAO resourceDAO) {
        HorizontalLayout hl = new HorizontalLayout();
        Item item = new Item(resourceDAO.getTitle(), resourceDAO.getDescription());
        Image image = new Image(resourceDAO.getThumbnailUrls().get(0), "an image");
        image.setMaxWidth(200, Unit.PIXELS);
        hl.add(image);
        hl.add(item);
        RippleClickableCard rippleClickableCard = new RippleClickableCard(hl);
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(rippleClickableCard);
        contextMenu.addItem("Delete", e -> {
            ui.access(() -> {
                Notification.show("Deleted");
                queueService.deleteResource(resourceDAO);
                init();
            });
        });
        contextMenu.addItem("Move Up", e -> {
            ui.access(() -> {
                Notification.show("Move up");
                queueService.moveResourceUp(resourceDAO);
                init();
            });
        });
        contextMenu.addItem("Move Down", e -> {
            ui.access(() -> {
                Notification.show("Moved Down");
                queueService.moveResourceDown(resourceDAO);
                init();
            });
        });
        contextMenu.addItem("Play", e -> {
            rabbitSender.pingAboutNewVideo(
                    ResourceDAO2VideoPingMessageConverter.convert(resourceDAO)
            );
            Notification.show("Send to play");
        });
        add(contextMenu);
        return rippleClickableCard;
    }

    private Component createResourceList() {
        List<ResourceDAO> resources = (List<ResourceDAO>) this.queueService.findAllResources();
        resources.sort(Comparator.comparingLong(ResourceDAO::getOrderr));
        VerticalCardComponentContainer container = new VerticalCardComponentContainer();
        resources.forEach(el -> {
            container.add(this.createCard(el));
        });
        return container;
    }
}
