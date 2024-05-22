package com.blps.lab2.service;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.blps.lab2.dto.UserDataDTO;
import com.blps.lab2.model.User;
import com.blps.lab2.repo.CreditRepository;
import com.blps.lab2.repo.DebitRepository;
import com.blps.lab2.repo.UserRepository;
import com.blps.lab2.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.UserTransaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Optional;

@Component
public class CommonService {
    @Value("${token.key}")
    private String jwtSigningKey;
    private final UserRepository userRepository;
    private final CreditRepository creditRepository;

    private final JwtService jwtService;
    private final DebitRepository debitRepository;
    private final UserTransaction utx;
    private final AtomikosDataSourceBean dataSource;
    private User user;

    public CommonService(UserRepository userRepository, CreditRepository creditRepository, JwtService jwtService,
                         DebitRepository debitRepository, UserTransaction utx, AtomikosDataSourceBean dataSource) {
        this.userRepository = userRepository;
        this.creditRepository = creditRepository;
        this.jwtService = jwtService;
        this.debitRepository = debitRepository;
        this.utx = utx;
        this.dataSource = dataSource;
    }

    public ResponseEntity<?> userCheck(Long id) {
        if (userRepository.findById(id).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Нет пользователя с данным аутентификатором");
        return null;
    }

    public ResponseEntity<?> offerExistenceCheck(Long id, boolean shouldExists, boolean flag) {
        if (!flag) {
            if (shouldExists && creditRepository.findByUserId(id) == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь еще не оставил запрос");
            } else if (!shouldExists && creditRepository.findByUserId(id) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь уже оставил запрос");
            }
        } else {
            if (shouldExists && debitRepository.findByUserId(id) == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь еще не оставил запрос");
            } else if (!shouldExists && debitRepository.findByUserId(id) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь уже оставил запрос");
            }
        }
        return null;
    }

    public ResponseEntity<?> toFillProfile(Long id, UserDataDTO userDataDTO) throws Exception {


        Optional<User> userOptional = userRepository.findById(id);
        boolean rollback = false;
        try {
            utx.begin();
            Connection connection = dataSource.getConnection();
            user = userOptional.get();
            user.setPassport(userDataDTO.getPassport());
            user.setSalary(userDataDTO.getSalary());
            user.setName(userDataDTO.getName());
            user.setSurname(userDataDTO.getSurname());
            user.setIs_fill(true);
            userRepository.save(user);
            connection.close();

        } catch (Exception e) {
            rollback = true;
        }
        finally {
            if (rollback) {
                utx.rollback();
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tx crashed");
            } else {
                utx.commit();
                return ResponseEntity.status(HttpStatus.OK).body(user);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    public Long extractIdFromJWT(String rHeader) {


        String jwtToken = rHeader.substring(7);
        Claims claims = jwtService.extractAllClaims(jwtToken);
        Long id = claims.get("id", Long.class);
        return id;
    }


}
