package cloud.ptl.povserver.amqp.message;

import lombok.Data;

@Data
public class ControlMessage {
    private String controlAction;
    private String content;
    private String mode;
}
