package cloud.ptl.povserver.service.download;

import org.springframework.stereotype.Service;

@Service
public class DownloadMediator {
    private YTDownloadService ytDownloadService;
    private GeneralDownloadService generalDownloadService;

    public DownloadMediator(YTDownloadService ytDownloadService, GeneralDownloadService generalDownloadService) {
        this.ytDownloadService = ytDownloadService;
        this.generalDownloadService = generalDownloadService;
    }

    public void download(String link, DownloadCallback downloadCallback) throws Exception {
        if (link.contains("https://www.youtube.com/")) {
            this.ytDownloadService.download(link, downloadCallback);
        } else {
            this.generalDownloadService.download(link, downloadCallback);
        }
    }
}
