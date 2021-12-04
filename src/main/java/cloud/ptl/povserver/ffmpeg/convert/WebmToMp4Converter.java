package cloud.ptl.povserver.ffmpeg.convert;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.resource.ResolutionService;
import cloud.ptl.povserver.service.resource.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

@Service
@Slf4j
public class WebmToMp4Converter extends ResourceConverter {
    private final String FFMPEG_CONVERT_COMMAND =
            "/usr/bin/ffmpeg -y -i %s -strict experimental %s.mp4";

    private final ResolutionService resolutionService;
    private final ResourceService resourceService;

    public WebmToMp4Converter(ResolutionService resolutionService, ResourceService resourceService) {
        this.resolutionService = resolutionService;
        this.resourceService = resourceService;
    }

    @Override
    public boolean supports(ConvertRequest.Format format) {
        return ConvertRequest.Format.GIF.equals(format);
    }

    @Override
    public ResourceDAO convert(ConvertRequest convertRequest) throws IOException, InterruptedException {
        this.convertToMp4(convertRequest);
        ResourceDAO newResourceDAO = new ResourceDAO();
        newResourceDAO.setResolutions(Collections.emptyList());
        newResourceDAO.setMovie(
                new File(
                        convertRequest.getDestinationFolder().getAbsolutePath() + File.separator + convertRequest.getFileToConvert().getName() + ".webm"
                )
        );
        newResourceDAO.setTitle(convertRequest.getFileToConvert().getName());
        this.setResolution(newResourceDAO);
        return this.resourceService.save(newResourceDAO);
    }

    private void setResolution(ResourceDAO resourceDAO) {
        try {
            resourceDAO.getResolutions().add(
                    this.resolutionService.findByResourceDAO(resourceDAO)
            );
        } catch (NotFoundException ex) {
            log.error("There is no stored resolution for given resource, something is probably broken");
        }

    }

    private void convertToMp4(ConvertRequest convertRequest) throws IOException, InterruptedException {
        String inflateCommand =
                String.format(
                        this.FFMPEG_CONVERT_COMMAND,
                        convertRequest.getFileToConvert().getAbsolutePath(),
                        convertRequest.getDestinationFolder().getAbsolutePath() + File.separator + convertRequest.getFileToConvert().getName()
                );
        this.run(inflateCommand);
    }
}
