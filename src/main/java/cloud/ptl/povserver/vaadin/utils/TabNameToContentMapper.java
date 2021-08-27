package cloud.ptl.povserver.vaadin.utils;

import cloud.ptl.povserver.amqp.RabbitSender;
import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.metric.MetricsService;
import cloud.ptl.povserver.service.queue.QueueService;
import cloud.ptl.povserver.service.resource.ResourceService;
import cloud.ptl.povserver.service.search.SearchService;
import cloud.ptl.povserver.vaadin.components.MainComponent;
import cloud.ptl.povserver.vaadin.components.QueueComponent;
import cloud.ptl.povserver.vaadin.components.SearchComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Maps tab names to proper components embedded in page
 */
public class TabNameToContentMapper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<String, Component> mappings;
    public static String lastMappedComponent;

    public TabNameToContentMapper(SearchService searchService, UI ui, QueueService queueService, ResourceService resourceService, RabbitSender rabbitSender, MetricsService metricsService) throws NotFoundException {
        QueueComponent queueComponent = new QueueComponent(queueService, ui, rabbitSender);
        SearchComponent searchComponent = new SearchComponent(
                searchService,
                ui,
                resourceService,
                queueComponent::init
        );
        MainComponent mainComponent = new MainComponent(metricsService, ui);

        this.mappings = Stream.of(new Object[][]{
                {"Search", searchComponent},
                {"Home", mainComponent},
                {"Queue", queueComponent}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Component) d[1]));
        TabNameToContentMapper.lastMappedComponent = "None";
        this.logger.info("Initialized tab mapper");
    }

    public Component toContent(String tabName) {
        this.logger.info("Changing tab to " + tabName);
        TabNameToContentMapper.lastMappedComponent = tabName;
        return this.mappings.get(tabName);
    }
}
