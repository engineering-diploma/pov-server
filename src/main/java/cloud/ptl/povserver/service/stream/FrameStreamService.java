package cloud.ptl.povserver.service.stream;

import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.frame.FrameParserService;
import com.vaadin.flow.internal.Pair;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Data
@Slf4j
@Service("frameStreamService")
public class FrameStreamService implements StreamService {
    private FrameParserService frameParseService;

    public FrameStreamService(FrameParserService frameParseService) {
        this.frameParseService = frameParseService;
    }

    @Override
    public ResponseEntity<ResourceRegion> getVideoRegionResized(String rangeHeader, Long videoId, int width, int height) throws IOException, InterruptedException, NotFoundException {
        log.error("This method should never be invoked. For more information consult documentation. Method: ");
        return null;
    }

    @Override
    public ResponseEntity<ResourceRegion> getVideoRegion(String rangeHeader, Long videoId) throws IOException, NotFoundException {
        log.error("This method should never be invoked. For more information consult documentation. Method: ");
        return null;
    }

    private Pair<Integer, Integer> extractFramesNumbers(String rangeHeader) {
        String framesNumbers = rangeHeader.split(" ")[0];
        String start = framesNumbers.split(",")[0];
        String end = framesNumbers.split(",")[1];
        return new Pair<>(
                Integer.valueOf(start),
                Integer.valueOf(end)
        );
    }

    private int extractSamplingInterval(String rangeHeader) {
        return Integer.parseInt(rangeHeader.split(" ")[1]);
    }
}
