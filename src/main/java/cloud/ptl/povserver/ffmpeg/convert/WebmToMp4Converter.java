package cloud.ptl.povserver.ffmpeg.convert;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.resource.ResolutionService;
import cloud.ptl.povserver.service.resource.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
        return format.equals(ConvertRequest.Format.WEBM);
    }

    @Override
    public ResourceDAO convert(ConvertRequest convertRequest) throws IOException, InterruptedException {
        this.convertToMp4(convertRequest);
        ResourceDAO resourceDAO = this.findResourceDAOby(convertRequest.getFileToConvert());
        ResourceDAO newResourceDAO = new ResourceDAO();
        newResourceDAO.setResolutions(new ArrayList<>());
        newResourceDAO.setMovie(
                new File(
                        convertRequest.getDestinationFolder().getAbsolutePath() + File.separator + convertRequest.getFileToConvert().getName() + ".mp4"
                )
        );
        newResourceDAO.setTitle(convertRequest.getFileToConvert().getName());
        newResourceDAO.getResolutions().add(
                resourceDAO.getResolutions().get(0)
        );
        newResourceDAO.setTitle(resourceDAO.getTitle());
        newResourceDAO.setDescription(resourceDAO.getDescription());
        newResourceDAO.setDownloadUrl(resourceDAO.getDownloadUrl());
        newResourceDAO.setThumbnailUrls(new ArrayList<>());
        resourceDAO.getThumbnailUrls().forEach(el -> newResourceDAO.getThumbnailUrls().add(el));
        // newResourceDAO.setThumbnailUrls(resourceDAO.getThumbnailUrls());
        newResourceDAO.setIsMovie(true);
        newResourceDAO.setFormat(ConvertRequest.Format.MP4);
        return this.resourceService.save(newResourceDAO);
    }

    private ResourceDAO findResourceDAOby(File file) {
        try {
            return this.resourceService.findByMovie(file);
        } catch (NotFoundException ex) {
            log.error("There is no stored resolution for given resource, something is probably broken");
            return null;
        }
    }

    private void convertToMp4(ConvertRequest convertRequest) throws IOException, InterruptedException {
        String inflateCommand =
                String.format(
                        this.FFMPEG_CONVERT_COMMAND,
                        convertRequest.getFileToConvert().getAbsolutePath(),
                        convertRequest.getDestinationFolder().getAbsolutePath() + File.separator + convertRequest.getFileToConvert().getName()
                );
        log.info("Executing command " + inflateCommand);
        this.run(inflateCommand);
    }
}
