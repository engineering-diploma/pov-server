package cloud.ptl.povserver.data.repositories;


import cloud.ptl.povserver.data.model.ResourceDAO;
import cloud.ptl.povserver.ffmpeg.convert.ConvertRequest;
import org.springframework.data.repository.CrudRepository;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

public interface ResourceRepository extends CrudRepository<ResourceDAO, Long> {
    boolean existsByDownloadUrl(String downloadUrl);

    Optional<ResourceDAO> findByDownloadUrl(String downloadUrl);

    Optional<ResourceDAO> findByMovie(File file);

    Collection<ResourceDAO> findAllByFormatEquals(ConvertRequest.Format format);
}
