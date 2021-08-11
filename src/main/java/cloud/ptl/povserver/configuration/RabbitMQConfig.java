package cloud.ptl.povserver.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RabbitMQConfig {
    @Bean
    public ConnectionFactory connectionFactory() throws NoSuchAlgorithmException, KeyManagementException {
        com.rabbitmq.client.ConnectionFactory connection = new com.rabbitmq.client.ConnectionFactory();
        connection.setHost("ptl.cloud");
        connection.setPort(5671);
        connection.useSslProtocol();
        connection.setUsername("pov");
        connection.setPassword("pov11082021");
        return new CachingConnectionFactory(connection);
    }

    @Bean
    public AmqpAdmin amqpAdmin() throws NoSuchAlgorithmException, KeyManagementException {
        return new RabbitAdmin(this.connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() throws NoSuchAlgorithmException, KeyManagementException {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(this.connectionFactory());
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue serverToPovControlQueue() {
        return QueueBuilder
                .durable("server-to-pov-control-queue")
                .build();
    }

    @Bean
    public DirectExchange serverToPovControlExchange() {
        return ExchangeBuilder
                .directExchange("server-to-pov-control-exchange")
                .build();
    }

    @Bean
    public Binding serverToPovControlBinding() {
        return BindingBuilder
                .bind(this.serverToPovControlQueue())
                .to(this.serverToPovControlExchange())
                .with("server-to-pov-control-binding");
    }
}
