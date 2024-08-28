package com.blps.lab3.service;

import com.blps.lab3.dto.DebitOfferDTO;
import com.blps.lab3.model.mainDB.Cards;
import com.blps.lab3.model.mainDB.DebitOffer;
import com.blps.lab3.model.util.ExpertMessage;
import com.blps.lab3.model.mainDB.User;
import com.blps.lab3.repo.main.CardRepository;
import com.blps.lab3.repo.main.DebitRepository;
import com.blps.lab3.repo.main.UserRepository;
import com.blps.lab3.utils.Bonus;
import com.blps.lab3.utils.CardType;
import com.blps.lab3.utils.Goal;
import com.blps.lab3.utils.mapper.DebitCardMapper;
import com.blps.lab3.utils.mapper.DebitOfferMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class DebitService {
    private final CardType CARD_TYPE = CardType.DEBIT;
    private final CardRepository cardRepository;
    private final DebitOfferMapper debitOfferMapper;
    private final DebitCardMapper debitCardMapper;
    private final UserRepository userRepository;
    private final DebitRepository debitRepository;
    private final CommonService commonService;
    private final KafkaProducerService kafkaProducerService;


    public DebitService(CardRepository cardRepository, DebitOfferMapper debitOfferMapper, DebitCardMapper debitCardMapper, UserRepository userRepository, DebitRepository debitRepository, CommonService commonService, KafkaProducerService kafkaProducerService) {
        this.cardRepository = cardRepository;
        this.debitOfferMapper = debitOfferMapper;
        this.debitCardMapper = debitCardMapper;
        this.userRepository = userRepository;
        this.debitRepository = debitRepository;
        this.commonService = commonService;
        this.kafkaProducerService = kafkaProducerService;
    }

    public ResponseEntity<?> getCards(Long id) {

        ResponseEntity<?> offerCheckResponse = commonService.offerExistenceCheck(id, true, true);

        if (offerCheckResponse != null)
            return offerCheckResponse;

        DebitOffer debitOffer = debitRepository.findByUserId(id);
        commonService.isItFeel(id);
        Set<Cards> cardsList = cardRepository.findAllByTypeAndGoalOrBonus(CARD_TYPE,
                debitOffer.getGoal(),
                debitOffer.getBonus());

        return cardsList.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goal or Bonus doesn't match with any card")
                : ResponseEntity.ok(cardsList.stream().map(debitCardMapper::toDTO).toList());
    }

    public ResponseEntity<?> creatOffer(Long id, DebitOfferDTO debitOfferDTO) {

        ResponseEntity<?> offerCheckResponse = commonService.offerExistenceCheck(id, false, true);

        if (offerCheckResponse != null)
            return offerCheckResponse;

        commonService.isItFeel(id);

        DebitOffer debitOffer = DTOToDebitOffer(debitOfferDTO);
        debitOffer.setCard_user(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
        debitOffer.setUser_id(id);
        ExpertMessage expertMessage = new ExpertMessage();
        expertMessage.setName(debitOffer.getCard_user().getName());
        expertMessage.setSurname(debitOffer.getCard_user().getSurname());
        kafkaProducerService.sendMessage(expertMessage);
        return ResponseEntity.ok(debitOfferToDTO(debitRepository.save(debitOffer)));
    }

    public DebitOfferDTO debitOfferToDTO(DebitOffer debitOffer) {
        return debitOfferMapper.toDTO(debitOffer);
    }

    public DebitOffer DTOToDebitOffer(DebitOfferDTO debitOfferDTO) {
        return debitOfferMapper.toEntity(debitOfferDTO);
    }
}
