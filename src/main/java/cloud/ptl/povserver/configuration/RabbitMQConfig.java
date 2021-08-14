package cloud.ptl.povserver.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Profile("!test")
@Configuration
public class RabbitMQConfig {
    @Value("${ptl.rabbit.host}")
    private String host;

    @Value("${ptl.rabbit.port}")
    private int port;

    @Value("${ptl.rabbit.username}")
    private String username;

    @Value("${ptl.rabbit.password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() throws NoSuchAlgorithmException, KeyManagementException {
        com.rabbitmq.client.ConnectionFactory connection = new com.rabbitmq.client.ConnectionFactory();
        connection.setHost(this.host);
        connection.setPort(this.port);
        connection.useSslProtocol();
        connection.setUsername(this.username);
        connection.setPassword(this.password);
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
