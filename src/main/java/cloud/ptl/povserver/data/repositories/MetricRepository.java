package cloud.ptl.povserver.data.repositories;

import cloud.ptl.povserver.data.model.MetricDAO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface MetricRepository extends CrudRepository<MetricDAO, Long> {
    Collection<MetricDAO> findAllByKeyy(String key);

    Optional<MetricDAO> findFirstByOrderByCreationDesc();

    @Query("select sum(m.value) from metric m where m.keyy like 'rpm'")
    Float getTotalRPM();

    @Query("select sum(m.value)" +
            " from metric m" +
            " where m.keyy like 'rpm' and" +
            " m.creation >= :time"
    )
    Float getRPMSinceDate(@Param("time") LocalDateTime time);
}
