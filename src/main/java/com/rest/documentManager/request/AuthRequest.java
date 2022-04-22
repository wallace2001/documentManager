package com.rest.documentManager.request;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
public class AuthRequest {
    private String login;
    private String password;
}
