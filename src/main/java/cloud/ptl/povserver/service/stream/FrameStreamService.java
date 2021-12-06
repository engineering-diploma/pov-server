package cloud.ptl.povserver.service.stream;

import cloud.ptl.povserver.data.frame.PovFrame;
import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.frame.FrameService;
import cloud.ptl.povserver.service.frame.PovFrameRequest;
import cloud.ptl.povserver.service.resource.ResourceService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Data
@Slf4j
@Service("frameStreamService")
public class FrameStreamService {
    private final FrameService frameParseService;
    private final ResourceService resourceService;

    public FrameStreamService(FrameService frameParseService, ResourceService resourceService) {
        this.frameParseService = frameParseService;
        this.resourceService = resourceService;
    }

    public String getVideoRegion(Long videoId, int height, int width, int sampleInterval, int start, int end) throws IOException, NotFoundException, InterruptedException {
        ResourceDAO requestedResource = this.resourceService.findById(videoId);
        PovFrameRequest povFrameRequest = new PovFrameRequest();
        povFrameRequest.setHeight(height);
        povFrameRequest.setWidth(width);
        povFrameRequest.setSamplingInterval(sampleInterval);
        povFrameRequest.setResourceDAO(requestedResource);
        List<PovFrame> frames = this.frameParseService.getFrames(povFrameRequest);
        end = Math.min(end, frames.size());
        start = Math.max(0, start);
        return this.join(
                frames.subList(start, end)
        );
    }

    private String join(List<PovFrame> frames) {
        StringBuilder result = new StringBuilder();
        for (PovFrame frame : frames) {
            for (List<PovFrame.Cell> row : frame.getRows()) {
                for (PovFrame.Cell cell : row) {
                    result.append(String.format(
                            "%s %s %s ",
                            cell.getR(), cell.getG(), cell.getB()
                    ));
                }
                result.append("\n");
            }
        }
        return result.toString();
    }
}
