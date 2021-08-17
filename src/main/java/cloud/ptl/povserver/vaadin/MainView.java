package cloud.ptl.povserver.vaadin;


import cloud.ptl.povserver.vaadin.utils.TabNameToContentMapper;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;

@Route(value = "/", absolute = true)
public class MainView extends AppLayout {
    public MainView() {
        Image img = new Image("https://i.imgur.com/GPpnszs.png", "Vaadin Logo");
        Tabs tabs = new Tabs(
                new Tab("Home"),
                new Tab("Search"),
                new Tab("Queue")
        );
        img.setHeight("44px");
        setContent(TabNameToContentMapper.toContent("Home"));
        tabs.addSelectedChangeListener(
                l -> setContent(
                        TabNameToContentMapper.toContent(
                                tabs.getSelectedTab().getLabel()
                        )
                )
        );
        addToNavbar(img, tabs);
    }
}
