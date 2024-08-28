package com.blps.lab3.service;

import com.blps.lab3.dto.UserDataDTO;
import com.blps.lab3.model.mainDB.CreditOffer;
import com.blps.lab3.model.mainDB.User;
import com.blps.lab3.repo.main.CreditRepository;
import com.blps.lab3.repo.main.DebitRepository;
import com.blps.lab3.repo.main.UserRepository;
import com.blps.lab3.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class CommonService {

    private final UserRepository userRepository;
    private final CreditRepository creditRepository;
    private final JwtService jwtService;
    private final DebitRepository debitRepository;


    public CommonService(UserRepository userRepository, CreditRepository creditRepository, JwtService jwtService,
                         DebitRepository debitRepository) {
        this.userRepository = userRepository;
        this.creditRepository = creditRepository;
        this.jwtService = jwtService;
        this.debitRepository = debitRepository;
    }

    public CreditOffer getExistedCreditOffer(Long id) {
        CreditOffer creditOffer = creditRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Credit offer not found for user with ID: " + id));
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        creditOffer.setCard_user(user);
        return creditOffer;
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

    @Transactional("transactionManager")
    public ResponseEntity<?> toFillProfile(Long id, UserDataDTO userDataDTO) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOptional.get();
        user.setPassport(userDataDTO.getPassport());
        user.setSalary(userDataDTO.getSalary());
        user.setName(userDataDTO.getName());
        user.setSurname(userDataDTO.getSurname());
        user.setIs_fill(true);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
    public void isItFeel(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getIs_fill())
            throw  new RuntimeException("First of all fill the profile");

    }
    public Long extractIdFromJWT(String rHeader) {

        String jwtToken = rHeader.substring(7);
        Claims claims = jwtService.extractAllClaims(jwtToken);
        return  claims.get("id", Long.class);
    }
}
