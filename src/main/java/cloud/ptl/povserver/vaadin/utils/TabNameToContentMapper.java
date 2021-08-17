package cloud.ptl.povserver.vaadin.utils;

import cloud.ptl.povserver.vaadin.components.MainComponent;
import cloud.ptl.povserver.vaadin.components.QueueComponent;
import cloud.ptl.povserver.vaadin.components.SearchComponent;
import com.vaadin.flow.component.Component;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TabNameToContentMapper {
    private static final Map<String, Component> mappings =
            Stream.of(new Object[][]{
                    {"Search", new SearchComponent()},
                    {"Home", new MainComponent()},
                    {"Queue", new QueueComponent()}
            }).collect(Collectors.toMap(d -> (String) d[0], d -> (Component) d[1]));

    public static Component toContent(String tabName) {
        return TabNameToContentMapper.mappings.get(tabName);
    }
}
