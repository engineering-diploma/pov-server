package cloud.ptl.povserver.amqp;

import cloud.ptl.povserver.amqp.message.ControlMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitSender {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToPovDisplay(ControlMessage controllMessage) {
        // this.rabbitTemplate.convertAndSend();
    }
}
