package cloud.ptl.povserver.service.resource;

import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.data.repositories.ResourceRepository;
import cloud.ptl.povserver.exception.NotFoundException;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Everything we store in system is resource. It can be gif, picture or movie.
 * This resource is used to manage resource entity
 */
@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public Collection<ResourceDAO> findAllResources() {
        return (Collection<ResourceDAO>) this.resourceRepository.findAll();
    }

    public ResourceDAO findById(Long id) throws NotFoundException {
        Optional<ResourceDAO> optionalResource =
                this.resourceRepository.findById(id);
        if (optionalResource.isEmpty())
            throw new NotFoundException("Resource with id=" + id + " not found");
        else
            return optionalResource.get();
    }

    public ResourceDAO save(ResourceDAO resourceDAO) {
        return this.resourceRepository.save(resourceDAO);
    }

    public void delete(ResourceDAO resourceDAO) {
        FileUtils.deleteQuietly(resourceDAO.getMovie());
        this.resourceRepository.delete(resourceDAO);
    }

    public boolean existByDownloadUrl(String donwloadUrl) {
        return this.resourceRepository.existsByDownloadUrl(donwloadUrl);
    }

    public Optional<ResourceDAO> findByDownloadUrl(String url) {
        return this.resourceRepository.findByDownloadUrl(url);
    }

    /**
     * Resource is bounded to order value, which is used in queue component of ui. This method move resource one
     * position up
     *
     * @param resourceDAO resource to move
     */
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

    /**
     * Resource is bounded to order value, which is used in queue component of ui. This method move resource one
     * position down
     *
     * @param resourceDAO resource to move down
     */
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
