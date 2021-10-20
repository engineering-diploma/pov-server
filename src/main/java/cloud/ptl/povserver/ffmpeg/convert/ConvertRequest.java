package cloud.ptl.povserver.ffmpeg.convert;

import lombok.Data;

import java.io.File;

@Data
public class ConvertRequest {
    private Format sourceFormat;

    private File fileToConvert;
    private File destinationFolder;

    public static enum Format {
        GIF, MP4
    }
}
