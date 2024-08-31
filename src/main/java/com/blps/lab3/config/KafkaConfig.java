package com.blps.lab3.config;

import com.blps.lab3.model.util.ExpertMessage;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, ExpertMessage> producerFactory(){
        Map<String,Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapAddress);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        properties.put(JsonSerializer.TYPE_MAPPINGS,"token: com.blps.lab3.model.util.ExpertMessage");
        return new DefaultKafkaProducerFactory<>(properties);

    }
    @Bean
    public ConsumerFactory<String, ExpertMessage> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES,"*");
        props.put(JsonDeserializer.TYPE_MAPPINGS,"token:com.blps.lab3.model.util.ExpertMessage");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public NewTopic testTopic(){
        return TopicBuilder.name("expertAudition").build();
    }

    @Bean NewTopic approvingTopic(){
        return TopicBuilder.name("expertResponse").build();
    }

    @Bean
    public KafkaTemplate<String,ExpertMessage> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean()
    public ConcurrentKafkaListenerContainerFactory<String, ExpertMessage> userKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ExpertMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }



}
