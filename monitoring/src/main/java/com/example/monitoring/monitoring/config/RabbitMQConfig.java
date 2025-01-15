package com.example.monitoring.monitoring.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue deviceChangesQueue() {
        return new Queue("device_changes", true);// Durable queue
    }

    @Bean
    public Queue energyMeasurementQueue() {
        return new Queue("energy_measurements", true);// Durable queue
    }
}
