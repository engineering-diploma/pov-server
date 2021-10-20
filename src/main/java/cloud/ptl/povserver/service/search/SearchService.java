package cloud.ptl.povserver.service.search;

import cloud.ptl.povserver.service.download.DownloadCallback;
import cloud.ptl.povserver.service.download.DownloadMediator;
import org.springframework.stereotype.Service;

/**
 * This service is used by Search component of vaadin ui
 */
@Service
public class SearchService {

    private final DownloadMediator downloadMediator;

    public SearchService(DownloadMediator downloadMediator) {
        this.downloadMediator = downloadMediator;
    }

    public void findResourceByLink(String link, DownloadCallback downloadCallback) throws Exception {
        this.downloadMediator.download(link, downloadCallback);
    }
}
