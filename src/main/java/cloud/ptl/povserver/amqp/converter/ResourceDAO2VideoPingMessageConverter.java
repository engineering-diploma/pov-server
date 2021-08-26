package cloud.ptl.povserver.amqp.converter;

import cloud.ptl.povserver.amqp.message.VideoPingMessage;
import cloud.ptl.povserver.data.model.ResourceDAO;
import lombok.experimental.UtilityClass;
import org.springframework.data.util.Pair;

import java.util.stream.Collectors;

@UtilityClass
public class ResourceDAO2VideoPingMessageConverter {
    /**
     * Converts ResourceDAO into message that will be ping about new vide for display
     *
     * @param resourceDAO resource dao which is ordered by user to play
     * @return ping message
     */
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
