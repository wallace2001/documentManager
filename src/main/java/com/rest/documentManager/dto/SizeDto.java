package com.rest.documentManager.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class SizeDto {

    private List<String> instance;

}
