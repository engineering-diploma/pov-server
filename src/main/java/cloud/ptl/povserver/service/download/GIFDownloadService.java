package cloud.ptl.povserver.service.download;

import cloud.ptl.povserver.service.resource.ResolutionService;
import cloud.ptl.povserver.service.resource.ResourceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
public class GIFDownloadService implements DownloadService {

    private final ResolutionService resolutionService;
    private final ResourceService resourceService;

    private File gifDownloadDir;
    private File gifConvertedDir;

    @Value("${ptl.download.gif}")
    private String gifDownloadPath;
    @Value("${ptl.download.gif_converted}")
    private String convertedGifPath;

    public GIFDownloadService(ResolutionService resolutionService, ResourceService resourceService) {
        this.resolutionService = resolutionService;
        this.resourceService = resourceService;
    }

    @PostConstruct
    public void init() {
        this.gifDownloadDir = new File(this.gifDownloadPath);
        this.gifConvertedDir = new File(this.convertedGifPath);
    }

    @Override
    public void download(String locator, DownloadCallback downloadCallback) {

    }
}
