package com.rest.documentManager.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CategoryInstance {

    private String instance;
    private Long id;

    public CategoryInstance(String instance, Long id) {
        this.instance = instance;
        this.id = id;
    }
}
