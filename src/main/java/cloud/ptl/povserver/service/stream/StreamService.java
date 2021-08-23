package cloud.ptl.povserver.service.stream;

import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.resource.ResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;

import static java.lang.Math.min;

@Service
public class StreamService {
    private static final long CHUNK_SIZE = 1000000L;
    private final ResourceService resourceService;

    public StreamService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public ResponseEntity<ResourceRegion> getVideoRegion(String rangeHeader, Long videoId) throws IOException, MalformedURLException, NotFoundException {
        String resourcePath = this.getVideoPath(videoId);
        FileUrlResource videoResource = new FileUrlResource(resourcePath);
        ResourceRegion resourceRegion = getResourceRegion(videoResource, rangeHeader);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resourceRegion);
    }

    private String getVideoPath(Long videoId) throws NotFoundException {
        return this.resourceService.findById(videoId).getMovie().getAbsolutePath();
    }

    private ResourceRegion getResourceRegion(UrlResource video, String httpHeaders) throws IOException {
        ResourceRegion resourceRegion = null;

        long contentLength = video.contentLength();
        int fromRange = 0;
        int toRange = 0;
        if (StringUtils.isNotBlank(httpHeaders)) {
            String[] ranges = httpHeaders.substring("bytes=".length()).split("-");
            fromRange = Integer.parseInt(ranges[0]);
            if (ranges.length > 1) {
                toRange = Integer.parseInt(ranges[1]);
            } else {
                toRange = (int) (contentLength - 1);
            }
        }

        if (fromRange > 0) {
            long rangeLength = min(CHUNK_SIZE, toRange - fromRange + 1);
            resourceRegion = new ResourceRegion(video, fromRange, rangeLength);
        } else {
            long rangeLength = min(CHUNK_SIZE, contentLength);
            resourceRegion = new ResourceRegion(video, 0, rangeLength);
        }

        return resourceRegion;
    }
}
