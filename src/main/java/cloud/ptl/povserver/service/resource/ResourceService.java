package cloud.ptl.povserver.service.resource;

import cloud.ptl.povserver.data.model.Format;
import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.data.repositories.ResourceRepository;
import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.frame.PovFrameRequest;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
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

    public Collection<ResourceDAO> findAllMP4Resources() {
        return this.resourceRepository.findAllByFormatEquals(Format.MP4);
    }

    public boolean existsByTitleAndIsFrames(String title) {
        return this.resourceRepository.existsByTitleAndFormat(title, Format.FRAMES);
    }

    public ResourceDAO findByTitleAndIsFrames(String title) throws NotFoundException {
        return this.resourceRepository
                .findByTitleAndFormat(title, Format.FRAMES)
                .orElseThrow(() -> new NotFoundException("Cannot find resource with title " + title));
    }

    public boolean exists(ResourceDAO resourceDAO) {
        return this.resourceRepository.existsById(resourceDAO.getId());
    }

    public ResourceDAO findById(Long id) throws NotFoundException {
        Optional<ResourceDAO> optionalResource =
                this.resourceRepository.findById(id);
        if (optionalResource.isEmpty())
            throw new NotFoundException("Resource with id=" + id + " not found");
        else
            return optionalResource.get();
    }

    public ResourceDAO findByFrameStream(File file) throws NotFoundException {
        return this.resourceRepository
                .findAllByFrameStream(file)
                .orElseThrow(() -> new NotFoundException(
                        String.format(
                                "Cannot find resource with given frame stream path: %s",
                                file.getAbsolutePath()
                        )
                ));
    }

    public ResourceDAO findByMovie(File file) throws NotFoundException {
        return this.resourceRepository
                .findByMovie(file)
                .orElseThrow(() -> new NotFoundException(
                        String.format(
                                "Cannot find resource with given movie path: %s",
                                file.getAbsolutePath()
                        )
                ));
    }

    public ResourceDAO save(ResourceDAO resourceDAO) {
        return this.resourceRepository.save(resourceDAO);
    }

    public void delete(ResourceDAO resourceDAO) {
        FileUtils.deleteQuietly(resourceDAO.getMovie());
        this.resourceRepository.delete(resourceDAO);
    }

    public List<ResourceDAO> findAllByTitleContaining(String title) {
        return this.resourceRepository.findAllByTitleContaining(title);
    }

    public List<ResourceDAO> findAllByTitleContainingAndIsFrameStream(String title) {
        return this.resourceRepository.findAllByTitleContainingAndFormat(title, Format.FRAMES);
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

    public ResourceDAO copyThumbnails(ResourceDAO from, ResourceDAO to) {
        from.getThumbnailUrls().forEach(el -> to.getThumbnailUrls().add(el));
        return to;
    }

    public void deleteRelatedTo(ResourceDAO resourceDAO) {
        List<ResourceDAO> related = this.findAllByTitleContaining(resourceDAO.getTitle());
        related.stream()
                .filter(el -> !el.equals(resourceDAO))
                .forEach(el -> {
                    if (el.getMovie() != null) el.getMovie().delete();
                    else el.getFrameStream().delete();
                });
    }

    public ResourceDAO markAsConversionStarted(ResourceDAO resourceDAO) {
        resourceDAO.setConversionOngoing(true);
        return this.resourceRepository.save(resourceDAO);
    }

    public void markAsConversionStarted(PovFrameRequest request) {
        request.setResourceDAO(
                this.markAsConversionStarted(
                        request.getResourceDAO()
                )
        );
    }

    public ResourceDAO markAsConversionEnded(ResourceDAO resourceDAO) {
        resourceDAO.setConversionOngoing(false);
        return this.resourceRepository.save(resourceDAO);
    }

    public void markAsConversionEnded(PovFrameRequest request) {
        request.setResourceDAO(
                this.markAsConversionEnded(
                        request.getResourceDAO()
                )
        );
    }
}
