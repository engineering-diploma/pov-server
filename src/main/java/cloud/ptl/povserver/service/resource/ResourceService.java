package cloud.ptl.povserver.service.resource;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.data.repositories.ResourceRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public Collection<ResourceDAO> findAllResources() {
        return (Collection<ResourceDAO>) this.resourceRepository.findAll();
    }

    public ResourceDAO save(ResourceDAO resourceDAO) {
        return this.resourceRepository.save(resourceDAO);
    }

    public void delete(ResourceDAO resourceDAO) {
        FileUtils.deleteQuietly(resourceDAO.getMovie());
        this.resourceRepository.delete(resourceDAO);
    }

    public void moveResourcesUp(ResourceDAO resourceDAO) {
        List<ResourceDAO> resources = (List<ResourceDAO>) this.resourceRepository.findAll();
        resources.sort(Comparator.comparingLong(ResourceDAO::getOrderr));
        int idx =
                IntStream.range(0, resources.size())
                        .filter(el -> Objects.equals(resources.get(el).getId(), resourceDAO.getId()))
                        .findFirst()
                        .orElse(-1);
        if (idx == 0) return;
        else {
            long order = resourceDAO.getOrderr();
            resourceDAO.setOrderr(
                    resources.get(idx - 1).getOrderr()
            );
            resources.get(idx - 1).setOrderr(order);
            this.resourceRepository.save(resources.get(idx - 1));
            this.resourceRepository.save(resourceDAO);
        }
    }

    public void moveResourcesDown(ResourceDAO resourceDAO) {
        List<ResourceDAO> resources = (List<ResourceDAO>) this.resourceRepository.findAll();
        resources.sort(Comparator.comparingLong(ResourceDAO::getOrderr));
        int idx =
                IntStream.range(0, resources.size())
                        .filter(el -> Objects.equals(resources.get(el).getId(), resourceDAO.getId()))
                        .findFirst()
                        .orElse(-1);
        if (idx == resources.size() - 1) return;
        else {
            long order = resourceDAO.getOrderr();
            resourceDAO.setOrderr(
                    resources.get(idx + 1).getOrderr()
            );
            resources.get(idx + 1).setOrderr(order);
            this.resourceRepository.save(resources.get(idx + 1));
            this.resourceRepository.save(resourceDAO);
        }
    }
}
