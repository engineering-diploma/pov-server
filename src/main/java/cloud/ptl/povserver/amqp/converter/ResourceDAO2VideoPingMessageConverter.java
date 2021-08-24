package cloud.ptl.povserver.amqp.converter;

import cloud.ptl.povserver.amqp.message.VideoPingMessage;
import cloud.ptl.povserver.data.model.ResourceDAO;
import org.springframework.data.util.Pair;

import java.util.stream.Collectors;

public class ResourceDAO2VideoPingMessageConverter {
    private ResourceDAO2VideoPingMessageConverter() {
    }

    ;

    public static VideoPingMessage convert(ResourceDAO resourceDAO) {
        return VideoPingMessage.builder()
                .videoId(resourceDAO.getId())
                .description(resourceDAO.getDescription())
                .title(resourceDAO.getTitle())
                .resolutions(
                        resourceDAO.getResolutions().stream()
                                .map(el -> Pair.of(el.getHeight(), el.getWidth()))
                                .collect(Collectors.toList())
                )
                .build();
    }
}
