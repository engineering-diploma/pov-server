package cloud.ptl.povserver.amqp;

import cloud.ptl.povserver.amqp.message.VideoPingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Sending point of rabbit messages
 */
@Component
public class RabbitSender {
    private final RabbitTemplate rabbitTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public RabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Pings display about new video to display
     *
     * @param videoPingMessage message to send to display, proper converter should be used
     */
    public void pingAboutNewVideo(VideoPingMessage videoPingMessage) {
        this.logger.info("Sending video ping to display: " + videoPingMessage);
        this.rabbitTemplate.convertAndSend(
                "server-to-pov-control-exchange",
                "server-to-pov-control-binding",
                videoPingMessage
        );
    }
}
