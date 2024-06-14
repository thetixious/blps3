package com.blps.lab2.service;

import com.blps.lab2.dto.CreditOfferDTO;
import com.blps.lab2.model.bankDB.Manager;
import com.blps.lab2.model.mainDB.Cards;
import com.blps.lab2.model.mainDB.CreditOffer;
import com.blps.lab2.model.mainDB.User;
import com.blps.lab2.repo.bank.ManagerRepository;
import com.blps.lab2.repo.main.CardRepository;
import com.blps.lab2.repo.main.CreditRepository;
import com.blps.lab2.repo.main.UserRepository;
import com.blps.lab2.utils.Bonus;
import com.blps.lab2.utils.CardType;
import com.blps.lab2.utils.Goal;
import com.blps.lab2.utils.mapper.CreditCardMapper;
import com.blps.lab2.utils.mapper.CreditOfferMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private CreditOfferDTO creditOfferDTO;
    private final ManagerRepository managerRepository;

    public CreditService(CardRepository cardRepository, UserRepository userRepository, CreditOfferMapper creditOfferMapper, CreditCardMapper creditCardMapper,
                         CreditRepository creditRepository, CommonService commonService, @Qualifier("managerRepository") ManagerRepository managerRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.creditOfferMapper = creditOfferMapper;
        this.creditCardMapper = creditCardMapper;
        this.creditRepository = creditRepository;
        this.commonService = commonService;
        this.managerRepository = managerRepository;
    }

    public Set<Cards> getUncheckedCards(CreditOffer creditOffer) {
        Goal goal = creditOffer.getGoal();
        Bonus bonus = creditOffer.getBonus();
        return cardRepository.findAllByTypeAndGoalOrBonus(CardType.CREDIT, goal, bonus);

    }

    public CreditOfferDTO setOffer(CreditOfferDTO creditOfferDTO, Long id) {
        CreditOffer creditOffer = creditOfferMapper.toEntity(creditOfferDTO);
        creditOffer.setCard_user(userRepository.findById(id).get());
        creditOffer.setReady(false);
        creditOffer.setApproved(false);
        creditOffer.setCards(getUncheckedCards(creditOffer));
        creditOffer.setCredit_limit(creditOfferDTO.getCreditLimit());
        return creditOfferMapper.toDTO(creditRepository.saveAndFlush(creditOffer));

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

    @Transactional("transactionManager")
    public ResponseEntity<?> creatOffer(Long id, CreditOfferDTO creditOfferDTO) throws Exception {
        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);
        ResponseEntity<?> offerCheckResponse = commonService.offerExistenceCheck(id, false, false);

        if (userCheckResponse != null)
            return userCheckResponse;

        if (offerCheckResponse != null)
            return offerCheckResponse;

        CreditOffer creditOffer = creditOfferMapper.toEntity(creditOfferDTO);
        creditOffer.setCard_user(userRepository.findById(id).orElseThrow(() -> new Exception("User not found")));
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
            creditOfferDTO = creditOfferMapper.toDTO(creditOffer);
            return ResponseEntity.status(HttpStatus.OK).body(creditOfferDTO);



//
//        CreditOffer creditOffer = creditRepository.findByUserId(id);
//        creditOffer.setCards(cardRepository.findAllByIdIn(cardsId));
//        creditRepository.saveAndFlush(creditOffer);
//
//        Optional<Manager> managerOptional = managerRepository.findFirstByStatusFalse();
//        StringBuilder info = new StringBuilder();
//        info.append(creditOffer.getCard_user().getEmail());
//        info.append(creditOffer.getCredit_limit());
//        info.append(creditOffer.getCard_user().getPassport());
//        managerRepository.updateUserNameById(managerOptional.get().getId(), info.toString());
//
//
//        creditOfferDTO = creditOfferMapper.toDTO(creditOffer);
//        return ResponseEntity.status(HttpStatus.OK).body(creditOfferDTO);


    }


}
