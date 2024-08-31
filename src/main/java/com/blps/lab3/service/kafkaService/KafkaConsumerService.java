package com.blps.lab3.service.kafkaService;

import com.blps.lab3.model.util.ExpertMessage;
import com.blps.lab3.service.controllerService.CreditService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private final CreditService creditService;

    public KafkaConsumerService(CreditService creditService) {
        this.creditService = creditService;
    }

    @KafkaListener(topics = "expertResponse", groupId = "my-group", containerFactory = "userKafkaListenerContainerFactory")
    public void receiveMessage(ExpertMessage expertMessage) {
        creditService.loadExpertMessageFromAudition(expertMessage);
        System.out.println(expertMessage);

    }

}
