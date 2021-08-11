package cloud.ptl.povserver.amqp;

import cloud.ptl.povserver.amqp.message.ControlMessage;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RabbitListeners {
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
    public void displayControlListener(@Payload ControlMessage controllMessage) {

    }
}
