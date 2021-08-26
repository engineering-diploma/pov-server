package cloud.ptl.povserver.service.search;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.service.download.DownloadCallback;
import cloud.ptl.povserver.service.download.DownloadService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * This service is used by Search component of vaadin ui
 */
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

    /**
     * This method is used to check if given url is youtube url
     *
     * @param link link to check
     * @return decision if link is youtube complaint one
     */
    private boolean isYoutubeLink(String link) {
        return link.contains("https://www.youtube.com/");
    }

    /**
     * Check if link points to gif internet resource
     *
     * @param link link to check
     * @return decision if link contains gif or not
     */
    private boolean isGif(String link) {
        return link.endsWith(".gif");
    }

    /**
     * method used to download gif from internet
     *
     * @param link
     * @return
     */
    private ResourceDAO findGif(String link) {
        // TODO: implement
        return null;
    }

    /**
     * downloads an youtube movie
     *
     * @param link             link to youtube movie
     * @param downloadCallback callback to call after download
     */
    private void findYoutubeMovie(String link, DownloadCallback downloadCallback) {
        this.downloadService.downloadYoutubeVideo(
                this.extractVideoId(link), downloadCallback
        );
    }

    // sample https://www.youtube.com/watch?v=5q87K1WaoFI&ab_channel=WIRED

    /**
     * Extracts video id from youtube link.
     * Id is store in v path variable
     *
     * @param link link from which we should extract video
     * @return extracted id
     */
    private String extractVideoId(String link) {
        String pathAttributesString = link.split("\\?")[1];
        String[] pathAttributes = pathAttributesString.split("&");
        String vAttribute = Arrays.stream(pathAttributes).sequential()
                .filter(el -> el.split("=")[0].equals("v"))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Missing argument v in url"));
        return vAttribute.split("=")[1];
    }
}
