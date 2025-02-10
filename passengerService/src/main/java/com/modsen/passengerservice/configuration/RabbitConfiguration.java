package com.modsen.passengerservice.configuration;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
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
    @Value("${spring.rabbitmq.rating.exchange}")
    private String ratingExchangeName;

    @Value("${spring.rabbitmq.user.exchange}")
    private String userExchangeName;

    @Value("${spring.rabbitmq.passenger.routing.key}")
    private String passengerRoutingKey;

    @Value("${spring.rabbitmq.passenger.queue}")
    private String passengerQueueName;

    @Value("${spring.rabbitmq.user.update.routing.key}")
    private String userUpdateRoutingKey;

    @Value("${spring.rabbitmq.user.update.queue}")
    private String userUpdateQueueName;

    @Value("${spring.rabbitmq.user.delete.routing.key}")
    private String userDeleteRoutingKey;

    @Value("${spring.rabbitmq.user.delete.queue}")
    private String userDeleteQueueName;

    @Bean
    public Queue queue() {
        return new Queue(passengerQueueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(ratingExchangeName);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(passengerRoutingKey);
    }

    @Bean
    public Declarables topicBindings() {
        Queue topicQueueUpdateUsers = new Queue(userUpdateQueueName, false);
        Queue topicQueueDeleteUsers = new Queue(userDeleteQueueName, false);

        TopicExchange topicExchange = new TopicExchange(userExchangeName);

        return new Declarables(topicQueueUpdateUsers, topicQueueDeleteUsers, topicExchange,
                BindingBuilder.bind(topicQueueUpdateUsers).to(topicExchange).with(userUpdateRoutingKey),
                BindingBuilder.bind(topicQueueDeleteUsers).to(topicExchange).with(userDeleteRoutingKey));
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
