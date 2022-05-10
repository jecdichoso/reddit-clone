package com.project.redditclone.controller;

import com.project.redditclone.dto.AuthenticationResponse;
import com.project.redditclone.dto.LoginRequest;
import com.project.redditclone.dto.RegisterRequest;
import com.project.redditclone.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest){
        //through this class we'll be transferring user data as part of the request body
        //we call this kind of classes as DTO
        authService.signup(registerRequest);
        return new ResponseEntity<>("Successfully Registered the User", HttpStatus.OK);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token){
        authService.verifyAccount(token);
        return new ResponseEntity<>("Activated the account successfully!", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest){ //this is just the same DTO we used for registerreq
        return authService.login(loginRequest);
    }

}
