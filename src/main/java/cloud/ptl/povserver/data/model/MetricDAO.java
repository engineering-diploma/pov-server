package cloud.ptl.povserver.data.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity(name = "metric")
@Table(name = "metric")
public class MetricDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyy;
    private Float value;

    @CreationTimestamp
    private LocalDateTime creation;

    public static MetricDAO empty() {
        MetricDAO metricDAO = new MetricDAO();
        metricDAO.setValue(0F);
        metricDAO.setKeyy("");
        return metricDAO;
    }
}
