package cloud.ptl.povserver.ffmpeg.convert;

import cloud.ptl.povserver.data.model.ResourceDAO;

import java.io.IOException;

public interface ResourceConverter {
    boolean supports(ConvertRequest.Format format);

    ResourceDAO convert(ConvertRequest convertRequest) throws IOException, InterruptedException;
}
