package cloud.ptl.povserver.data.repositories;


import cloud.ptl.povserver.data.model.ResourceDAO;
import org.springframework.data.repository.CrudRepository;

public interface ResourceRepository extends CrudRepository<ResourceDAO, Long> {
}
