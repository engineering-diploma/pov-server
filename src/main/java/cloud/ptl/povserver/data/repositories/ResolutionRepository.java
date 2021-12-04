package cloud.ptl.povserver.data.repositories;

import cloud.ptl.povserver.data.model.ResolutionDAO;
import cloud.ptl.povserver.data.model.ResourceDAO;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ResolutionRepository extends CrudRepository<ResolutionDAO, Long> {
    Optional<ResolutionDAO> findByResourcesContains(ResourceDAO resourceDAO);
}
