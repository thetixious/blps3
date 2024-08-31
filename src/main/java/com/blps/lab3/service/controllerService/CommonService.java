package com.blps.lab3.service.controllerService;

import com.blps.lab3.dto.UserDataDTO;
import com.blps.lab3.exception.customException.NotFilledProfileException;
import com.blps.lab3.exception.customException.UserNotFoundByIdException;
import com.blps.lab3.model.mainDB.User;
import com.blps.lab3.repo.main.UserRepository;
import com.blps.lab3.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CommonService {

    private final UserRepository userRepository;
    private final JwtService jwtService;


    public CommonService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> toFillProfile(Long id, UserDataDTO userDataDTO) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundByIdException(id.toString()));
        user.setPassport(userDataDTO.getPassport());
        user.setSalary(userDataDTO.getSalary());
        user.setName(userDataDTO.getName());
        user.setSurname(userDataDTO.getSurname());
        user.setIs_fill(true);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
    public void isItFeel(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundByIdException(id.toString()));
        if (!user.getIs_fill())
            throw new NotFilledProfileException();

    }
    public Long extractIdFromJWT(String rHeader) {

        String jwtToken = rHeader.substring(7);
        Claims claims = jwtService.extractAllClaims(jwtToken);
        return  claims.get("id", Long.class);
    }
}
