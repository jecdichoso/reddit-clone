package com.project.redditclone.service;

import com.project.redditclone.dto.RegisterRequest;
import com.project.redditclone.exceptions.SpringRedditException;
import com.project.redditclone.model.NotificationEmail;
import com.project.redditclone.model.User;
import com.project.redditclone.model.VerificationToken;
import com.project.redditclone.repository.UserRepository;
import com.project.redditclone.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    //contains the business logic to register the users
    //creating the user object and saving it to the datbase
    //sending out activation emails


    //removed autowired here on the 2 private classes and declared final
    //this is because we are using field injection and spring recommends us to use
    //constructor injection whenever possible
    //also added allargscons on the class level to take care of the consturtors of the
    // 2 final classes-jecd

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    //transactional added cause we are interacting on the relational db -jecd
    @Transactional
    public void signup(RegisterRequest registerRequest){
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account via your email token", user.getEmail(),
                "Thank you for signing up to Novare Spring Reddit Clone," +
                        "please click on the below url to activate your account :" +
                        "http://localhost:8080/api/auth/accountVerification/"+token));
    }

    private String generateVerificationToken(User user) {
        //this will generate a unique and random 128bit value for us to use as token -jecd
        String token = UUID.randomUUID().toString();
        VerificationToken VerificationToken = new VerificationToken();
        VerificationToken.setToken(token);
        VerificationToken.setUser(user);

        verificationTokenRepository.save(VerificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verficationToken = verificationTokenRepository.findByToken(token);
        verficationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));
        fetchUserAndEnable(verficationToken.get());
    }


    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(()->new SpringRedditException("User with username: "+username+" does not Exist"));
        user.setEnabled(true);
        userRepository.save(user);
    }
}
