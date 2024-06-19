package com.blps.lab3.security.Jaas;

import com.blps.lab3.model.mainDB.User;
import com.blps.lab3.repo.main.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
public class AuthorityGranterImpl implements AuthorityGranter {

    private final UserRepository userRepository;
    @Override
    public Set<String> grant(Principal principal) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());
        if (optionalUser.isPresent())
            return Collections.singleton(optionalUser.get().getRole().name());
        throw new UsernameNotFoundException("wrong credentials");
    }
}
