package cloud.ptl.povserver.service.download;

import cloud.ptl.povserver.data.model.ResourceDAO;
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

@Service
public class DownloadService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YoutubeDownloader youtubeDownloader = new YoutubeDownloader();
    @Value("${ptl.download.youtube}")
    private String youtubeDownloadPath;
    @Value("${ptl.download.gif}")
    private String gifDownloadPath;
    @Value("${ptl.download.gif_converted}")
    private String convertedGifPath;
    private File youtubeDownloadDir;
    private File gifDownloadDir;
    private File gifConvertedDir;

    @PostConstruct
    private void init() {
        this.youtubeDownloadDir = new File(this.youtubeDownloadPath);
        this.gifDownloadDir = new File(this.gifDownloadPath);
        this.gifConvertedDir = new File(this.convertedGifPath);
    }

    public VideoInfo getVideoInfo(String videoId) {
        RequestVideoInfo request = new RequestVideoInfo(videoId);
        Response<VideoInfo> response = youtubeDownloader.getVideoInfo(request);
        return response.data();
    }

    public void downloadYoutubeVideo(String videoId, DownloadCallback downloadCallback) {
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
                        downloadCallback.onFinished(resourceDAO);
                        logger.info("Download complete");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        downloadCallback.onError(throwable);
                    }
                })
                .saveTo(this.youtubeDownloadDir)
                .renameTo(videoInfo.details().title())
                .async();
        //RequestVideoFileDownload request = new RequestVideoFileDownload(format);
        Response<File> response = this.youtubeDownloader.downloadVideoFile(request);
//        response.data();

    }
}
