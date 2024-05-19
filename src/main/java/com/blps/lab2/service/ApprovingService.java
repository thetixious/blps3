package com.blps.lab2.service;

import com.blps.lab2.model.CreditOffer;
import com.blps.lab2.repo.CreditRepository;
import com.blps.lab2.utils.mapper.CreditCardMapper;
import com.blps.lab2.utils.mapper.CreditOfferMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovingService {
    private final CommonService commonService;
    private final CreditRepository creditRepository;
    private final CreditOfferMapper creditOfferMapper;
    private final CreditCardMapper creditCardMapper;


    public ApprovingService(CommonService commonService, CreditRepository creditRepository, CreditOfferMapper creditOfferMapper, CreditCardMapper creditCardMapper) {
        this.commonService = commonService;
        this.creditRepository = creditRepository;
        this.creditOfferMapper = creditOfferMapper;
        this.creditCardMapper = creditCardMapper;
    }

    public ResponseEntity<?> getInfo(Long id){

        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);
        ResponseEntity<?> offerCheckResponse = commonService.offerExistenceCheck(id,true, false);

        if (userCheckResponse != null)
            return userCheckResponse;

        if (offerCheckResponse != null)
            return offerCheckResponse;

        CreditOffer creditOffer = creditRepository.findByUserId(id);
        return ResponseEntity.status(HttpStatus.OK).body(creditOfferMapper.toDTO(creditOffer));
//        return ResponseEntity.status(HttpStatus.OK).body(creditOffer);
    }

    public ResponseEntity<?> getResult(Long id, List<Long> cardsId){
        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);

        if (userCheckResponse != null)
            return userCheckResponse;


        CreditOffer creditOffer = creditRepository.findByUserId(id);

        if (creditOffer.getReady())
            return ResponseEntity.status(HttpStatus.OK).body("Credit offer уже закрыт");

        creditOffer.getCards().removeIf(cards -> !cardsId.contains(cards.getId()));
        creditOffer.setApproved(!creditOffer.getCards().isEmpty());
        creditOffer.setReady(true);

        return ResponseEntity.status(HttpStatus.OK).body(creditRepository.save(creditOffer).getCards().stream().
                map(creditCardMapper::toDTO).collect(Collectors.toList()));
    }
}
