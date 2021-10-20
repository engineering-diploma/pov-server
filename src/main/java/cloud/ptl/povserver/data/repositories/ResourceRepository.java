package cloud.ptl.povserver.data.repositories;


import cloud.ptl.povserver.data.model.ResourceDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ResourceRepository extends CrudRepository<ResourceDAO, Long> {
    boolean existsByDownloadUrl(String downloadUrl);

    Optional<ResourceDAO> findByDownloadUrl(String downloadUrl);
}
