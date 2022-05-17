package com.rest.documentManager.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesBoughtResponse {
    private String category;
    private Double totalPrice;
    private Integer totalBought;
}
