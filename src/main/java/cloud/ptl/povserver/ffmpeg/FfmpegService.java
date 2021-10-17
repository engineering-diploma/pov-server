package cloud.ptl.povserver.ffmpeg;

// ffmpeg -i 'I made Brett do this....webm' -s 1280x720 -vcodec libx265 'I made Brett do this...1280x720.mp4'

import cloud.ptl.povserver.data.model.ResolutionDAO;
import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.service.resource.ResolutionService;
import cloud.ptl.povserver.service.resource.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Service is used to call external program to convert vide into other formats.
 * ffmpeg -i earth.gif -c:v libvpx-vp9 -crf 30 -b:v 0 -b:a 128k -c:a libopus -s 476x280 out.webm
 */
@Service
public class FfmpegService {
    private final String FFMPEG_RESIZE_COMMAND =
            "/usr/bin/ffmpeg -i %s -c:v libvpx-vp9 -crf 30 -b:v 0 -b:a 128k -c:a libopus -s %dx%d %s%dx%d.webm";
    private final Logger logger = LoggerFactory.getLogger(FfmpegService.class);
    private final ResourceService resourceService;
    private final ResolutionService resolutionService;

    public FfmpegService(ResourceService resourceService, ResolutionService resolutionService) {
        this.resourceService = resourceService;
        this.resolutionService = resolutionService;
    }

    /**
     * Resize given resource into given format
     *
     * @param resizeRequest request containing all information to conduct resizing
     * @return resized resource
     * @throws IOException if file cannot be found
     */
    public ResourceDAO resize(ResizeRequest resizeRequest) throws IOException, InterruptedException {
        List<ResolutionDAO> availableResolutions = resizeRequest.getResourceDAO().getResolutions();
        // checking if conversion was made before
        boolean alreadyContains =
                availableResolutions.stream()
                        .anyMatch(el ->
                                el.getHeight() == resizeRequest.getHeight() && el.getWidth() == resizeRequest.getWidth()
                        );
        if (!alreadyContains) {
            this.callFFMPEG(resizeRequest);
        }
        // update path to newly created resource
        String newPath = resizeRequest.getResourceDAO().getMovie().getAbsolutePath();
        newPath += resizeRequest.getWidth() + "x" + resizeRequest.getHeight() + ".webm";
        ResourceDAO resizedResource = resizeRequest.getResourceDAO();
        resizedResource.setMovie(new File(newPath));
        return resizedResource;
    }

    /**
     * Calls FFMPEG command line utility to generate resized resource
     *
     * @param resizeRequest request containing all needed informations to conduct resizing
     * @throws IOException thrown if resize cannot be done, because file was not found
     */
    private void callFFMPEG(ResizeRequest resizeRequest) throws IOException, InterruptedException {
        String inflateCommand =
                String.format(
                        this.FFMPEG_RESIZE_COMMAND,
                        resizeRequest.getResourceDAO().getMovie().getAbsolutePath(),
                        resizeRequest.getWidth(),
                        resizeRequest.getHeight(),
                        resizeRequest.getResourceDAO().getMovie().getAbsolutePath(),
                        resizeRequest.getWidth(),
                        resizeRequest.getHeight()
                );
        ProcessBuilder processBuilder = new ProcessBuilder(inflateCommand.split(" "));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        // connect to process streams
        BufferedReader outputBufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;

        while ((line = outputBufferedReader.readLine()) != null) {
            this.logger.info(line);
        }
        while ((line = errorBufferedReader.readLine()) != null) {
            this.logger.info(line);
        }

        process.waitFor();
        if (process.exitValue() == 0) {
            // processed successfully
            // rewrite data to resource
            ResolutionDAO resolutionDAO = new ResolutionDAO();
            resolutionDAO.setWidth(resizeRequest.getWidth());
            resolutionDAO.setHeight(resizeRequest.getHeight());
            resolutionDAO = this.resolutionService.save(resolutionDAO);
            resizeRequest.getResourceDAO().getResolutions().add(resolutionDAO);
            resizeRequest.setResourceDAO(
                    this.resourceService.save(
                            resizeRequest.getResourceDAO()
                    )
            );
        } else {
            // some error occurred
            throw new RuntimeException("Cannot convert file to given resolution, ffmpeg exit code in none-zero");
        }
    }
}
