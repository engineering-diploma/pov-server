package cloud.ptl.povserver.service.stream;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.ffmpeg.FfmpegService;
import cloud.ptl.povserver.ffmpeg.resize.ResizeRequest;
import cloud.ptl.povserver.service.metric.MetricsService;
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

/**
 * Service used to stream video from server to clients
 */
@Service
public class StreamService {
    private static final long CHUNK_SIZE = 1000000L;
    private final ResourceService resourceService;
    private final FfmpegService ffmpegService;
    private final MetricsService metricsService;

    public StreamService(ResourceService resourceService, FfmpegService ffmpegService, MetricsService metricsService) {
        this.resourceService = resourceService;
        this.ffmpegService = ffmpegService;
        this.metricsService = metricsService;
    }

    /**
     * Returns ordered by client region of video
     *
     * @param rangeHeader header in user order pointing to which portion of video should be sent
     * @param videoId     id of video stored in system
     * @return stream of vide ordered by user
     */
    public ResponseEntity<ResourceRegion> getVideoRegion(String rangeHeader, Long videoId) throws IOException, MalformedURLException, NotFoundException {
        String resourcePath = this.getVideoPath(videoId);
        return this.extractRegion(rangeHeader, resourcePath);
    }

    /**
     * Returns resized part of video ordered by user
     *
     * @param rangeHeader plae of video to send
     * @param videoId     internal  id of vide
     * @param width       width of new vide
     * @param height      height of new video
     * @return resized stream of vide
     */
    public ResponseEntity<ResourceRegion> getVideoRegionResized(String rangeHeader, Long videoId, int width, int height) throws NotFoundException, IOException, InterruptedException {
        ResourceDAO resourceDAO = this.resourceService.findById(videoId);
        ResizeRequest resizeRequest = new ResizeRequest();
        resizeRequest.setWidth(width);
        resizeRequest.setHeight(height);
        resizeRequest.setResourceDAO(resourceDAO);
        ResourceDAO resizedResource = this.ffmpegService.resize(resizeRequest);

        String resourcePath = resizedResource.getMovie().getAbsolutePath();
        return this.extractRegion(rangeHeader, resourcePath);
    }

    /**
     * Extract given part of vide
     *
     * @param rangeHeader  part of video to extract
     * @param resourcePath path to resource to send in stream
     * @return stream of vide
     */
    private ResponseEntity<ResourceRegion> extractRegion(String rangeHeader, String resourcePath) throws IOException {
        FileUrlResource videoResource = new FileUrlResource(resourcePath);
        ResourceRegion resourceRegion = getResourceRegion(videoResource, rangeHeader);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resourceRegion);
    }

    /**
     * Returns path to video with given id
     *
     * @param videoId internal id of vide
     * @return path to this video
     */
    private String getVideoPath(Long videoId) throws NotFoundException {
        return this.resourceService.findById(videoId).getMovie().getAbsolutePath();
    }

    /**
     * Returns region of vide
     */
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
            this.metricsService.updateSendDataAmount((float) rangeLength / (1024F * 1024F));
            resourceRegion = new ResourceRegion(video, fromRange, rangeLength);
        } else {
            long rangeLength = min(CHUNK_SIZE, contentLength);
            this.metricsService.updateSendDataAmount((float) rangeLength / (1024F * 1024F));
            resourceRegion = new ResourceRegion(video, 0, rangeLength);
        }

        return resourceRegion;
    }
}
