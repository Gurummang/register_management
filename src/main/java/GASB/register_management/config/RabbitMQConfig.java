package GASB.register_management.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.init.queue}")
    private String initQueueName;

    @Value("${rabbitmq.init.routing-key}")
    private String initRoutingKey;

    @Bean
    Queue myQueue() {
        return new Queue(initQueueName, true, false, false);
    }

    @Bean
    Binding initQueueBinding(Queue myQueue, DirectExchange exchange) {
        return BindingBuilder.bind(myQueue).to(exchange).with(initRoutingKey);
    }

    // 교환기(Exchange) 설정
    @Bean
    DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    // RabbitTemplate 설정 (기본 라우팅 키 사용)
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(exchangeName);
        return rabbitTemplate;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getRoutingKey() {
        return initRoutingKey;
    }
}
