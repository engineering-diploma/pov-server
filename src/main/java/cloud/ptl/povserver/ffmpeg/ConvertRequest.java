package cloud.ptl.povserver.ffmpeg;

import lombok.Data;

import java.io.File;

@Data
public class ConvertRequest {
    private File fileToConvert;
    private File destinationFolder;
}
