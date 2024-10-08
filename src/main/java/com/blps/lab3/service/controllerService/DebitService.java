package com.blps.lab3.service.controllerService;

import com.blps.lab3.dto.DebitOfferDTO;
import com.blps.lab3.exception.customException.OfferAlreadyExistException;
import com.blps.lab3.exception.customException.OfferNotFoundException;
import com.blps.lab3.exception.customException.UserNotFoundByIdException;
import com.blps.lab3.model.mainDB.Cards;
import com.blps.lab3.model.mainDB.DebitOffer;
import com.blps.lab3.repo.main.CardRepository;
import com.blps.lab3.repo.main.DebitRepository;
import com.blps.lab3.repo.main.UserRepository;
import com.blps.lab3.utils.CardType;
import com.blps.lab3.utils.mapper.DebitCardMapper;
import com.blps.lab3.utils.mapper.DebitOfferMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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


    public DebitService(CardRepository cardRepository, DebitOfferMapper debitOfferMapper, DebitCardMapper debitCardMapper, UserRepository userRepository, DebitRepository debitRepository, CommonService commonService) {
        this.cardRepository = cardRepository;
        this.debitOfferMapper = debitOfferMapper;
        this.debitCardMapper = debitCardMapper;
        this.userRepository = userRepository;
        this.debitRepository = debitRepository;
        this.commonService = commonService;
    }

    public ResponseEntity<?> getCards(Long id) {


        DebitOffer debitOffer = getDebitOfferIfItExist(id);
        commonService.isItFeel(id);
        Set<Cards> cardsList = cardRepository.findAllByTypeAndGoalOrBonus(CARD_TYPE,
                debitOffer.getGoal(),
                debitOffer.getBonus());

        return cardsList.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goal or Bonus doesn't match with any card")
                : ResponseEntity.ok(cardsList.stream().map(debitCardMapper::toDTO).toList());
    }

    public ResponseEntity<?> creatOffer(Long id, DebitOfferDTO debitOfferDTO) {

        checkCreditOfferDoesntExistBefore(id);
        commonService.isItFeel(id);

        DebitOffer debitOffer = DTOToDebitOffer(debitOfferDTO);
        debitOffer.setCard_user(userRepository.findById(id).orElseThrow(() -> new UserNotFoundByIdException(id.toString())));
        debitOffer.setUser_id(id);
        System.out.println(debitOffer.getUser_id());
        return ResponseEntity.ok(debitOfferToDTO(debitRepository.saveAndFlush(debitOffer)));
    }

    private DebitOffer getDebitOfferIfItExist(Long id) {
        return debitRepository.findByUserId(id).orElseThrow(OfferNotFoundException::new);
    }

    private void checkCreditOfferDoesntExistBefore(Long id) {
        if (debitRepository.findByUserId(id).isPresent())
            throw new OfferAlreadyExistException();
    }

    private DebitOfferDTO debitOfferToDTO(DebitOffer debitOffer) {
        return debitOfferMapper.toDTO(debitOffer);
    }

    private DebitOffer DTOToDebitOffer(DebitOfferDTO debitOfferDTO) {
        return debitOfferMapper.toEntity(debitOfferDTO);
    }
}
