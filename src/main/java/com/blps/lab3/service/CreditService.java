package com.blps.lab3.service;

import com.blps.lab3.dto.CreditOfferDTO;
import com.blps.lab3.model.mainDB.Cards;
import com.blps.lab3.model.mainDB.CreditOffer;
import com.blps.lab3.model.mainDB.User;
import com.blps.lab3.repo.main.CardRepository;
import com.blps.lab3.repo.main.CreditRepository;
import com.blps.lab3.repo.main.UserRepository;
import com.blps.lab3.utils.Bonus;
import com.blps.lab3.utils.CardType;
import com.blps.lab3.utils.Goal;
import com.blps.lab3.utils.mapper.CreditCardMapper;
import com.blps.lab3.utils.mapper.CreditOfferMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CreditService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CreditOfferMapper creditOfferMapper;
    private final CreditCardMapper creditCardMapper;
    private final CreditRepository creditRepository;
    private final CommonService commonService;

    public CreditService(CardRepository cardRepository, UserRepository userRepository, CreditOfferMapper creditOfferMapper, CreditCardMapper creditCardMapper,
                         CreditRepository creditRepository, CommonService commonService) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.creditOfferMapper = creditOfferMapper;
        this.creditCardMapper = creditCardMapper;
        this.creditRepository = creditRepository;
        this.commonService = commonService;
    }

    public Set<Cards> getUncheckedCards(CreditOffer creditOffer) {
        Goal goal = creditOffer.getGoal();
        Bonus bonus = creditOffer.getBonus();
        return cardRepository.findAllByTypeAndGoalOrBonus(CardType.CREDIT, goal, bonus);

    }


    public ResponseEntity<?> getApprovedCards(Long id) {

        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);
        ResponseEntity<?> offerCheckResponse = commonService.offerExistenceCheck(id, true, false);

        if (userCheckResponse != null)
            return userCheckResponse;

        if (offerCheckResponse != null)
            return offerCheckResponse;

        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.get();
        if (!user.getIs_fill())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Сначала заполните профиль");


        CreditOffer creditOffer = creditRepository.findByUserId(id);
        if (!creditOffer.getReady())
            return ResponseEntity.status(HttpStatus.OK).body("Запрос в процессе обработки");

        return ResponseEntity.status(HttpStatus.OK).body(creditOffer.getCards().stream().map(creditCardMapper::toDTO).collect(Collectors.toList()));

    }


    public ResponseEntity<?> creatOffer(Long id, CreditOfferDTO creditOfferDTO) {
        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);
        ResponseEntity<?> offerCheckResponse = commonService.offerExistenceCheck(id, false, false);

        if (userCheckResponse != null)
            return userCheckResponse;

        if (offerCheckResponse != null)
            return offerCheckResponse;

        CreditOffer creditOffer = creditOfferMapper.toEntity(creditOfferDTO);
        creditOffer.setCard_user(userRepository.findById(id).get());
        creditOffer.setUser_id(id);
        creditOffer.setReady(false);
        creditOffer.setApproved(false);
        creditOffer.setCards(getUncheckedCards(creditOffer));
        creditOffer.setCredit_limit(creditOfferDTO.getCreditLimit());

        CreditOffer savedCreditOffer = creditRepository.save(creditOffer);
        return ResponseEntity.status(HttpStatus.OK).body(creditOfferMapper.toDTO(savedCreditOffer));
    }

    public ResponseEntity<?> getCards(Long id) {
        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);
        ResponseEntity<?> offerCheckResponse = commonService.offerExistenceCheck(id, true, false);

        if (userCheckResponse != null)
            return userCheckResponse;

        if (offerCheckResponse != null)
            return offerCheckResponse;

        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.get();
        if (!user.getIs_fill())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Сначала заполните профиль");

        CreditOffer creditOffer = creditRepository.findByUserId(id);
        Goal goal = creditOffer.getGoal();
        Bonus bonus = creditOffer.getBonus();
        Set<Cards> cardsList = cardRepository.findAllByTypeAndGoalOrBonus(CardType.CREDIT, goal, bonus);

        if (cardsList.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Нет карт с таким набором бонусов или целей");

        return ResponseEntity.ok(cardsList.stream().map(creditCardMapper::toDTO).toList());
    }

    public ResponseEntity<?> updateOfferByChosenCards(Long id, List<Long> cardsId) {

            CreditOffer creditOffer = creditRepository.findByUserId(id);
            creditOffer.setCards(cardRepository.findAllByIdIn(cardsId));
            creditRepository.save(creditOffer);
            creditOffer.setCard_user(userRepository.findById(id).get());
        CreditOfferDTO creditOfferDTO = creditOfferMapper.toDTO(creditOffer);
            return ResponseEntity.status(HttpStatus.OK).body(creditOfferDTO);

    }
}
