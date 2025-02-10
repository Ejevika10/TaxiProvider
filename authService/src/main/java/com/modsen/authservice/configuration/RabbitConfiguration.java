package com.modsen.authservice.configuration;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Value("${spring.rabbitmq.user.exchange}")
    private String userExchangeName;

    @Value("${spring.rabbitmq.user.update.routing.key}")
    private String userUpdateRoutingKey;

    @Value("${spring.rabbitmq.user.update.queue}")
    private String userUpdateQueueName;

    @Value("${spring.rabbitmq.user.delete.routing.key}")
    private String userDeleteRoutingKey;

    @Value("${spring.rabbitmq.user.delete.queue}")
    private String userDeleteQueueName;

    @Bean
    public Queue userUpdateQueue() {
        return new Queue(userUpdateQueueName, false);
    }

    @Bean
    public Queue userDeleteQueue() {
        return new Queue(userDeleteQueueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(userExchangeName);
    }

    @Bean
    public Binding userUpdateBinding(Queue userUpdateQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userUpdateQueue).to(exchange).with(userUpdateRoutingKey);
    }

    @Bean
    public Binding userDeleteBinding(Queue userDeleteQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userDeleteQueue).to(exchange).with(userDeleteRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setObservationEnabled(true);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
