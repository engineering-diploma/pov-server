package cloud.ptl.povserver.vaadin.components;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class QueueComponent extends VerticalLayout {
    public QueueComponent() {
        VerticalLayout vl = new VerticalLayout();
        Paragraph p = new Paragraph("Some Queue");
        vl.add(p);
        add(vl);
    }
}
