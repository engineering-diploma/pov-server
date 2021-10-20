package cloud.ptl.povserver.ffmpeg;

// ffmpeg -i 'I made Brett do this....webm' -s 1280x720 -vcodec libx265 'I made Brett do this...1280x720.mp4'

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.ffmpeg.convert.ConvertRequest;
import cloud.ptl.povserver.ffmpeg.convert.FormatConverter;
import cloud.ptl.povserver.ffmpeg.resize.ResizeRequest;
import cloud.ptl.povserver.ffmpeg.resize.ResizeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service is used to call external program to convert vide into other formats.
 */
@Service
@Slf4j
public class FfmpegMediator {
    private final ResizeService resizeService;
    private final FormatConverter formatConverter;

    public FfmpegMediator(ResizeService resizeService, FormatConverter formatConverter) {
        this.resizeService = resizeService;
        this.formatConverter = formatConverter;
    }

    /**
     * Resize given resource into given format
     *
     * @param resizeRequest request containing all information to conduct resizing
     * @return resized resource
     * @throws IOException if file cannot be found
     */
    public ResourceDAO resize(ResizeRequest resizeRequest) throws IOException, InterruptedException {
        return this.resizeService.resize(resizeRequest);
    }

    public ResourceDAO convert(ConvertRequest convertRequest) throws IOException, InterruptedException {
        return this.formatConverter.convert(convertRequest);
    }

    public static ConvertRequest.Format findFormat(String locator) {
        if (locator.endsWith(".gif")) return ConvertRequest.Format.GIF;
        else if (locator.endsWith(".mp4")) return ConvertRequest.Format.MP4;
        else throw new IllegalArgumentException(
                    String.format(
                            "Unrecognized file format: %s",
                            locator.substring(
                                    locator.lastIndexOf(".")
                            )
                    )
            );
    }

}
