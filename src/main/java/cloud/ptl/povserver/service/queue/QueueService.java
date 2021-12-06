package cloud.ptl.povserver.service.queue;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.service.resource.ResourceService;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Service used to control queue component of vaadin interface
 */
@Service
public class QueueService {
    private final ResourceService resourceService;

    public QueueService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public Collection<ResourceDAO> findAllMP4Resources() {
        return this.resourceService.findAllMP4Resources();
    }

    public void deleteResource(ResourceDAO resourceDAO) {
        this.resourceService.deleteRelatedTo(resourceDAO);
        this.resourceService.delete(resourceDAO);
    }

    public void moveResourceUp(ResourceDAO resourceDAO) {
        this.resourceService.moveResourcesUp(resourceDAO);
    }

    public void moveResourceDown(ResourceDAO resourceDAO) {
        this.resourceService.moveResourcesDown(resourceDAO);
    }
}
