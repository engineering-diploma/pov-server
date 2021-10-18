package cloud.ptl.povserver.service.metadata;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.resource.ResourceService;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MetadataService {

    private final ResourceService resourceService;

    public MetadataService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public Metadata getMetadata(ResourceDAO resourceDAO) throws IOException {
        FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");
        FFmpegProbeResult probeResult = ffprobe.probe(resourceDAO.getMovie().getAbsolutePath());

        FFmpegFormat format = probeResult.getFormat();
        FFmpegStream stream = probeResult.getStreams().get(0);

        Metadata metadata = new Metadata();
        metadata.setCodec(stream.codec_long_name);
        metadata.setDescription(resourceDAO.getDescription());
        metadata.setHeight(stream.height);
        metadata.setWidth(stream.width);
        metadata.setDuration(stream.duration);
        metadata.setDownloadUrl(resourceDAO.getDownloadUrl());
        metadata.setFfmpegFormat(format.format_long_name);
        metadata.setFrameRate(stream.avg_frame_rate.doubleValue());
        metadata.setFileName(resourceDAO.getMovie().getName());
        metadata.setLocation(resourceDAO.getMovie().getAbsolutePath());
        metadata.setTitle(resourceDAO.getTitle());
        return metadata;
    }

    public Metadata getMetadata(Long id) throws NotFoundException, IOException {
        return this.getMetadata(
                this.resourceService.findById(id)
        );
    }
}
