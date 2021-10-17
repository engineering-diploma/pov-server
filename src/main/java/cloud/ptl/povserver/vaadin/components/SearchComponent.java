package cloud.ptl.povserver.vaadin.components;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.service.download.DownloadCallback;
import cloud.ptl.povserver.service.resource.ResourceService;
import cloud.ptl.povserver.service.search.SearchService;
import cloud.ptl.povserver.vaadin.utils.SearchComponentListener;
import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.content.Item;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;

public class SearchComponent extends VerticalLayout {
    private final SearchService searchService;
    private final ResourceService resourceService;
    private final SearchComponentListener searchComponentListener;

    private ProgressBar progressBar;
    private Label progressLabel;
    private TextField searchTextField;

    private UI ui;

    public SearchComponent(SearchService searchService, UI ui, ResourceService resourceService, SearchComponentListener searchComponentListener) {
        this.searchComponentListener = searchComponentListener;
        this.searchService = searchService;
        this.resourceService = resourceService;
        this.ui = ui;
        this.generateLayout();
    }

    private void generateLayout() {
        add(this.createSearchBar());
        add(this.createSpinner());
    }

    private Component createSearchBar() {
        VerticalLayout vl = new VerticalLayout();
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        this.searchTextField = new TextField();
        this.searchTextField.setSizeFull();
        Button button = new Button("Search");
        button.setWidth("100px");
        button.addClickListener(
                l -> this.downloadVideo()
        );
        hl.add(this.searchTextField);
        hl.add(button);
        Label label = new Label("Video / GIF URL");
        vl.add(label);
        vl.add(hl);
        return vl;
    }

    private Component createSpinner() {
        VerticalLayout verticalLayout = new VerticalLayout();
        this.progressBar = new ProgressBar(0, 100);
        this.progressBar.setValue(0);
        verticalLayout.add(this.progressBar);
        this.progressLabel = new Label("");
        verticalLayout.add(this.progressLabel);
        return verticalLayout;
    }

    private RippleClickableCard createCard(ResourceDAO resourceDAO) {
        HorizontalLayout hl = new HorizontalLayout();
        Image image = new Image(resourceDAO.getThumbnailUrls().get(0), "an image");
        Item item = new Item(resourceDAO.getTitle(), resourceDAO.getDescription());
        hl.add(image);
        hl.add(item);
        RippleClickableCard rippleClickableCard = new RippleClickableCard(hl);
//        ContextMenu contextMenu = new ContextMenu();
//        contextMenu.setTarget(rippleClickableCard);
//        contextMenu.addItem("Delete", e -> Notification.show("Deleted"));
//        contextMenu.addItem("Move Up", e -> Notification.show("Move up"));
//        contextMenu.addItem("Move Down", e -> Notification.show("Moved Down"));
//        add(contextMenu);
        rippleClickableCard.setId("result-card");
        return rippleClickableCard;
    }

    private void downloadVideo() {
        this.progressLabel.setText("Starting download...");
        this.progressBar.setValue(10);
        String link = this.searchTextField.getValue();
        try {
            this.searchService.findResourceByLink(link, new DownloadCallback() {
                @Override
                public void onDownload(int progress) {
                    float progressPercent = (float) progress / 100;
                    ui.access(() -> {
                        progressBar.setValue(10 + 80 * progressPercent);
                        progressLabel.setText("Dwonloading... " + progress + "%");
                    });
                }

                @Override
                public void onFinished(ResourceDAO resourceDAO) {
                    ui.access(() -> {
                        progressLabel.setText("Finished");
                        progressBar.setValue(100);
                        getChildren()
                                .filter(el -> el.getId().orElse("false").equals("result-card"))
                                .findFirst()
                                .ifPresent(component -> remove(component));
                        ;

                        add(createCard(resourceDAO));
                        searchComponentListener.onUpdate();
                    });
                }

                @Override
                public void onError(Throwable throwable) {
                    ui.access(() -> {
                        progressBar.setValue(0);
                        progressLabel.setText("Error occurred");
                    });
                }
            });
        } catch (Exception e) {
            Notification.show("Something wrong happen during download: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
