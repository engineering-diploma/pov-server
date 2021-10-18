package cloud.ptl.povserver.api.metadata;

import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.metadata.Metadata;
import cloud.ptl.povserver.service.metadata.MetadataService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MetadataController {
    private MetadataService metadataService;

    @GetMapping("/metadata/{id}")
    public Metadata getMetadata(
            @PathVariable Long id
    ) throws NotFoundException, IOException {
        return this.metadataService.getMetadata(id);
    }
}
