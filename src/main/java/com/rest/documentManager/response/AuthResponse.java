package com.rest.documentManager.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private Long id;
    private String name;
    private String email;
    private String username;
    private String token;

    public AuthResponse(Long id, String name, String email, String username, String token) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.token = token;
    }
}
