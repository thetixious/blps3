package com.blps.lab2.service;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.blps.lab2.dto.CreditCardDTO;
import com.blps.lab2.dto.CreditOfferDTO;
import com.blps.lab2.model.bankDB.Manager;
import com.blps.lab2.model.mainDB.CreditOffer;
import com.blps.lab2.repo.bank.ManagerRepository;
import com.blps.lab2.repo.main.CardRepository;
import com.blps.lab2.repo.main.CreditRepository;
import com.blps.lab2.utils.mapper.CreditCardMapper;
import com.blps.lab2.utils.mapper.CreditOfferMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApprovingService {
    private final CommonService commonService;
    private final CreditRepository creditRepository;
    private final CreditOfferMapper creditOfferMapper;
    private final CreditCardMapper creditCardMapper;
    private final UserTransactionImp utx;
    private final AtomikosDataSourceBean mainDataSource;
    private final AtomikosDataSourceBean bankDataSource;
    private final ManagerRepository managerRepository;
    private final CardRepository cardRepository;
    private final PlatformTransactionManager transactionManager;
    private List<CreditCardDTO> response = new ArrayList<>();


    public ApprovingService(CommonService commonService, CreditRepository creditRepository, CreditOfferMapper creditOfferMapper, CreditCardMapper creditCardMapper, UserTransactionImp utx, @Qualifier("mainDataSource") AtomikosDataSourceBean mainDataSource, @Qualifier("bankDataSource") AtomikosDataSourceBean bankDataSource, ManagerRepository managerRepository, CardRepository cardRepository, PlatformTransactionManager transactionManager) {
        this.commonService = commonService;
        this.creditRepository = creditRepository;
        this.creditOfferMapper = creditOfferMapper;
        this.creditCardMapper = creditCardMapper;
        this.utx = utx;
        this.mainDataSource = mainDataSource;
        this.bankDataSource = bankDataSource;
        this.managerRepository = managerRepository;
        this.cardRepository = cardRepository;
        this.transactionManager =transactionManager;
    }

    public ResponseEntity<?> getInfo(Long id) {

        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);
        ResponseEntity<?> offerCheckResponse = commonService.offerExistenceCheck(id, true, false);

        if (userCheckResponse != null)
            return userCheckResponse;

        if (offerCheckResponse != null)
            return offerCheckResponse;

        CreditOffer creditOffer = creditRepository.findByUserId(id);
        return ResponseEntity.status(HttpStatus.OK).body(creditOfferMapper.toDTO(creditOffer));
    }

    //doesn't work with transactions but work with) sukkaaaaaaaa
    @Transactional("transactionManager")
    public ResponseEntity<?> getResult(Long id, List<Long> cardsId) throws Exception {
        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);

        if (userCheckResponse != null) {
            return userCheckResponse;
        }

        CreditOffer creditOffer = creditRepository.findByUserId(id);
        if (creditOffer.getReady()) {
            return ResponseEntity.status(HttpStatus.OK).body("Credit offer уже закрыт");
        }

        List<CreditCardDTO> response = null;

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



//        response = creditOffer.getCards().stream()
//                .map(creditCardMapper::toDTO)
//                .collect(Collectors.toList());

        creditRepository.save(creditOffer);
        managerRepository.save(manager);




        return ResponseEntity.status(HttpStatus.MULTI_STATUS).body("");
    }
}
