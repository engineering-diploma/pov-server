package cloud.ptl.povserver.service.metadata;

import lombok.Data;

@Data
public class Metadata {
    private String fileName;
    private String location;
    private String title;
    private String description;
    private String downloadUrl;
    private Double duration;
    private Integer height;
    private Integer width;
    private Double frameRate;
    private String ffmpegFormat;
    private String codec;
}
