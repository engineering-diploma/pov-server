package cloud.ptl.povserver.service.metric;

import cloud.ptl.povserver.data.model.MetricDAO;
import cloud.ptl.povserver.data.repositories.MetricRepository;
import cloud.ptl.povserver.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MetricsService {

    @Value("${ptl.display.diameter}")
    private String diameter;

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

    public Float getAngleSpeed() throws NotFoundException {
        Float rpm = this.findByKey(MetricKeys.RPM.getName()).getValue();
        return rpm * 2 * (float) Math.PI;
    }

    public Float getTangentialSpeed() throws NotFoundException {
        Float rpm = this.findByKey(MetricKeys.RPM.getName()).getValue();
        Float radius = (float) Math.PI * Float.parseFloat(this.diameter) / 200F; // in meters
        return rpm * 2 * radius * 3.6F;
    }

    public Float getDataTransferred() {
        return 100F;
    }
}
