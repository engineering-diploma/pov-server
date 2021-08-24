package cloud.ptl.povserver.api.stream;

import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.stream.StreamService;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class StreamController {
    private final StreamService streamService;

    public StreamController(StreamService streamService) {
        this.streamService = streamService;
    }

    @GetMapping("/video/{id}")
    public ResponseEntity<ResourceRegion> getVideoStream(
            @RequestHeader(value = "Range", required = false) String range,
            @PathVariable("id") Long id
    ) throws NotFoundException, IOException {
        return this.streamService.getVideoRegion(range, id);
    }

    @GetMapping("/video/resize/{id}")
    public ResponseEntity<ResourceRegion> getVideoStreamResized(
            @RequestHeader(value = "Range", required = false) String range,
            @PathVariable("id") Long id,
            @RequestParam("width") int width,
            @RequestParam("height") int height
    ) throws NotFoundException, IOException, InterruptedException {
        return this.streamService.getVideoRegionResized(range, id, width, height);
    }
}
