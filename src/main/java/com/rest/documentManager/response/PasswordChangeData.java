package com.rest.documentManager.response;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PasswordChangeData {

    private String password;
    private String passwordCheck;

    private String codeVerify;
    private String email;
}
