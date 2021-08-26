package cloud.ptl.povserver.amqp;

import cloud.ptl.povserver.amqp.message.MetricMessage;
import cloud.ptl.povserver.data.model.MetricDAO;
import cloud.ptl.povserver.service.metric.MetricsService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RabbitListeners {

    private MetricsService metricsService;

    public RabbitListeners(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

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
        MetricDAO metricDAO = new MetricDAO();
        metricDAO.setKeyy(message.getKey());
        metricDAO.setValue(message.getValue());
        this.metricsService.save(metricDAO);
    }
}
