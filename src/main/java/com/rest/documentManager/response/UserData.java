package com.rest.documentManager.response;

import com.rest.documentManager.entity.Profile;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserData {

    private Long id;
    private String name;
    private String email;
    private List<Profile> profiles;
    private String token;
    private String username;

    public UserData(Long id, String name, String email, String token, String username, List<Profile> profiles) {
        this.id = id;
        this.profiles = profiles;
        this.name = name;
        this.email = email;
        this.token = token;
        this.username = username;
    }
}
