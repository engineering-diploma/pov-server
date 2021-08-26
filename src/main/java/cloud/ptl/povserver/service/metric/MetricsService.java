package cloud.ptl.povserver.service.metric;

import cloud.ptl.povserver.data.model.MetricDAO;
import cloud.ptl.povserver.data.repositories.MetricRepository;
import cloud.ptl.povserver.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MetricsService {

    private final MetricRepository metricRepository;
    private final List<MetricCallback> callbacks = new ArrayList<>();

    public MetricsService(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    public void registerCallback(MetricCallback metricCallback) {
        this.callbacks.add(metricCallback);
    }

    private void notifyAllCallbacks() {
        this.callbacks.forEach(MetricCallback::onNewMetric);
    }

    public MetricDAO save(MetricDAO metricDAO) {
        MetricDAO metricDAO1 = this.metricRepository.save(metricDAO);
        this.notifyAllCallbacks();
        return metricDAO1;
    }

    public MetricDAO findByKey(String key) throws NotFoundException {
        Optional<MetricDAO> optionalMetric = this.metricRepository.findByKeyy(key);
        if (optionalMetric.isEmpty()) throw new NotFoundException("Metric with key: " + key + " not found");
        else return optionalMetric.get();
    }

    public List<MetricDAO> getAllRPMMetrics() {
        return (List<MetricDAO>) this.metricRepository.findAllByKeyy("rpm");
    }

    public Float getRPM() {
        Optional<MetricDAO> optionalMetricDAO = this.metricRepository.findFirstByOrderByCreationDesc();
        if (optionalMetricDAO.isPresent()) return optionalMetricDAO.get().getValue();
        else return 0F;
    }

    public Float getTotalRPM() {
        return this.metricRepository.getTotalRPM();
    }

    public Float getLastMinuteRPM() {
        return this.metricRepository.getRPMSinceDate(LocalDateTime.now().minus(1, ChronoUnit.MINUTES));
    }
}
