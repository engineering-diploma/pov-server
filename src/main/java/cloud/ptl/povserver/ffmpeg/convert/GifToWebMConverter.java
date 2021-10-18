package cloud.ptl.povserver.ffmpeg.convert;

import cloud.ptl.povserver.data.model.ResolutionDAO;
import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.service.resource.ResolutionService;
import cloud.ptl.povserver.service.resource.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
@Slf4j
public class GifToWebMConverter implements ResourceConverter {
    private final String FFMPEG_CONVERT_COMMAND =
            "/usr/bin/ffmpeg -i %s -c:v libvpx-vp9 -crf 30 -b:v 0 -b:a 128k -c:a libopus -s %dx%d %s.webm";

    private final ResolutionService resolutionService;
    private final ResourceService resourceService;

    public GifToWebMConverter(ResolutionService resolutionService, ResourceService resourceService) {
        this.resolutionService = resolutionService;
        this.resourceService = resourceService;
    }

    @Override
    public boolean supports(ConvertRequest.Format format) {
        return ConvertRequest.Format.GIF.equals(format);
    }

    @Override
    public ResourceDAO convert(ConvertRequest convertRequest) throws IOException, InterruptedException {
        Pair<Integer, Integer> dimensions = this.getGifDimensions(convertRequest.getFileToConvert());
        this.callFFMPEGtoConvertToGif(convertRequest, dimensions);
        ResolutionDAO resolutionDAO = new ResolutionDAO();
        resolutionDAO.setHeight(dimensions.getFirst());
        resolutionDAO.setWidth(dimensions.getSecond());
        resolutionDAO = this.resolutionService.save(resolutionDAO);

        ResourceDAO newResourceDAO = new ResourceDAO();
        newResourceDAO.setResolutions(List.of(resolutionDAO));
        newResourceDAO.setMovie(
                new File(
                        convertRequest.getDestinationFolder().getAbsolutePath() + File.separator + convertRequest.getFileToConvert().getName() + ".webm"
                )
        );
        newResourceDAO.setTitle(convertRequest.getFileToConvert().getName());
        return this.resourceService.save(newResourceDAO);
    }

    private Pair<Integer, Integer> getGifDimensions(File file) throws IOException {
        ImageReader is = ImageIO.getImageReadersBySuffix("GIF").next();
        ImageInputStream iis;
        try {
            iis = ImageIO.createImageInputStream(file);
            is.setInput(iis);
            return Pair.of(
                    is.getWidth(0),
                    is.getHeight(0)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void callFFMPEGtoConvertToGif(ConvertRequest convertRequest, Pair<Integer, Integer> dimensions) throws IOException, InterruptedException {
        String inflateCommand =
                String.format(
                        this.FFMPEG_CONVERT_COMMAND,
                        convertRequest.getFileToConvert().getAbsolutePath(),
                        dimensions.getFirst(),
                        dimensions.getSecond(),
                        convertRequest.getDestinationFolder().getAbsolutePath() + File.separator + convertRequest.getFileToConvert().getName()
                );
        ProcessBuilder processBuilder = new ProcessBuilder(inflateCommand.split(" "));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        // connect to process streams
        BufferedReader outputBufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;

        while ((line = outputBufferedReader.readLine()) != null) {
            log.info(line);
        }
        while ((line = errorBufferedReader.readLine()) != null) {
            log.info(line);
        }

        process.waitFor();
        if (process.exitValue() == 0) {
            // processed successfully
            // rewrite data to resource
            return;
        } else {
            // some error occurred
            throw new RuntimeException("Cannot convert file to given resolution, ffmpeg exit code in none-zero");
        }
    }
}
