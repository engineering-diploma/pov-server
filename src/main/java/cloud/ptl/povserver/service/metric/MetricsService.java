package cloud.ptl.povserver.service.metric;

import cloud.ptl.povserver.data.model.MetricDAO;
import cloud.ptl.povserver.data.repositories.MetricRepository;
import cloud.ptl.povserver.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service used to gather information about system state.
 * Information can be fetched from server but also from pov display via rabbit and mqs
 */
@Service
public class MetricsService {

    @Value("${ptl.display.diameter}")
    private String diameter;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MetricRepository metricRepository;
    // this callback are called whenever new metric is present
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

    /**
     * Saves metric into db
     *
     * @param metricDAO metric to save
     * @return saved metric
     */
    public MetricDAO save(MetricDAO metricDAO) {
        MetricDAO metricDAO1 = this.metricRepository.save(metricDAO);
        if (metricDAO.getKeyy().equals(MetricKeys.RPM.getName())) this.notifyAllCallbacks();
        return metricDAO1;
    }

    /**
     * Finds metric by it key. If none exist new one is created
     *
     * @param key metric key
     * @return metric
     * @throws NotFoundException if any metric was found
     */
    public MetricDAO findByKey(String key) throws NotFoundException {
        Optional<MetricDAO> optionalMetric = this.metricRepository.findByKeyy(key);
        if (optionalMetric.isEmpty()) {
            this.logger.info("Metric with key: " + key + " not found");
            MetricDAO metricDAO = new MetricDAO();
            metricDAO.setKeyy(key);
            metricDAO.setValue(0F);
            this.save(metricDAO);
            return this.findByKey(key);
        } else return optionalMetric.get();
    }

    /**
     * Update amount of data transferred into pov display, this metric is gathered in its own via server
     *
     * @param amountAdded number of bytes send to display
     */
    public void updateSendDataAmount(Float amountAdded) {
        MetricDAO metricDAO;
        try {
            metricDAO = this.findByKey(MetricKeys.DATA_TRANSFERRED_TO_DISPLAY.getName());
            metricDAO.setValue(
                    metricDAO.getValue() + amountAdded
            );
        } catch (NotFoundException e) {
            metricDAO = new MetricDAO();
            metricDAO.setKeyy(MetricKeys.DATA_TRANSFERRED_TO_DISPLAY.getName());
            metricDAO.setValue(amountAdded);
        }
        this.save(metricDAO);
    }

    /**
     * Calculate angle speed based on rpms
     *
     * @return angle speed in deg/s
     * @throws NotFoundException if metric with rpms was not found
     */
    public Float getAngleSpeed() throws NotFoundException {
        Float rpm = this.findByKey(MetricKeys.RPM.getName()).getValue();
        return rpm * 2 * (float) Math.PI;
    }

    /**
     * Calculate tangential speed based on rpms
     *
     * @return speed in km/h
     * @throws NotFoundException if rpm metric was not found
     */
    public Float getTangentialSpeed() throws NotFoundException {
        Float rpm = this.findByKey(MetricKeys.RPM.getName()).getValue();
        Float radius = (float) Math.PI * Float.parseFloat(this.diameter) / 200F; // in meters
        return rpm * 2 * radius * 3.6F;
    }
}
