package cloud.ptl.povserver.vaadin;


import cloud.ptl.povserver.service.search.SearchService;
import cloud.ptl.povserver.vaadin.utils.TabNameToContentMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;

@Route(value = "/", absolute = true)
@Push
public class MainView extends AppLayout {
    private final TabNameToContentMapper tabNameToContentMapper;

    public MainView(SearchService searchService) {
        this.tabNameToContentMapper = new TabNameToContentMapper(searchService, UI.getCurrent());
        Image img = new Image("https://i.imgur.com/GPpnszs.png", "Vaadin Logo");
        Tabs tabs = new Tabs(
                new Tab("Home"),
                new Tab("Search"),
                new Tab("Queue")
        );
        img.setHeight("44px");
        setContent(this.tabNameToContentMapper.toContent("Home"));
        tabs.addSelectedChangeListener(
                l -> setContent(
                        this.tabNameToContentMapper.toContent(
                                tabs.getSelectedTab().getLabel()
                        )
                )
        );
        addToNavbar(img, tabs);
    }
}
