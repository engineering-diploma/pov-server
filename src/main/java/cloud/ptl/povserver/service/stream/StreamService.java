package cloud.ptl.povserver.service.stream;

import cloud.ptl.povserver.exception.NotFoundException;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

/**
 * Service used to stream video from server to clients
 */

public interface StreamService {
    ResponseEntity<ResourceRegion> getVideoRegionResized(String rangeHeader, Long videoId, int width, int height) throws IOException, InterruptedException, NotFoundException;

    ResponseEntity<ResourceRegion> getVideoRegion(String rangeHeader, Long videoId) throws IOException, NotFoundException;
}
