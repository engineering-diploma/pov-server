package cloud.ptl.povserver.amqp.message;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.util.Pair;

import java.util.List;

@Data
@Builder
public class VideoPingMessage {
    private Long videoId;
    private String title;
    private String description;
    private List<Pair<Integer, Integer>> resolutions;
}
