package cloud.ptl.povserver.amqp.message;

import lombok.Data;

@Data
public class MetricMessage {
    private String key;
    private Float value;
}
