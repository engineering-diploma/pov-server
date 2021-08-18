package cloud.ptl.povserver.service.download;

import cloud.ptl.povserver.data.model.ResourceDAO;

public interface DownloadCallback {
    void onDownload(int progress);

    void onFinished(ResourceDAO resourceDAO);

    void onError(Throwable throwable);
}
