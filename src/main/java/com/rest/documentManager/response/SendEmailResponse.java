package com.rest.documentManager.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailResponse {

    private String email;
    private String title;
    private String description;
}
