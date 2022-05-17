package com.rest.documentManager.dto;

import com.rest.documentManager.entity.Profile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class UserDto {

    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;

    private List<Profile> profiles;

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
