package cloud.ptl.povserver.service.download;

public interface DownloadService {
    void download(String locator, DownloadCallback downloadCallback);
}