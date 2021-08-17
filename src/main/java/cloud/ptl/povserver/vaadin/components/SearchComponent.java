package cloud.ptl.povserver.vaadin.components;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class SearchComponent extends VerticalLayout {
    public SearchComponent() {
        VerticalLayout vl = new VerticalLayout();
        Paragraph p = new Paragraph("Some Search");
        vl.add(p);
        add(vl);
    }
}
