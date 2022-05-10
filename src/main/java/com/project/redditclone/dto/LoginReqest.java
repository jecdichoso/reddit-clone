package com.project.redditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Access;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginReqest {
    //This is a DTO
    private String email;
    private String password;
    private String username;
}
