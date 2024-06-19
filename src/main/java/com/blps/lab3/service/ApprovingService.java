package com.blps.lab3.service;

import com.blps.lab3.dto.CreditCardDTO;
import com.blps.lab3.model.bankDB.Manager;
import com.blps.lab3.model.mainDB.CreditOffer;
import com.blps.lab3.repo.bank.ManagerRepository;
import com.blps.lab3.repo.main.CreditRepository;
import com.blps.lab3.repo.main.UserRepository;
import com.blps.lab3.utils.mapper.CreditCardMapper;
import com.blps.lab3.utils.mapper.CreditOfferMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApprovingService {
    private final CommonService commonService;
    private final CreditRepository creditRepository;
    private final UserRepository userRepository;
    private final CreditOfferMapper creditOfferMapper;
    private final CreditCardMapper creditCardMapper;
    private TransactionTemplate transactionTemplate;
    private final ManagerRepository managerRepository;
    private final PlatformTransactionManager transactionManager;
    private List<CreditCardDTO> response = new ArrayList<>();


    public ApprovingService(CommonService commonService, CreditRepository creditRepository, UserRepository userRepository, CreditOfferMapper creditOfferMapper, CreditCardMapper creditCardMapper, ManagerRepository managerRepository, PlatformTransactionManager transactionManager) {
        this.commonService = commonService;
        this.creditRepository = creditRepository;
        this.userRepository = userRepository;
        this.creditOfferMapper = creditOfferMapper;
        this.creditCardMapper = creditCardMapper;
        this.managerRepository = managerRepository;
        this.transactionManager = transactionManager;
    }

    public ResponseEntity<?> getInfo(Long id) {

        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);
        ResponseEntity<?> offerCheckResponse = commonService.offerExistenceCheck(id, true, false);

        if (userCheckResponse != null)
            return userCheckResponse;

        if (offerCheckResponse != null)
            return offerCheckResponse;

        CreditOffer creditOffer = creditRepository.findByUserId(id);
        creditOffer.setCard_user(userRepository.findById(id).get());
        return ResponseEntity.status(HttpStatus.OK).body(creditOfferMapper.toDTO(creditOffer));
    }

    public ResponseEntity<?> getResult(Long id, List<Long> cardsId) throws Exception {

        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);

        if (userCheckResponse != null) {
            return userCheckResponse;
        }

        CreditOffer creditOffer = creditRepository.findByUserId(id);
        creditOffer.setCard_user(userRepository.findById(id).get());
        if (creditOffer.getReady()) {
            return ResponseEntity.status(HttpStatus.OK).body("Credit offer уже закрыт");
        }
        transactionTemplate = new TransactionTemplate(transactionManager);
        try {
            transactionTemplate.execute(status -> {

                creditOffer.getCards().removeIf(card -> !cardsId.contains(card.getId()));
                creditOffer.setApproved(!creditOffer.getCards().isEmpty());
                creditOffer.setReady(true);
                Optional<Manager> managerOptional = managerRepository.findFirstByStatusFalse();
                StringBuilder info = new StringBuilder();
                info.append(creditOffer.getCard_user().getEmail());
                info.append(creditOffer.getCredit_limit());
                info.append(creditOffer.getCard_user().getPassport());
                System.out.println(info);
                Manager manager = managerOptional.get();
                manager.setData(info.toString());
                manager.setStatus(true);


                response = creditOffer.getCards().stream()
                        .map(creditCardMapper::toDTO)
                        .collect(Collectors.toList());

                creditRepository.save(creditOffer);
                managerRepository.save(manager);
                return response;
            });

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);
    }
}
