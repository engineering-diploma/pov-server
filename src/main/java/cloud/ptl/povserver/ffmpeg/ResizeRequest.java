package cloud.ptl.povserver.ffmpeg;

import cloud.ptl.povserver.data.model.ResourceDAO;
import lombok.Data;

/**
 * Information container for resizing resource
 */
@Data
public class ResizeRequest {
    private ResourceDAO resourceDAO;
    private int width;
    private int height;
}
