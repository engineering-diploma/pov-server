package cloud.ptl.povserver.amqp;

import cloud.ptl.povserver.amqp.message.MetricMessage;
import cloud.ptl.povserver.data.model.MetricDAO;
import cloud.ptl.povserver.exception.NotFoundException;
import cloud.ptl.povserver.service.metric.MetricsService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Listeners for rabbit MQ
 */
@Component
public class RabbitListeners {

    private final MetricsService metricsService;

    public RabbitListeners(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    /**
     * Main listener
     *
     * @param message received payload of message
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = "pov-to-server-control-queue",
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            name = "pov-to-server-control-exchange"
                    ),
                    key = "pov-to-server-control-binding"
            )
    )
    public void displayControlListener(@Payload MetricMessage message) {
        // here we would like to update message or create new if any exists
        MetricDAO metricDAO = null;
        try {
            metricDAO = this.metricsService.findByKey(message.getKey());
            metricDAO.setValue(message.getValue());
        } catch (NotFoundException e) {
            metricDAO = new MetricDAO();
            metricDAO.setKeyy(message.getKey());
            metricDAO.setValue(message.getValue());
        }
        this.metricsService.save(metricDAO);
    }
}
