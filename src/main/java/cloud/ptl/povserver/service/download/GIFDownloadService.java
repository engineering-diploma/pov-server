package cloud.ptl.povserver.service.download;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.ffmpeg.ConvertRequest;
import cloud.ptl.povserver.ffmpeg.FfmpegService;
import cloud.ptl.povserver.service.resource.ResolutionService;
import cloud.ptl.povserver.service.resource.ResourceService;
import org.atmosphere.util.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Service
public class GIFDownloadService implements DownloadService {

    private final String DOWNLOAD_COMMAND = "ffmpeg -i %s -c:v libvpx-vp9 -crf 30 -b:v 0 -b:a 128k -c:a libopus -s %sx%s %s";

    private final ResolutionService resolutionService;
    private final ResourceService resourceService;
    private final FfmpegService ffmpegService;

    private File gifDownloadDir;
    private File gifConvertedDir;

    @Value("${ptl.download.gif}")
    private String gifDownloadPath;
    @Value("${ptl.download.gif_converted}")
    private String convertedGifPath;

    public GIFDownloadService(ResolutionService resolutionService, ResourceService resourceService, FfmpegService ffmpegService) {
        this.resolutionService = resolutionService;
        this.resourceService = resourceService;
        this.ffmpegService = ffmpegService;
    }

    @PostConstruct
    public void init() {
        this.gifDownloadDir = new File(this.gifDownloadPath);
        this.gifDownloadDir.mkdir();
        this.gifConvertedDir = new File(this.convertedGifPath);
        this.gifConvertedDir.mkdir();
    }

    @Override
    public void download(String locator, DownloadCallback downloadCallback) throws Exception {
        this.downloadFromURL(locator);
        downloadCallback.onDownload(100);
        ResourceDAO converted =
                this.convert(
                        this.fileNameExtractor(locator)
                );
        converted.setThumbnailUrls(List.of(locator));
        converted =
                this.resourceService.save(converted);
        downloadCallback.onFinished(converted);
    }

    private void downloadFromURL(String locator) throws Exception {
        byte[] b = new byte[1];
        URL url = new URL(locator);
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        DataInputStream di = new DataInputStream(urlConnection.getInputStream());

        FileOutputStream fo =
                new FileOutputStream(
                        this.gifDownloadPath + File.separator + this.fileNameExtractor(url)
                );
        while (-1 != di.read(b, 0, 1))
            fo.write(b, 0, 1);
        di.close();
        fo.close();
    }

    private ResourceDAO convert(String fileName) throws IOException, InterruptedException {
        File fileToConvert = new File(
                this.gifDownloadPath + File.separator + fileName);
        ConvertRequest convertRequest = new ConvertRequest();
        convertRequest.setFileToConvert(fileToConvert);
        convertRequest.setDestinationFolder(this.gifConvertedDir);
        return this.ffmpegService.convertGifToWebM(convertRequest);
    }

    private String fileNameExtractor(String url) throws Exception {

        if (url.endsWith(".gif")) return StringEscapeUtils.escapeJava(url).replace("/", "__");
        else return StringEscapeUtils.escapeJava(url + ".gif").replace("/", "__");
    }

    private String fileNameExtractor(URL url) throws Exception {
        return this.fileNameExtractor(
                url.getProtocol() + "://" + url.getHost() + url.getPath()
        );
    }
}
