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
     * Synchronous download of youtube video
     *
     * @param videoId          id of video present in youtube link
     * @param downloadCallback callback to call after download compete
     */
    @Override
    public void download(String videoId, DownloadCallback downloadCallback) {
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

    }
}
