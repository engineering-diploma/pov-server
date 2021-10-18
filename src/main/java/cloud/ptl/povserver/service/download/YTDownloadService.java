package cloud.ptl.povserver.service.download;

import cloud.ptl.povserver.data.model.ResolutionDAO;
import cloud.ptl.povserver.data.model.ResourceDAO;
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
    @Value("${ptl.download.youtube}")
    private String youtubeDownloadPath;
    private File youtubeDownloadDir;

    public YTDownloadService(ResolutionService resolutionService, ResourceService resourceService) {
        this.resolutionService = resolutionService;
        this.resourceService = resourceService;
    }

    @PostConstruct
    private void init() {
        this.youtubeDownloadDir = new File(this.youtubeDownloadPath);
        this.youtubeDownloadDir.mkdir();
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
                            logger.info("Downloading... " + i + "%");
                        }

                        @Override
                        public void onFinished(File file) {
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
                            ResolutionDAO resolutionDAO = new ResolutionDAO();
                            resolutionDAO.setWidth(videoInfo.bestVideoFormat().width());
                            resolutionDAO.setHeight(videoInfo.bestVideoFormat().height());
                            resolutionDAO = resolutionService.save(resolutionDAO);
                            resourceDAO.getResolutions().add(resolutionDAO);
                            resourceDAO = resourceService.save(resourceDAO);
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
            //RequestVideoFileDownload request = new RequestVideoFileDownload(format);
            Response<File> response = this.youtubeDownloader.downloadVideoFile(request);
//        response.data();
        } else {
            downloadCallback.onDownload(100);
            downloadCallback.onFinished(
                    this.resourceService.findByDownloadUrl(link).get()
            );
        }

    }
}
