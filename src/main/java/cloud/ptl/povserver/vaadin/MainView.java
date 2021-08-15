package cloud.ptl.povserver.vaadin;


import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;

@Route(value = "/portal", absolute = true)
public class MainView extends AppLayout {
    public MainView() {
        Image img = new Image("https://i.imgur.com/GPpnszs.png", "Vaadin Logo");
        Tabs tabs = new Tabs(new Tab("Home"), new Tab("About"));
        img.setHeight("44px");
        addToNavbar(img, tabs);
    }
}
