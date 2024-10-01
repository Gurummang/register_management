package GASB.register_management.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.init.queue}")
    private String initQueueName;

    @Value("${rabbitmq.init.routing-key}")
    private String initRoutingKey;

    @Value("${rabbitmq.O365_INIT_QUEUE}")
    private String o365InitQueueName;

    @Value("${rabbitmq.O365_ROUTING_KEY}")
    private String o365RoutingKey;

    @Bean
    Queue myQueue() {
        return new Queue(initQueueName, true, false, false);
    }

    @Bean
    Queue o365Queue() {
        return new Queue(o365InitQueueName, true, false, false);
    }

    @Bean
    Binding initQueueBinding(Queue myQueue, DirectExchange exchange) {
        return BindingBuilder.bind(myQueue).to(exchange).with(initRoutingKey);
    }

    @Bean
    Binding o365QueueBinding(Queue o365Queue, DirectExchange exchange) {
        return BindingBuilder.bind(o365Queue).to(exchange).with(o365RoutingKey);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    // 메시지 변환기 설정 (Jackson2JsonMessageConverter)
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate 설정 (메시지 변환기 적용)
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(exchangeName);
        rabbitTemplate.setMessageConverter(jsonMessageConverter()); // 메시지 변환기 설정
        return rabbitTemplate;
    }

    public String getExchangeName() { return exchangeName; }

    public String getRoutingKey() {
        return initRoutingKey;
    }

    public String getO365RoutingKey() {
        return o365RoutingKey;
    }
}
