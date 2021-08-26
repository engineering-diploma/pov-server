package cloud.ptl.povserver.amqp.message;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.util.Pair;

import java.util.List;

/**
 * Ping message is used to notify display about which movie should display.
 * Contains also some more sophisticated info so display does not have to download it on its own
 */
@Data
@Builder
public class VideoPingMessage {
    private Long videoId;
    private String title;
    private String description;
    private List<Pair<Integer, Integer>> resolutions;
}
