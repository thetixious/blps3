package com.blps.lab3.service;

import com.blps.lab3.dto.CreditOfferDTO;
import com.blps.lab3.exception.customException.UserNotFoundByIdException;
import com.blps.lab3.model.mainDB.Cards;
import com.blps.lab3.model.mainDB.CreditOffer;
import com.blps.lab3.model.mainDB.User;
import com.blps.lab3.repo.main.CardRepository;
import com.blps.lab3.repo.main.CreditRepository;
import com.blps.lab3.repo.main.UserRepository;
import com.blps.lab3.utils.CardType;
import com.blps.lab3.utils.mapper.CreditCardMapper;
import com.blps.lab3.utils.mapper.CreditOfferMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
    Service class for user's credit-card operations
 */
@Service
public class CreditService {

    private final CardType CARD_TYPE = CardType.CREDIT;
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

        return cardRepository.findAllByTypeAndGoalOrBonus(CARD_TYPE,
                creditOffer.getGoal(), creditOffer.getBonus());

    }


    public ResponseEntity<?> getApprovedCards(Long id) {

        commonService.isItFeel(id);
        CreditOffer creditOffer = getCreditOfferIfItExist(id);

        return !creditOffer.getReady()
                ? ResponseEntity.status(HttpStatus.OK).body("Запрос в процессе обработки")
                : ResponseEntity.status(HttpStatus.OK).body(creditOffer.getCards().stream().
                map(creditCardMapper::toDTO).collect(Collectors.toList()));


    }

    /*
        Creates unapproved credit offer by user's initial parameters
     */
    public ResponseEntity<?> creatRowOffer(Long id, CreditOfferDTO creditOfferDTO) {

        checkCreditOfferDoesntExistBefore(id);
        CreditOffer creditOffer = creditOfferMapper.toEntity(creditOfferDTO);
        creditOffer.setCard_user(getUserIfItExist(id));
        creditOffer.setUser_id(id);
        creditOffer.setReady(false);
        creditOffer.setApproved(false);
        creditOffer.setCards(getUncheckedCards(creditOffer));
        creditOffer.setCredit_limit(creditOfferDTO.getCreditLimit());

        CreditOffer savedCreditOffer = creditRepository.save(creditOffer);
        return ResponseEntity.status(HttpStatus.OK).body(creditOfferMapper.toDTO(savedCreditOffer));
    }

    /*
        Get card witch haven't unapproved by admin yet
     */
    public ResponseEntity<?> getYetUnapprovedCards(Long id) {

        commonService.isItFeel(id);
        CreditOffer creditOffer = getCreditOfferIfItExist(id);

        Set<Cards> cardsList = cardRepository.findAllByTypeAndGoalOrBonus(CARD_TYPE,
                creditOffer.getGoal(), creditOffer.getBonus());

        return cardsList.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goal or Bonus doesn't match with any card")
                : ResponseEntity.ok(cardsList.stream().map(creditCardMapper::toDTO).toList());

    }

    /*
        Update
     */
    public ResponseEntity<?> updateOfferByChosenCards(Long id, List<Long> cardsId) {

        CreditOffer creditOffer = getCreditOfferIfItExist(id);
        creditOffer.setCards(cardRepository.findAllByIdIn(cardsId));
        creditRepository.save(creditOffer);
        creditOffer.setCard_user(getUserIfItExist(id));
        CreditOfferDTO creditOfferDTO = creditOfferMapper.toDTO(creditOffer);
        return ResponseEntity.status(HttpStatus.OK).body(creditOfferDTO);

    }

    private CreditOffer getCreditOfferIfItExist(Long id) {
        return creditRepository.findByUserId(id).orElseThrow(() ->
                new RuntimeException("Credit offer not found for user with ID: " + id));
    }

    private User getUserIfItExist(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundByIdException(id.toString()));
    }
    private void checkCreditOfferDoesntExistBefore(Long id){
        if (creditRepository.findByUserId(id).isPresent())
            throw new RuntimeException("offer already exist");
    }
}
