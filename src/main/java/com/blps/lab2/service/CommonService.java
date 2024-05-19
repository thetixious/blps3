package com.blps.lab2.service;

import com.blps.lab2.dto.UserDataDTO;
import com.blps.lab2.model.User;
import com.blps.lab2.repo.CreditRepository;
import com.blps.lab2.repo.DebitRepository;
import com.blps.lab2.repo.UserRepository;
import com.blps.lab2.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommonService {
    @Value("${token.key}")
    private String jwtSigningKey;
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

    public ResponseEntity<?> toFillProfile(Long id, UserDataDTO userDataDTO) {


        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.get();
        user.setPassport(userDataDTO.getPassport());
        user.setSalary(userDataDTO.getSalary());
        user.setName(userDataDTO.getName());
        user.setSurname(userDataDTO.getSurname());
        user.setIs_fill(true);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    public Long extractIdFromJWT(String rHeader) {


        String jwtToken = rHeader.substring(7);
        Claims claims = jwtService.extractAllClaims(jwtToken);
        Long id = claims.get("id", Long.class);
        return id;
    }


}
