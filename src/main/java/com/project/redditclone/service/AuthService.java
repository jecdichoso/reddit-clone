package com.project.redditclone.service;

import com.project.redditclone.dto.AuthenticationResponse;
import com.project.redditclone.dto.LoginRequest;
import com.project.redditclone.dto.RegisterRequest;
import com.project.redditclone.exceptions.SpringRedditException;
import com.project.redditclone.model.NotificationEmail;
import com.project.redditclone.model.User;
import com.project.redditclone.model.VerificationToken;
import com.project.redditclone.repository.UserRepository;
import com.project.redditclone.repository.VerificationTokenRepository;
import com.project.redditclone.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

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

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));

        //this is after setting up jwt mechanism -jecd
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        //if you wanna check if the user is logged in or not, you can look up the
        //security context for the authenticate object, if you find the obj, then
        //the user is logged in
        String token = jwtProvider.generateToken(authenticate);
        //this returns a string that is our authentication token
        //now we can send this token back to the user
        //to send this token, we're going to use a dto called authentication response -jecd
//        return new AuthenticationResponse(token, loginRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .username(loginRequest.getUsername())
                .build();

    }
}
