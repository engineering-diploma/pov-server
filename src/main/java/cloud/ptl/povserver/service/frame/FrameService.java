package cloud.ptl.povserver.service.frame;

import cloud.ptl.povserver.data.frame.PovFrame;
import cloud.ptl.povserver.data.model.ResolutionDAO;
import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.ffmpeg.convert.ConvertRequest;
import cloud.ptl.povserver.service.resource.ResolutionService;
import cloud.ptl.povserver.service.resource.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FrameService {
    private final String CONVERSION_COMMAND = "etc/frame_converter.sh %s %s %d %d %d %s";
    private final ResourceService resourceService;
    private final ResolutionService resolutionService;
    @Value("${ptl.download.converted}")
    private String convertedPath;
    private File convertedDir;

    public FrameService(ResourceService resourceService, ResolutionService resolutionService) {
        this.resourceService = resourceService;
        this.resolutionService = resolutionService;
    }

    @PostConstruct
    public void init() {
        this.convertedDir = new File(this.convertedPath);
        this.convertedDir.mkdir();
    }

    public ResourceDAO createFramesFrom(PovFrameRequest request) throws IOException, InterruptedException, NotFoundException {
        String newFramesFileDestination = this.createName(request);
        if (this.checkIfFramesFileExist(newFramesFileDestination)) {
            return this.fetchResourceDaoWithFileName(newFramesFileDestination);
        } else {
            return this.makeNewFramesFile(request, newFramesFileDestination);
        }
    }

    private boolean checkIfFramesEntryInDBExists(ResourceDAO baseResourceDAO) {
        return this.resourceService.existsByTitleAndIsFrames(baseResourceDAO.getTitle());
    }

    private ResourceDAO addNewResolution(PovFrameRequest request, String destination) throws IOException, InterruptedException, NotFoundException {
        String inflatedCommand =
                String.format(
                        this.CONVERSION_COMMAND,
                        request.getResourceDAO().getMovie(),
                        destination,
                        request.getWidth(),
                        request.getHeight(),
                        request.getSamplingInterval()
                );
        this.convert(inflatedCommand);
        ResolutionDAO resolutionDAO = new ResolutionDAO();
        resolutionDAO.setHeight(request.getHeight());
        resolutionDAO.setWidth(request.getWidth());
        resolutionDAO = this.resolutionService.save(resolutionDAO);
        ResourceDAO resourceDAO =
                this.resourceService.findByTitleAndIsFrames(
                        request.getResourceDAO().getTitle()
                );
        resourceDAO.getResolutions().add(resolutionDAO);
        return this.resourceService.save(resourceDAO);
    }

    private ResourceDAO makeNewFramesFile(PovFrameRequest request, String destination) throws IOException, InterruptedException {
        String inflatedCommand =
                String.format(
                        this.CONVERSION_COMMAND,
                        request.getResourceDAO().getMovie(),
                        destination,
                        request.getWidth(),
                        request.getHeight(),
                        request.getSamplingInterval(),
                        request.getLedStrip()
                );
        log.info("Executing command " + inflatedCommand);
        this.convert(inflatedCommand);
        ResourceDAO newResourceDAO = new ResourceDAO();
        newResourceDAO.setTitle(request.getResourceDAO().getTitle());
        newResourceDAO.setDescription(request.getResourceDAO().getDescription());
        newResourceDAO.setFrameStream(new File(destination));
        newResourceDAO.setThumbnailUrls(new ArrayList<>());
        request.getResourceDAO().getThumbnailUrls().forEach(el -> newResourceDAO.getThumbnailUrls().add(el));
        newResourceDAO.setFormat(ConvertRequest.Format.FRAMES);
        newResourceDAO.setDownloadUrl("");
        newResourceDAO.setIsMovie(true);
        ResolutionDAO resolutionDAO = new ResolutionDAO();
        resolutionDAO.setHeight(request.getHeight());
        resolutionDAO.setWidth(request.getWidth());
        resolutionDAO = this.resolutionService.save(resolutionDAO);
        newResourceDAO.setResolutions(new ArrayList<>());
        newResourceDAO.getResolutions().add(resolutionDAO);
        return this.resourceService.save(newResourceDAO);
    }

    public void convert(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
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

    private ResourceDAO fetchResourceDaoWithFileName(String name) {
        File movie = new File(name);
        try {
            return this.resourceService.findByFrameStream(movie);
        } catch (NotFoundException e) {
            log.error(
                    String.format(
                            "There is file with given name %s but DB does not contains information about it. Aborting. %n Error message %s",
                            name, e.getMessage()
                    )
            );
            return null;
        }
    }

    private boolean checkIfFramesFileExist(String path) {
        int existingWithSameName =
                Objects.requireNonNull(
                        this.convertedDir.listFiles(
                                el -> el.getAbsolutePath().equals(path)
                        )
                ).length;
        return existingWithSameName > 0;
    }

    private String createName(PovFrameRequest request) {
        return this.convertedDir.getAbsolutePath()
                + File.separator
                + request.getResourceDAO().getTitle().replace(" ", "_")
                + "_" + request.getWidth() + "x" + request.getHeight() + "_" + request.getSamplingInterval() + "ms_" + request.getLedStrip() + ".frames";
    }

    public List<PovFrame> getFrames(PovFrameRequest request) throws IOException, NotFoundException, InterruptedException {
        ResourceDAO framesResourceDAO = this.createFramesFrom(request);
        File file = framesResourceDAO.getFrameStream();
        List<String> lines = FileUtils.readLines(file);
        List<PovFrame> povFrames = new ArrayList<>();
        PovFrame povFrame = new PovFrame();
        povFrame.setRows(new ArrayList<>());
        for (String line : lines) {
            // "End" is delimiter for each parsed frame
            if (line.contains("End")) {
                povFrames.add(povFrame);
                povFrame = new PovFrame();
                povFrame.setRows(new ArrayList<>());
                continue;
            }
            List<PovFrame.Cell> cellLine = new ArrayList<>();
            List<String> pixels = List.of(line.split("\t"));
            for (String pixel : pixels) {
                pixel = pixel.replaceAll(" +", " ");
                List<String> RGB = List.of(pixel.split(" "));
                PovFrame.Cell cell = new PovFrame.Cell();
                cell.setR(RGB.get(0));
                cell.setG(RGB.get(1));
                cell.setB(RGB.get(2));
                cellLine.add(cell);
            }
            povFrame.getRows().add(cellLine);
        }
        return povFrames;
    }
}
