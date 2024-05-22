package com.blps.lab2.service;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.template.TransactionTemplate;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.blps.lab2.dto.CreditCardDTO;
import com.blps.lab2.model.CreditOffer;
import com.blps.lab2.repo.CreditRepository;
import com.blps.lab2.utils.mapper.CreditCardMapper;
import com.blps.lab2.utils.mapper.CreditOfferMapper;
import jakarta.transaction.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovingService {
    private final CommonService commonService;
    private final CreditRepository creditRepository;
    private final CreditOfferMapper creditOfferMapper;
    private final CreditCardMapper creditCardMapper;
    private final UserTransactionImp utx;
    private final AtomikosDataSourceBean dataSource;
    private List<CreditCardDTO> response = new ArrayList<>();


    public ApprovingService(CommonService commonService, CreditRepository creditRepository, CreditOfferMapper creditOfferMapper, CreditCardMapper creditCardMapper, UserTransactionImp utx, AtomikosDataSourceBean dataSource) {
        this.commonService = commonService;
        this.creditRepository = creditRepository;
        this.creditOfferMapper = creditOfferMapper;
        this.creditCardMapper = creditCardMapper;
        this.utx = utx;
        this.dataSource = dataSource;
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

    public ResponseEntity<?> getResult(Long id, List<Long> cardsId) throws Exception {
        ResponseEntity<?> userCheckResponse = commonService.userCheck(id);

        if (userCheckResponse != null)
            return userCheckResponse;
        CreditOffer creditOffer = creditRepository.findByUserId(id);
        if (creditOffer.getReady())
            return ResponseEntity.status(HttpStatus.OK).body("Credit offer уже закрыт");

        boolean rollback = true;
        try {
            utx.begin();
            Connection connection = dataSource.getConnection();
            creditOffer.getCards().removeIf(cards -> !cardsId.contains(cards.getId()));
            creditOffer.setApproved(!creditOffer.getCards().isEmpty());
            creditOffer.setReady(true);
            response = creditRepository.save(creditOffer).getCards().stream().map(creditCardMapper::toDTO).collect(Collectors.toList());
            connection.close();

        } catch (Exception e) {
            rollback = true;
            e.printStackTrace();
        } finally {
            if (rollback) {
                utx.rollback();
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tx crashed");
            } else {
                utx.commit();
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
    }

}
