package com.modsen.ratingservice.configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Value("${spring.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${spring.rabbitmq.driver.routing.key}")
    private String driverRoutingKey;

    @Value("${spring.rabbitmq.passenger.routing.key}")
    private String passengerRoutingKey;

    @Value("${spring.rabbitmq.driver.queue}")
    private String driverQueueName;

    @Value("${spring.rabbitmq.passenger.queue}")
    private String passengerQueueName;

    @Bean
    public Declarables topicBindings() {
        Queue topicQueueDrivers = new Queue(driverQueueName, false);
        Queue topicQueuePassengers = new Queue(passengerQueueName, false);

        TopicExchange topicExchange = new TopicExchange(exchangeName);

        return new Declarables(topicQueueDrivers, topicQueuePassengers, topicExchange,
                BindingBuilder.bind(topicQueueDrivers).to(topicExchange).with(driverRoutingKey),
                BindingBuilder.bind(topicQueuePassengers).to(topicExchange).with(passengerRoutingKey));
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
