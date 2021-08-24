package cloud.ptl.povserver.amqp;

import cloud.ptl.povserver.amqp.message.VideoPingMessage;
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

    public void pingAboutNewVideo(VideoPingMessage videoPingMessage) {
        this.rabbitTemplate.convertAndSend(
                "server-to-pov-control-exchange",
                "server-to-pov-control-binding",
                videoPingMessage
        );
    }
}
