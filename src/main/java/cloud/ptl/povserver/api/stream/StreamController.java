package cloud.ptl.povserver.api.stream;

import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.stream.StreamService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Controller used to stream videos into pov display
 */
@RestController
@RequestMapping("/api")
public class StreamController {

    private final StreamService streamService;

    public StreamController(@Qualifier("videoStreamService") StreamService streamService) {
        this.streamService = streamService;
    }

    /**
     * Send video as is as binary stream
     *
     * @param range bytes to send
     * @param id    id of video to display
     * @return binary stream of video
     * @throws NotFoundException if given video is not found
     * @throws IOException       if video cannot be opened
     */
    @GetMapping("/video/{id}")
    public ResponseEntity<ResourceRegion> getVideoStream(
            @RequestHeader(value = "Range", required = false) String range,
            @PathVariable("id") Long id
    ) throws NotFoundException, IOException {
        return this.streamService.getVideoRegion(range, id);
    }

    /**
     * Resize video to given dimensions
     *
     * @param range  bytes to send
     * @param id     id of video to send
     * @param width  total width of vide
     * @param height total height of video
     * @return
     * @throws NotFoundException
     * @throws IOException
     * @throws InterruptedException
     */
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
