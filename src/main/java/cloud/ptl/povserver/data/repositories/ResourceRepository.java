package cloud.ptl.povserver.data.repositories;


import cloud.ptl.povserver.data.model.Format;
import cloud.ptl.povserver.data.model.ResourceDAO;
import org.springframework.data.repository.CrudRepository;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends CrudRepository<ResourceDAO, Long> {
    boolean existsByDownloadUrl(String downloadUrl);

    Optional<ResourceDAO> findByDownloadUrl(String downloadUrl);

    Optional<ResourceDAO> findAllByFrameStream(File file);

    Optional<ResourceDAO> findByMovie(File file);

    Optional<ResourceDAO> findByTitleAndFormat(String title, Format format);

    List<ResourceDAO> findAllByTitleContaining(String title);

    List<ResourceDAO> findAllByTitleContainingAndFormat(String title, Format format);

    Collection<ResourceDAO> findAllByFormatEquals(Format format);

    boolean existsByTitleAndFormat(String title, Format format);
}
