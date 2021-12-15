package cloud.ptl.povserver.service.frame;

import cloud.ptl.povserver.data.model.ResourceDAO;
import lombok.Data;

@Data
public class PovFrameRequest {
    private int width;
    private int height;
    private int samplingInterval;
    private ResourceDAO resourceDAO;
    private String ledStrip;
}
