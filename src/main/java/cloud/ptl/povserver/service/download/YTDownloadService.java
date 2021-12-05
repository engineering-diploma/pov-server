package cloud.ptl.povserver.service.download;

import cloud.ptl.povserver.data.model.ResolutionDAO;
import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.ffmpeg.convert.ConvertRequest;
import cloud.ptl.povserver.ffmpeg.convert.FormatConverter;
import cloud.ptl.povserver.service.resource.ResolutionService;
import cloud.ptl.povserver.service.resource.ResourceService;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Service used to download youtube movies
 */
@Service
public class YTDownloadService implements DownloadService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YoutubeDownloader youtubeDownloader = new YoutubeDownloader();
    private final ResolutionService resolutionService;
    private final ResourceService resourceService;
    private final FormatConverter formatConverter;
    @Value("${ptl.download.youtube}")
    private String youtubeDownloadPath;
    @Value("ptl.download.converted")
    private String convertedDownloadsPath;
    private File youtubeDownloadDir;
    private File convertedDownloadsDir;

    public YTDownloadService(ResolutionService resolutionService, ResourceService resourceService, FormatConverter formatConverter) {
        this.resolutionService = resolutionService;
        this.resourceService = resourceService;
        this.formatConverter = formatConverter;
    }

    @PostConstruct
    private void init() {
        this.youtubeDownloadDir = new File(this.youtubeDownloadPath);
        this.youtubeDownloadDir.mkdir();

        this.convertedDownloadsDir = new File(this.convertedDownloadsPath);
        this.convertedDownloadsDir.mkdir();
    }

    /**
     * Fetch info from youtube
     *
     * @param videoId id of movie which is in youtube url
     * @return VideoInfo object
     */
    public VideoInfo getVideoInfo(String videoId) {
        RequestVideoInfo request = new RequestVideoInfo(videoId);
        Response<VideoInfo> response = youtubeDownloader.getVideoInfo(request);
        return response.data();
    }

    /**
     * Extracts video id from youtube link.
     * Id is store in v path variable
     *
     * @param link link from which we should extract video
     * @return extracted id
     */
    private String extractVideoId(String link) {
        String pathAttributesString = link.split("\\?")[1];
        String[] pathAttributes = pathAttributesString.split("&");
        String vAttribute = Arrays.stream(pathAttributes).sequential()
                .filter(el -> el.split("=")[0].equals("v"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Missing argument v in url"));
        return vAttribute.split("=")[1];
    }

    /**
     * Synchronous download of youtube video
     *
     * @param link             link of video present in youtube
     * @param downloadCallback callback to call after download compete
     */
    @Override
    public void download(String link, DownloadCallback downloadCallback) {
        if (!this.resourceService.existByDownloadUrl(link)) {
            String videoId = this.extractVideoId(link);
            VideoInfo videoInfo = this.getVideoInfo(videoId);
            Format format = videoInfo.bestVideoFormat();
            RequestVideoFileDownload request = new RequestVideoFileDownload(format)
                    .callback(new YoutubeProgressCallback<File>() {
                        @Override
                        public void onDownloading(int i) {
                            downloadCallback.onDownload(i);
                            logger.info(
                                    String.format(
                                            "Downloading... %d ",
                                            i
                                    )
                            );
                        }

                        @Override
                        public void onFinished(File file) {
                            ResourceDAO resourceDAO = saveWebM(videoInfo, file, link);
                            resourceDAO = convertMp4(resourceDAO);
                            downloadCallback.onFinished(resourceDAO);
                            logger.info("Download complete");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            downloadCallback.onError(throwable);
                        }
                    })
                    .saveTo(this.youtubeDownloadDir)
                    .renameTo(videoInfo.details().title().replace(" ", "_"))
                    .async();
            this.youtubeDownloader.downloadVideoFile(request);
        } else {
            downloadCallback.onDownload(100);
            downloadCallback.onFinished(
                    this.resourceService.findByDownloadUrl(link).get()
            );
        }

    }

    private ResourceDAO saveWebM(VideoInfo videoInfo, File file, String link) {
        ResourceDAO resourceDAO = new ResourceDAO();
        resourceDAO.setDescription(
                videoInfo.details().description()
        );
        resourceDAO.setThumbnailUrls(videoInfo.details().thumbnails());
        resourceDAO.setMovie(file);
        resourceDAO.setIsMovie(true);
        resourceDAO.setTitle(videoInfo.details().title());
        resourceDAO.setResolutions(new ArrayList<>());
        resourceDAO.setDownloadUrl(link);
        resourceDAO.setFormat(ConvertRequest.Format.WEBM);
        ResolutionDAO resolutionDAO = new ResolutionDAO();
        resolutionDAO.setWidth(videoInfo.bestVideoFormat().width());
        resolutionDAO.setHeight(videoInfo.bestVideoFormat().height());
        resolutionDAO = resolutionService.save(resolutionDAO);
        resourceDAO.getResolutions().add(resolutionDAO);
        return resourceService.save(resourceDAO);
    }

    private ResourceDAO convertMp4(ResourceDAO resourceDAO) {
        ConvertRequest convertRequest = new ConvertRequest();
        convertRequest.setSourceFormat(ConvertRequest.Format.WEBM);
        convertRequest.setFileToConvert(resourceDAO.getMovie());
        convertRequest.setDestinationFolder(this.youtubeDownloadDir);
        try {
            return this.formatConverter.convert(convertRequest);
        } catch (IOException | InterruptedException e) {
            logger.error(
                    String.format("Cannot download video, exception: %s",
                            e.getMessage()
                    )
            );
            return resourceDAO;
        }
    }
}
