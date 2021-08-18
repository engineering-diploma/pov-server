package cloud.ptl.povserver.service.search;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.service.download.DownloadCallback;
import cloud.ptl.povserver.service.download.DownloadService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SearchService {

    private final DownloadService downloadService;

    public SearchService(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    public void findResourceByLink(String link, DownloadCallback downloadCallback) {
        if (isGif(link)) this.findGif(link);
        else if (isYoutubeLink(link)) this.findYoutubeMovie(link, downloadCallback);
        else
            throw new IllegalArgumentException("Link " + link + " cannot be downloaded as source is not supported by download service");
    }

    // samples
    // https://www.youtube.com/watch?v=5q87K1WaoFI&ab_channel=WIRED
    private boolean isYoutubeLink(String link) {
        return link.contains("https://www.youtube.com/");
    }

    private boolean isGif(String link) {
        return link.endsWith(".gif");
    }

    private ResourceDAO findGif(String link) {
        return null;
    }

    private void findYoutubeMovie(String link, DownloadCallback downloadCallback) {
        this.downloadService.downloadYoutubeVideo(
                this.extractVideoId(link), downloadCallback
        );
    }

    // sample https://www.youtube.com/watch?v=5q87K1WaoFI&ab_channel=WIRED
    private String extractVideoId(String link) {
        String pathAttributesString = link.split("\\?")[1];
        String[] pathAttributes = pathAttributesString.split("&");
        String vAttribute = Arrays.stream(pathAttributes).sequential()
                .filter(el -> el.split("=")[0].equals("v"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Missing argument v in url"));
        return vAttribute.split("=")[1];
    }
}
