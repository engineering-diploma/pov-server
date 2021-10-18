package cloud.ptl.povserver.service.metadata;

import lombok.Data;

@Data
public class Metadata {
    private String fileName;
    private String location;
    private String title;
    private String description;
    private String downloadUrl;
    private Integer duration;
    private Integer height;
    private Integer width;
    private Integer frameRate;
    private String systemFormat;
    private String ffmpegFormat;
    private String codec;
}
