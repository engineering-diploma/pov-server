package cloud.ptl.povserver.vaadin.components;

import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.metric.MetricCallback;
import cloud.ptl.povserver.service.metric.MetricKeys;
import cloud.ptl.povserver.service.metric.MetricsService;
import cloud.ptl.povserver.vaadin.utils.NumberFormatter;
import cloud.ptl.povserver.vaadin.utils.TabNameToContentMapper;
import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.content.HorizontalCardComponentContainer;
import com.github.appreciated.card.content.Item;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MainComponent extends VerticalLayout {

    private String RPM;
    private String totalRotations;
    private String angleSpeed;
    private String tangentialSpeed;

    private final MetricsService metricsService;
    private final UI ui;

    public MainComponent(MetricsService metricsService, UI ui) throws NotFoundException {
        this.metricsService = metricsService;
        this.ui = ui;
        add(this.createHead());
        add(this.createServerDescription());
        add(this.createMetrics());
        this.registerToMetricCallback();
    }

    private void registerToMetricCallback() {
        MetricCallback metricCallback = () -> {
            if (TabNameToContentMapper.lastMappedComponent.equals("Home")) {
                ui.access(() -> {
                    getChildren()
                            .filter(el -> el.getId().orElse("no").equals("metrics"))
                            .findFirst()
                            .ifPresent(this::remove);
                    try {
                        add(createMetrics());
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        this.metricsService.registerCallback(metricCallback);
    }

    private Component createHead() {
        VerticalLayout vl = new VerticalLayout();
        H2 h2 = new H2("Persistence of Vision server");
        H4 h4 = new H4("What is PoV?");
        Paragraph p = new Paragraph(
                "Persistence of vision traditionally refers to the optical illusion that occurs when visual perception of an object does not cease for some time after the rays of light proceeding from it have ceased to enter the eye. The illusion has also been described as \"retinal persistence\", \"persistence of impressions\", simply \"persistence\" and other variations. In many descriptions the illusion seems the same as, or very similar to positive afterimages. \"Persistence of vision\" can also be understood to mean the same as \"flicker fusion\", the effect that vision seems to persist continuously when the light that enters the eyes is interrupted with short and regular intervals."
        );
        vl.add(h2);
        vl.add(h4);
        vl.add(p);
        return vl;
    }

    private Component createServerDescription() {
        VerticalLayout vl = new VerticalLayout();
        H4 h4 = new H4("Why do we create this?");
        Paragraph p = new Paragraph(
                "This server is dedicated to communicate and control other part of our project called PoV Display. Scope of this server is to enable end user to easily manipulate complex process of fetching, transformation and post processing of data which could be consumed by PoV Display. Moreover thanks to bidirectional communication we can continuously monitor and react to conditions of PoV Display, but also make some high level performance testing"
        );
        vl.add(h4);
        vl.add(p);
        return vl;
    }

    private Component createMetrics() throws NotFoundException {
        VerticalLayout vl = new VerticalLayout();
        vl.setId("metrics");

        HorizontalCardComponentContainer<RippleClickableCard> cardContainer1 = new HorizontalCardComponentContainer<>();
        cardContainer1.add(this.rotationPerMinute());
        cardContainer1.add(this.totalRotations());
        cardContainer1.add(this.rotationMadeInLastMinute());
        vl.add(cardContainer1);

        HorizontalCardComponentContainer<RippleClickableCard> cardContainer3 = new HorizontalCardComponentContainer<>();
        cardContainer3.add(this.numberOfDiodeSwitched());
        cardContainer3.add(this.framesDisplayed());
        cardContainer3.add(this.dataTransferredToDisplay());
        vl.add(cardContainer3);

        HorizontalCardComponentContainer<RippleClickableCard> cardContainer2 = new HorizontalCardComponentContainer<>();
        cardContainer2.add(this.angleSpeed());
        cardContainer2.add(this.tangentialSpeed());
        vl.add(cardContainer2);
        return vl;
    }

    private RippleClickableCard rotationPerMinute() throws NotFoundException {
        Float rpm = this.metricsService.findByKey(MetricKeys.RPM.getName()).getValue();
        String formatted = NumberFormatter.format(rpm);
        Item item = new Item(formatted + " RPM", "...is the number rotation per minute display is doing");
        return new RippleClickableCard(item);
    }

    private RippleClickableCard totalRotations() throws NotFoundException {
        Float totalRotations = this.metricsService.findByKey(MetricKeys.TOTAL_ROTATIONS.getName()).getValue();
        String formatted = NumberFormatter.format(totalRotations);
        Item item = new Item(formatted, "...is the rotation made since start");
        return new RippleClickableCard(item);
    }

    private RippleClickableCard rotationMadeInLastMinute() throws NotFoundException {
        Float lastMinuteRotations = this.metricsService.findByKey(MetricKeys.LAST_MINUTE_ROTATIONS.getName()).getValue();
        String formatted = NumberFormatter.format(lastMinuteRotations);
        Item item = new Item(formatted, "...is the number of rotations made in last minute");
        return new RippleClickableCard(item);
    }

    private RippleClickableCard angleSpeed() throws NotFoundException {
        Float angleSpeed = this.metricsService.getAngleSpeed();
        String formatted = NumberFormatter.format(angleSpeed);
        Item item = new Item(formatted + " deg/s", "...is the angle speed");
        return new RippleClickableCard(item);
    }

    private RippleClickableCard tangentialSpeed() throws NotFoundException {
        Float tangentialSpeed = this.metricsService.getTangentialSpeed();
        String formatted = NumberFormatter.format(tangentialSpeed);
        Item item = new Item(formatted + " km/h", "...is the tangential speed");
        return new RippleClickableCard(item);
    }

    private RippleClickableCard numberOfDiodeSwitched() throws NotFoundException {
        Float diodeSwitches = this.metricsService.findByKey(MetricKeys.DIODE_SWITCHES.getName()).getValue();
        String formatted = NumberFormatter.format(diodeSwitches);
        Item item = new Item(formatted, "...is the number of times we switched led");
        return new RippleClickableCard(item);
    }

    private RippleClickableCard framesDisplayed() throws NotFoundException {
        Float framesDisplayed = this.metricsService.findByKey(MetricKeys.FRAMES_DISPLAYED.getName()).getValue();
        String formatted = NumberFormatter.format(framesDisplayed);
        Item item = new Item(formatted, "...is the number of frames we displayed");
        return new RippleClickableCard(item);
    }

    private RippleClickableCard dataTransferredToDisplay() throws NotFoundException {
        Float dataTransferred = this.metricsService.findByKey(MetricKeys.DATA_TRANSFERRED_TO_DISPLAY.getName()).getValue();
        String formatted = NumberFormatter.format(dataTransferred);
        Item item = new Item(formatted + " MB", "...is the data sent to display");
        return new RippleClickableCard(item);
    }
}
