package cloud.ptl.povserver.service.stream;

import cloud.ptl.povserver.exception.NotFoundException;
import lombok.Data;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Data
@Service("frameStreamService")
public class FrameStreamService implements StreamService {
    @Override
    public ResponseEntity<ResourceRegion> getVideoRegionResized(String rangeHeader, Long videoId, int width, int height) throws IOException, InterruptedException, NotFoundException {
        return null;
    }

    @Override
    public ResponseEntity<ResourceRegion> getVideoRegion(String rangeHeader, Long videoId) throws IOException, NotFoundException {
        return null;
    }
}
