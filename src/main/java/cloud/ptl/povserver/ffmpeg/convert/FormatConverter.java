package cloud.ptl.povserver.ffmpeg.convert;

import cloud.ptl.povserver.data.model.ResourceDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class FormatConverter {
    private final List<ResourceConverter> resourceConverters;

    public FormatConverter(List<ResourceConverter> resourceConverters) {
        this.resourceConverters = resourceConverters;
    }

    public ResourceDAO convert(ConvertRequest convertRequest) throws IOException, InterruptedException {
        return this.resourceConverters.stream()
                .filter(e -> e.supports(convertRequest.getSourceFormat()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "We do not support this file type: " + convertRequest.getSourceFormat().toString()
                        )
                ).convert(convertRequest);
    }
}
