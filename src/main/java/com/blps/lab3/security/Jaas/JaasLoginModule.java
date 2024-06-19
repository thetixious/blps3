package com.blps.lab3.security.Jaas;


import com.blps.lab3.model.mainDB.User;
import com.blps.lab3.repo.main.UserRepository;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

public class JaasLoginModule implements LoginModule {

    private CallbackHandler callbackHandler;
    private String username;
    private Boolean loginSucceeded = false;
    private Subject subject;
    private UserRepository userRepository;


    private PasswordEncoder passwordEncoder;




    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.callbackHandler = callbackHandler;
        this.subject = subject;
        this.userRepository = (UserRepository) options.get("userRepository");
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @SneakyThrows
    @Override
    public boolean login() throws LoginException {
        var nameCallback = new NameCallback("username");
        var passwordCallback = new PasswordCallback("password", false);
        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new RuntimeException(e);
        }
        username = nameCallback.getName();
        String password = new String(passwordCallback.getPassword());
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            loginSucceeded = passwordEncoder.matches(password, userOptional.get().getPassword());
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return loginSucceeded;

    }

    @Override
    public boolean commit() throws LoginException {
        if (!loginSucceeded) return false;
        if (username == null) throw new UsernameNotFoundException("username  is not specified");
        Principal principal = (UserPrincipal) () -> username;
        subject.getPrincipals().add(principal);
        return true;

    }

    @Override
    public boolean abort() throws LoginException {
        return false;
    }

    @Override
    public boolean logout() throws LoginException {
        return false;
    }
}
