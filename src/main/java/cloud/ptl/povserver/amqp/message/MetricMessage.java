package cloud.ptl.povserver.amqp.message;

import lombok.Data;

/**
 * Control message received from display. Contains basic informations about display state
 */
@Data
public class MetricMessage {
    private String key;
    private Float value;
}
