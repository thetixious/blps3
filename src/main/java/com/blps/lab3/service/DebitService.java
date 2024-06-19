package com.blps.lab3.service;

import com.blps.lab3.dto.DebitOfferDTO;
import com.blps.lab3.model.mainDB.Cards;
import com.blps.lab3.model.mainDB.DebitOffer;
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
    private final CardRepository cardRepository;
    private final DebitOfferMapper debitOfferMapper;
    private final DebitCardMapper debitCardMapper;
    private final UserRepository userRepository;
    private final DebitRepository debitRepository;
    private final CommonService commonService;


    public DebitService(CardRepository cardRepository, DebitOfferMapper debitOfferMapper, DebitCardMapper debitCardMapper, UserRepository userRepository, DebitRepository debitRepository, CommonService commonService) {
        this.cardRepository = cardRepository;
        this.debitOfferMapper = debitOfferMapper;
        this.debitCardMapper = debitCardMapper;
        this.userRepository = userRepository;
        this.debitRepository = debitRepository;
        this.commonService = commonService;
    }


    public DebitOfferDTO debitOfferToDTO(DebitOffer debitOffer) {
        return debitOfferMapper.toDTO(debitOffer);
    }

    public DebitOffer DTOToDebitOffer(DebitOfferDTO debitOfferDTO) {
        return debitOfferMapper.toEntity(debitOfferDTO);
    }


    public ResponseEntity<?> getCards(Long id) {

        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);
        ResponseEntity<?> offerCheckResponse = commonService.offerExistenceCheck(id, true,true);

        if (userCheckResponse != null)
            return userCheckResponse;

        if (offerCheckResponse != null)
            return offerCheckResponse;

        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.get();
        if (!user.getIs_fill())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Сначала заполните профиль");

        DebitOffer debitOffer = debitRepository.findByUserId(id);
        Goal goal = debitOffer.getGoal();
        Bonus bonus = debitOffer.getBonus();
        Set<Cards> cardsList = cardRepository.findAllByTypeAndGoalOrBonus(CardType.DEBIT,goal, bonus);

        if (cardsList.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Нет карт с таким набором бонусов или целей");

        return ResponseEntity.ok(cardsList.stream().map(debitCardMapper::toDTO).toList());
    }

    public ResponseEntity<?> creatOffer(Long id, DebitOfferDTO debitOfferDTO) {

        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);
        ResponseEntity<?> offerCheckResponse = commonService.offerExistenceCheck(id, false, true);

        if (userCheckResponse != null)
            return userCheckResponse;

        if (offerCheckResponse != null)
            return offerCheckResponse;


        DebitOffer debitOffer = DTOToDebitOffer(debitOfferDTO);
        debitOffer.setCard_user(userRepository.findById(id).get());
        debitOffer.setUser_id(id);
        return ResponseEntity.ok(debitOfferToDTO(debitRepository.saveAndFlush(debitOffer)));
    }



}
