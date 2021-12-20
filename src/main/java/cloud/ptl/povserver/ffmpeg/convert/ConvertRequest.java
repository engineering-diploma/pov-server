package cloud.ptl.povserver.ffmpeg.convert;

import cloud.ptl.povserver.data.model.Format;
import lombok.Data;

import java.io.File;

@Data
public class ConvertRequest {
    private Format sourceFormat;

    private File fileToConvert;
    private File destinationFolder;
}
