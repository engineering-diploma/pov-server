package cloud.ptl.povserver.service.download;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.ffmpeg.FfmpegMediator;
import cloud.ptl.povserver.ffmpeg.convert.ConvertRequest;
import cloud.ptl.povserver.service.resource.ResourceService;
import org.atmosphere.util.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Service
public class GeneralDownloadService implements DownloadService {
    private final ResourceService resourceService;
    private final FfmpegMediator ffmpegService;

    private File downloadDir;
    private File convertedDir;

    @Value("${ptl.download.raw}")
    private String downloadPath;
    @Value("${ptl.download.converted}")
    private String convertedPath;

    public GeneralDownloadService(ResourceService resourceService, FfmpegMediator ffmpegService) {
        this.resourceService = resourceService;
        this.ffmpegService = ffmpegService;
    }

    @PostConstruct
    public void init() {
        this.downloadDir = new File(this.downloadPath);
        this.downloadDir.mkdir();
        this.convertedDir = new File(this.convertedPath);
        this.convertedDir.mkdir();
    }

    @Override
    public void download(String locator, DownloadCallback downloadCallback) throws Exception {
        this.downloadFromURL(locator);
        downloadCallback.onDownload(100);
        if (!this.resourceService.existByDownloadUrl(locator)) {
            ResourceDAO converted =
                    this.convert(
                            locator
                    );
            converted.setThumbnailUrls(List.of(locator));
            converted.setDownloadUrl(locator);
            converted =
                    this.resourceService.save(converted);
            downloadCallback.onFinished(converted);
        } else {
            downloadCallback.onFinished(
                    this.resourceService.findByDownloadUrl(locator).get()
            );
        }
    }

    private void downloadFromURL(String locator) throws Exception {
        byte[] b = new byte[1];
        URL url = new URL(locator);
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        DataInputStream di = new DataInputStream(urlConnection.getInputStream());

        FileOutputStream fo =
                new FileOutputStream(
                        this.downloadPath + File.separator + this.fileNameExtractor(url)
                );
        while (-1 != di.read(b, 0, 1))
            fo.write(b, 0, 1);
        di.close();
        fo.close();
    }

    private ResourceDAO convert(String locator) throws Exception {
        String fileName = this.fileNameExtractor(locator);
        File fileToConvert = new File(
                this.downloadPath + File.separator + fileName);
        ConvertRequest convertRequest = new ConvertRequest();
        convertRequest.setFileToConvert(fileToConvert);
        convertRequest.setDestinationFolder(this.convertedDir);
        convertRequest.setSourceFormat(
                FfmpegMediator.findFormat(locator)
        );
        return this.ffmpegService.convert(convertRequest);
    }

    private String fileNameExtractor(String url) throws Exception {
        return StringEscapeUtils.escapeJava(url).replace("/", "__");
    }

    private String fileNameExtractor(URL url) throws Exception {
        return this.fileNameExtractor(
                url.getProtocol() + "://" + url.getHost() + url.getPath()
        );
    }
}
