package cloud.ptl.povserver.vaadin.utils;

import cloud.ptl.povserver.service.queue.QueueService;
import cloud.ptl.povserver.service.resource.ResourceService;
import cloud.ptl.povserver.service.search.SearchService;
import cloud.ptl.povserver.vaadin.components.MainComponent;
import cloud.ptl.povserver.vaadin.components.QueueComponent;
import cloud.ptl.povserver.vaadin.components.SearchComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TabNameToContentMapper {

    private final SearchService searchService;
    private final Map<String, Component> mappings;

    public TabNameToContentMapper(SearchService searchService, UI ui, QueueService queueService, ResourceService resourceService) {
        this.searchService = searchService;
        QueueComponent queueComponent = new QueueComponent(queueService, ui);
        SearchComponent searchComponent = new SearchComponent(
                searchService,
                ui,
                resourceService,
                queueComponent::init
        );
        MainComponent mainComponent = new MainComponent();

        this.mappings = Stream.of(new Object[][]{
                {"Search", searchComponent},
                {"Home", mainComponent},
                {"Queue", queueComponent}
        }).collect(Collectors.toMap(d -> (String) d[0], d -> (Component) d[1]));
    }

    public Component toContent(String tabName) {
        return this.mappings.get(tabName);
    }
}
